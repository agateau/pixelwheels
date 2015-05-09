package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.greenyetilab.utils.Assert;

import java.util.Stack;

/**
 * The game
 */
public class RaceGame extends Game {
    private Assets mAssets;
    private Stack<Screen> mScreenStack = new Stack<Screen>();

    public Assets getAssets() {
        return mAssets;
    }

    @Override
    public void create() {
        GamePlay.instance.load();
        mAssets = new Assets();
        Box2D.init();
        showMainMenu();
    }

    public void showMainMenu() {
        Screen screen = new MainMenuScreen(this);
        replaceScreen(screen);
    }

    public void start() {
        TiledMap map = new AtlasTmxMapLoader().load("maps/race.tmx");
        MapInfo mapInfo = new MapInfo(map);
        Screen screen = new RaceGameScreen(this, mapInfo);
        replaceScreen(screen);
    }

    public void replaceScreen(Screen screen) {
        if (!mScreenStack.isEmpty()) {
            mScreenStack.pop().dispose();
        }
        pushScreen(screen);
    }

    public static Preferences getPreferences() {
        return Gdx.app.getPreferences("com.greenyetilab.tinywheels");
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
}
