package com.greenyetilab.race;

import com.badlogic.gdx.Game;
import com.greenyetilab.race.RaceGameScreen;

/**
 * Created by aurelien on 21/11/14.
 */
public class RaceGame extends Game {
    @Override
    public void create() {
        RaceGameScreen screen = new RaceGameScreen();
        setScreen(screen);
    }
}
