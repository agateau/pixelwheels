package com.greenyetilab.race;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.greenyetilab.utils.log.NLog;

/**
 * Created by aurelien on 21/11/14.
 */
public class RaceGame extends Game {
    private Assets mAssets;

    public Assets getAssets() {
        return mAssets;
    }

    @Override
    public void create() {
        mAssets = new Assets();
        Screen screen = new MainMenuScreen(this);
        setScreen(screen);
    }

    public void start(String mapName) {
        NLog.i("mapName=%s", mapName);
        Screen screen = new RaceGameScreen(this, mapName);
        setScreenAndDispose(screen);
    }

    private void setScreenAndDispose(Screen screen) {
        Screen oldScreen = getScreen();
        if (oldScreen != null) {
            oldScreen.dispose();
        }
        setScreen(screen);
    }

    public void showGameOverOverlay() {

    }

    public void showFinishedOverlay(float duration) {

    }
}
