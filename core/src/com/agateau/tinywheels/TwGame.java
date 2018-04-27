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
package com.agateau.tinywheels;

import com.agateau.tinywheels.debug.Debug;
import com.agateau.tinywheels.screens.MainMenuScreen;
import com.agateau.tinywheels.screens.TwStageScreen;
import com.agateau.tinywheels.sound.AudioManager;
import com.agateau.tinywheels.sound.DefaultAudioManager;
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.agateau.utils.Introspector;
import com.agateau.utils.PlatformUtils;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.physics.box2d.Box2D;

import java.util.Stack;

/**
 * The game
 */
public class TwGame extends Game {
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

    public void showQuickRace(GameMode gameMode) {
        mMaestro = new QuickRaceMaestro(this, gameMode);
        mMaestro.start();
    }

    public void showChampionship(GameMode gameMode) {
        mMaestro = new ChampionshipMaestro(this, gameMode);
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
            Gdx.graphics.setWindowedMode(TwStageScreen.WIDTH, TwStageScreen.HEIGHT);
        }
    }
}
