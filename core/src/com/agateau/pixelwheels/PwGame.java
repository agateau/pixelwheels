/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels;

import com.agateau.pixelwheels.debug.Debug;
import com.agateau.pixelwheels.gamesetup.ChampionshipGameInfo;
import com.agateau.pixelwheels.gamesetup.ChampionshipMaestro;
import com.agateau.pixelwheels.gamesetup.Maestro;
import com.agateau.pixelwheels.gamesetup.PlayerCount;
import com.agateau.pixelwheels.gamesetup.QuickRaceMaestro;
import com.agateau.pixelwheels.rewards.RewardManager;
import com.agateau.pixelwheels.screens.MainMenuScreen;
import com.agateau.pixelwheels.screens.PwStageScreen;
import com.agateau.pixelwheels.screens.UnlockedRewardScreen;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.sound.DefaultAudioManager;
import com.agateau.pixelwheels.sound.SoundSettings;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.stats.GameStatsImpl;
import com.agateau.pixelwheels.stats.JsonGameStatsImplIO;
import com.agateau.ui.MouseCursorManager;
import com.agateau.ui.ScreenStack;
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.agateau.utils.Introspector;
import com.agateau.utils.PlatformUtils;
import com.agateau.utils.ScreenshotCreator;
import com.agateau.utils.log.NLog;
import com.agateau.utils.log.NLogGdxApplicationLogger;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.Box2D;

/** The game */
public class PwGame extends Game implements GameConfig.ChangeListener {
    private Assets mAssets;
    private final ScreenStack mScreenStack = new ScreenStack(this);
    private Maestro mMaestro;
    private GameConfig mGameConfig;
    private AudioManager mAudioManager;

    private Introspector mGamePlayIntrospector;
    private Introspector mDebugIntrospector;
    private Introspector mSoundSettingsIntrospector;
    private GameStatsImpl mGameStats;
    private RewardManager mRewardManager;

    private GameStatsImpl.IO mNormalGameStatsIO;
    // Used when GamePlay has been modified, to ensure stats are not recorded
    private final GameStatsImpl.IO mNoSaveGameStatsIO =
            new GameStatsImpl.IO() {
                @Override
                public void load(GameStatsImpl gameStats) {
                    mNormalGameStatsIO.load(gameStats);
                }

                @Override
                public void save(GameStatsImpl gameStats) {}
            };

    private LogExporter mLogExporter;
    private String mCurrentLanguageId = "";
    private String mExtraOsInformation = "";

    public Assets getAssets() {
        return mAssets;
    }

    public AudioManager getAudioManager() {
        return mAudioManager;
    }

    public RewardManager getRewardManager() {
        return mRewardManager;
    }

    private static Introspector createIntrospector(Object instance, String fileName) {
        FileHandle handle = FileUtils.getUserWritableFile(fileName);
        Introspector introspector = Introspector.fromInstance(instance, handle);
        introspector.load();
        return introspector;
    }

    /** If a log exporter has been defined, the game can use it in the config screen */
    public LogExporter getLogExporter() {
        return mLogExporter;
    }

    public void setLogExporter(LogExporter logExporter) {
        mLogExporter = logExporter;
    }

    @Override
    public void create() {
        // Installing the GdxApplicationLogger must be done only now because it requires
        // Gdx.app to be initialized
        NLogGdxApplicationLogger.install();

        logStartup();

        setupExtraAssetsDir();

        mGamePlayIntrospector = createIntrospector(GamePlay.instance, "gameplay.xml");
        mDebugIntrospector = createIntrospector(Debug.instance, "debug.xml");
        mSoundSettingsIntrospector = createIntrospector(SoundSettings.instance, "sound.xml");

        mGamePlayIntrospector.addListener(this::updateGameStatsIO);

        mAssets = new Assets();
        mAudioManager = new DefaultAudioManager(mAssets);
        setupCursorManager();
        setupConfig();
        setupTrackStats();
        setupRewardManager();
        Box2D.init();
        setupDisplay();
        showMainMenu();
    }

    private void setupExtraAssetsDir() {
        String assetsDir = System.getenv("PW_ASSETS_DIR");
        if (assetsDir != null) {
            FileUtils.setExtraAssetsDir(assetsDir);
        }
    }

    private void logStartup() {
        NLog.i("-------------------------------------------");
        NLog.i("Pixel Wheels: version='%s'", VersionInfo.VERSION);
        NLog.i(
                "Java: vendor='%s' version='%s'",
                System.getProperty("java.vendor"), System.getProperty("java.version"));
        NLog.i(
                "OS: name='%s' version='%s' arch='%s'",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch"));
        if (!mExtraOsInformation.isEmpty()) {
            NLog.i(mExtraOsInformation);
        }
    }

    private void loadTranslations() {
        NLog.i("Loading translations for language '%s'", mGameConfig.languageId);
        boolean firstCall = mCurrentLanguageId.equals("");
        mAssets.setLanguage(mGameConfig.languageId);
        if (!firstCall) {
            setupRewardManager();
        }
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            String path = ScreenshotCreator.saveScreenshot();
            NLog.i("Screenshot saved in %s", path);
        }
        MouseCursorManager.getInstance().act();
        super.render();
    }

