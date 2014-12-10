package com.greenyetilab.race;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.ScreenUtils;
import com.greenyetilab.utils.log.NLog;

import java.util.Stack;

/**
 * Created by aurelien on 21/11/14.
 */
public class RaceGame extends Game {
    private Assets mAssets;
    private Stack<Screen> mScreenStack = new Stack<Screen>();

    public Assets getAssets() {
        return mAssets;
    }

    @Override
    public void create() {
        mAssets = new Assets();
        Box2D.init();
        showMainMenu();
    }

    public void showMainMenu() {
        Screen screen = new MainMenuScreen(this);
        setScreenAndDispose(screen);
    }

    public void start() {
        MapInfo mapInfo = mAssets.mapInfoList.first();
        Screen screen = new RaceGameScreen(this, mapInfo.getMap());
        setScreenAndDispose(screen);
    }

    private void setScreenAndDispose(Screen screen) {
        if (!mScreenStack.isEmpty()) {
            mScreenStack.pop().dispose();
        }
        pushScreen(screen);
    }

    public void showGameOverOverlay() {
        showOverlay("Game Over");
    }

    private void showOverlay(String text) {
        TextureRegion bg = ScreenUtils.getFrameBufferTexture();
        setScreenAndDispose(new OverlayScreen(this, bg, text));
    }

    public static Preferences getPreferences() {
        return Gdx.app.getPreferences("com.greenyetilab.race");
    }

    public void pushScreen(Screen screen) {
        mScreenStack.push(screen);
        setScreen(screen);
    }

    public void popScreen() {
        assert !mScreenStack.isEmpty();
        mScreenStack.pop().dispose();
        assert !mScreenStack.isEmpty();
        setScreen(mScreenStack.peek());
    }
}
