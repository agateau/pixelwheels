package com.greenyetilab.race;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Stack;

/**
 * Created by aurelien on 21/11/14.
 */
public class RaceGame extends Game {
    private Assets mAssets;
    private Stack<Screen> mScreenStack = new Stack<Screen>();
    private MapCreator mMapCreator = new MapCreator();

    public Assets getAssets() {
        return mAssets;
    }

    @Override
    public void create() {
        mAssets = new Assets();
        Box2D.init();
        mMapCreator.addSourceMap(new AtlasTmxMapLoader().load("maps/straight_single_single.tmx"));
        mMapCreator.addSourceMap(new AtlasTmxMapLoader().load("maps/cross_single_single.tmx"));
        mMapCreator.addSourceMap(new AtlasTmxMapLoader().load("maps/right-left_single_single.tmx"));
        mMapCreator.addSourceMap(new AtlasTmxMapLoader().load("maps/curve_single_single.tmx"));
        mMapCreator.addSourceMap(new AtlasTmxMapLoader().load("maps/shrink_single_single.tmx"));
        mMapCreator.addSourceMap(new AtlasTmxMapLoader().load("maps/split_single_single.tmx"));
        showMainMenu();
    }

    public void showMainMenu() {
        Screen screen = new MainMenuScreen(this);
        setScreenAndDispose(screen);
    }

    public void start() {
        TiledMap map = mMapCreator.run(20);
        MapInfo mapInfo = new MapInfo(map);
        Screen screen = new RaceGameScreen(this, mapInfo);
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
