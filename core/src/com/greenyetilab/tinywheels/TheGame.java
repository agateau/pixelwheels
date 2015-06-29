package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.greenyetilab.utils.Assert;

import java.util.Stack;

/**
 * The game
 */
public class TheGame extends Game {
    private Assets mAssets;
    private Stack<Screen> mScreenStack = new Stack<Screen>();
    private Maestro mMaestro;

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

    public void showOnePlayer() {
        mMaestro = new OnePlayerMaestro(this);
        mMaestro.start();
    }

    public void showMultiPlayer() {
        mMaestro = new MultiPlayerMaestro(this);
        mMaestro.start();
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
