/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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
import com.agateau.pixelwheels.gameinput.GameInputHandler;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactories;
import com.agateau.pixelwheels.gamesetup.ChampionshipMaestro;
import com.agateau.pixelwheels.gamesetup.Maestro;
import com.agateau.pixelwheels.gamesetup.PlayerCount;
import com.agateau.pixelwheels.gamesetup.QuickRaceMaestro;
import com.agateau.pixelwheels.screens.MainMenuScreen;
import com.agateau.pixelwheels.screens.PwStageScreen;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.sound.DefaultAudioManager;
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.agateau.utils.Introspector;
import com.agateau.utils.PlatformUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.Array;

import java.util.Map;
import java.util.Stack;

/**
 * The game
 */
public class PwGame extends Game {
    private Assets mAssets;
    private Stack<Screen> mScreenStack = new Stack<Screen>();
    private Maestro mMaestro;
    private GameConfig mGameConfig;
    private AudioManager mAudioManager = new DefaultAudioManager();

    private Introspector mGamePlayIntrospector;
    private Introspector mDebugIntrospector;

    public Assets getAssets() {
        return mAssets;
    }

    public AudioManager getAudioManager() {
        return mAudioManager;
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
        mGameConfig = new GameConfig();
        mAudioManager.setMuted(!mGameConfig.audio);
        Box2D.init();
        hideMouseCursor();
        setupDisplay();
        showMainMenu();
    }

    public void showMainMenu() {
        Screen screen = new MainMenuScreen(this);
        replaceScreen(screen);
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
        if (!mScreenStack.isEmpty()) {
            mScreenStack.pop().dispose();
        }
        pushScreen(screen);
    }

    public GameConfig getConfig() {
        return mGameConfig;
    }

    public Introspector getGamePlayIntrospector() {
        return mGamePlayIntrospector;
    }

    public Introspector getDebugIntrospector() {
        return mDebugIntrospector;
    }

    public void pushScreen(Screen screen) {
        mScreenStack.push(screen);
        setScreen(screen);
    }

    public void popScreen() {
        Assert.check(!mScreenStack.isEmpty(), "mScreenStack is empty");
        mScreenStack.pop().dispose();
        Assert.check(!mScreenStack.isEmpty(), "mScreenStack is empty");
        setScreen(mScreenStack.peek());
    }

    private void hideMouseCursor() {
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        Cursor cursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        if (cursor != null) {
            Gdx.graphics.setCursor(cursor);
        }
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

    /**
     * Returns true if devices for all players are available
     */
    public boolean checkInputHandlers(int playerCount) {
        return getPlayerInputHandlers(playerCount) != null;
    }

    public Array<GameInputHandler> getPlayerInputHandlers(int playerCount) {
        Array<GameInputHandler> playerInputHandlers = new Array<GameInputHandler>();
        Map<String, Array<GameInputHandler>> inputHandlersByIds = GameInputHandlerFactories.getInputHandlersByIds();
        for (int idx = 0; idx < playerCount; ++idx) {
            Assert.check(idx < mGameConfig.inputs.length, "Not enough inputs for all players");
            String id = mGameConfig.inputs[idx];
            Array<GameInputHandler> inputHandlers = inputHandlersByIds.get(id);
            if (inputHandlers == null) {
                NLog.e("No input handlers for id '%s'", id);
                return null;
            }
            if (inputHandlers.size == 0) {
                NLog.i("Not enough input handlers for id '%s'", id);
                return null;
            } else {
                GameInputHandler inputHandler = inputHandlers.first();
                inputHandler.loadConfig(mGameConfig.getPreferences(), mGameConfig.getInputPrefix(idx));
                playerInputHandlers.add(inputHandler);
                inputHandlers.removeIndex(0);
            }
        }
        return playerInputHandlers;
    }
}