    public void refreshAssets() {
        mAssets = new Assets();
        mAssets.setLanguage(mGameConfig.languageId);
        // Tracks and championship have been recreated, need to recreate reward manager
        setupRewardManager();
        setupCursorManager();
    }

    private void setupCursorManager() {
        MouseCursorManager.getInstance().setCursorPixmap(FileUtils.assets(Assets.CURSOR_FILENAME));
    }

    private void setupConfig() {
        mGameConfig = new GameConfig();
        if (mGameConfig.languageId.isEmpty()) {
            mGameConfig.languageId = mAssets.languages.findBestLanguageId();
        }
        mGameConfig.addListener(this);
        onGameConfigChanged();
    }

    private void setupTrackStats() {
        mNormalGameStatsIO =
                new JsonGameStatsImplIO(FileUtils.getUserWritableFile("gamestats.json"));
        mGameStats = new GameStatsImpl(getCurrentGameStatsIO());
        GameStatsSetup.loadDefaultRecords(mGameStats, mAssets.championships);
    }

    private GameStatsImpl.IO getCurrentGameStatsIO() {
        return mGamePlayIntrospector.hasBeenModified() ? mNoSaveGameStatsIO : mNormalGameStatsIO;
    }

    private void updateGameStatsIO() {
        mGameStats.setIO(getCurrentGameStatsIO());
    }

    private void setupRewardManager() {
        Assert.check(mGameStats != null, "GameStats must be instantiated first");
        Assert.check(mAssets != null, "Assets must be instantiated first");
        mRewardManager = new RewardManager(mGameStats);
        RewardManagerSetup.createChampionshipRules(mRewardManager, mAssets.championships);
        RewardManagerSetup.createVehicleRules(mRewardManager, mAssets);
        mRewardManager.markAllUnlockedRewardsSeen();
    }

    public void showMainMenu() {
        mScreenStack.clear();
        mAudioManager.playMusic(Assets.MENU_MUSIC_ID);
        Screen screen;
        if (Constants.DEBUG_SCREEN.startsWith("Unlocked")) {
            screen = UnlockedRewardScreen.createDebugScreen(this);
        } else {
            screen = new MainMenuScreen(this);
        }
        mScreenStack.push(screen);
    }

    public void showQuickRace(PlayerCount playerCount) {
        mMaestro = new QuickRaceMaestro(this, playerCount);
        mMaestro.start();
    }

    public void showChampionship(PlayerCount playerCount) {
        mMaestro = new ChampionshipMaestro(this, playerCount);
        mMaestro.start();
    }

    public void replaceScreen(Screen screen) {
        mScreenStack.replace(screen);
    }

    public GameConfig getConfig() {
        return mGameConfig;
    }

    public GameStats getGameStats() {
        return mGameStats;
    }

    public Maestro getMaestro() {
        return mMaestro;
    }

    public Introspector getGamePlayIntrospector() {
        return mGamePlayIntrospector;
    }

    public Introspector getDebugIntrospector() {
        return mDebugIntrospector;
    }

    public Introspector getSoundSettingsIntrospector() {
        return mSoundSettingsIntrospector;
    }

    public ScreenStack getScreenStack() {
        return mScreenStack;
    }

    public void pushScreen(Screen screen) {
        mScreenStack.push(screen);
    }

    public void popScreen() {
        mScreenStack.pop();
    }

    private void setupDisplay() {
        setFullscreen(mGameConfig.fullscreen);
    }

    public void setFullscreen(boolean fullscreen) {
        if (!PlatformUtils.isDesktop()) {
            return;
        }
        if (fullscreen) {
            Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(mode);
        } else {
            Gdx.graphics.setWindowedMode(PwStageScreen.WIDTH, PwStageScreen.HEIGHT);
        }
    }

    public void onChampionshipFinished(ChampionshipGameInfo gameInfo) {
        mGameStats.onChampionshipFinished(gameInfo.getChampionship(), gameInfo.getBestRank());
    }

    @Override
    public void onGameConfigChanged() {
        mAudioManager.setSoundFxMuted(!mGameConfig.playSoundFx);
        mAudioManager.setMusicMuted(!mGameConfig.playMusic);
        if (!mGameConfig.languageId.equals(mCurrentLanguageId)) {
            loadTranslations();
            mCurrentLanguageId = mGameConfig.languageId;
        }
    }

    public void setExtraOsInformation(String osInformation) {
        mExtraOsInformation = osInformation;
    }
}
