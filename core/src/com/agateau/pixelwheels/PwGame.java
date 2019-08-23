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
import com.agateau.pixelwheels.screens.MouseCursorManager;
import com.agateau.pixelwheels.screens.PwStageScreen;
import com.agateau.pixelwheels.screens.UnlockedRewardScreen;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.sound.DefaultAudioManager;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.stats.GameStatsImpl;
import com.agateau.pixelwheels.stats.JsonGameStatsImplIO;
import com.agateau.ui.ScreenStack;
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.agateau.utils.Introspector;
import com.agateau.utils.PlatformUtils;
import com.agateau.utils.ScreenshotCreator;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.Box2D;

/**
 * The game
 */
public class PwGame extends Game implements GameConfig.ChangeListener {
    private Assets mAssets;
    private final ScreenStack mScreenStack = new ScreenStack(this);
    private Maestro mMaestro;
    private GameConfig mGameConfig;
    private final AudioManager mAudioManager = new DefaultAudioManager();
    private MouseCursorManager mMouseCursorManager;

    private Introspector mGamePlayIntrospector;
    private Introspector mDebugIntrospector;
    private GameStats mGameStats;
    private RewardManager mRewardManager;

    public Assets getAssets() {
        return mAssets;
    }

    public AudioManager getAudioManager() {
        return mAudioManager;
    }

    public RewardManager getRewardManager() {
        return mRewardManager;
    }

    @Override
    public void create() {
        mGamePlayIntrospector = new Introspector(GamePlay.instance, new GamePlay(),
                FileUtils.getUserWritableFile("gameplay.xml"));
        mDebugIntrospector = new Introspector(Debug.instance, new Debug(),
                FileUtils.getUserWritableFile("debug.xml"));

        mGamePlayIntrospector.load();
        mDebugIntrospector.load();

        mAssets = new Assets();
        mMouseCursorManager = new MouseCursorManager();
        setupConfig();
        setupTrackStats();
        setupRewardManager();
        Box2D.init();
        setupDisplay();
        showMainMenu();
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            String path = ScreenshotCreator.saveScreenshot();
            NLog.i("Screenshot saved in %s", path);
        }
        mMouseCursorManager.act();
        super.render();
    }

    void refreshAssets() {
        mAssets = new Assets();
        // Tracks and championship have been recreated, need to recreate reward manager
        setupRewardManager();
        mMouseCursorManager.refreshAssets();
    }

    private void setupConfig() {
        mGameConfig = new GameConfig();
        mGameConfig.addListener(this);
        onGameConfigChanged();
    }

    private void setupTrackStats() {
        JsonGameStatsImplIO io = new JsonGameStatsImplIO(FileUtils.getUserWritableFile("gamestats.json"));
        mGameStats = new GameStatsImpl(io);
    }

    private void setupRewardManager() {
        Assert.check(mGameStats != null, "GameStats must be instantiated first");
        Assert.check(mAssets != null, "Assets must be instantiated first");
        mRewardManager = new RewardManager(mGameStats, mAssets.championships);
        RewardManagerSetup.createChampionshipRules(mRewardManager, mAssets.championships);
        RewardManagerSetup.createVehicleRules(mRewardManager, mAssets);
    }

    public void showMainMenu() {
        mScreenStack.clear();
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

    public Introspector getGamePlayIntrospector() {
        return mGamePlayIntrospector;
    }

    public Introspector getDebugIntrospector() {
        return mDebugIntrospector;
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
        mAudioManager.setMuted(!mGameConfig.audio);
    }
}
