package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Screen;

/**
 * Handle a multi player game
 */
public class MultiPlayerMaestro implements Maestro {
    private final TheGame mGame;
    private final GameInfo mGameInfo = new GameInfo();

    public MultiPlayerMaestro(TheGame game) {
        mGame = game;
    }

    @Override
    public void actionTriggered(String action) {
        String current = mGame.getScreen().getClass().getSimpleName();
        if (current.equals("MultiPlayerScreen")) {
            if (action.equals("next")) {
                mGame.replaceScreen(new SelectMapScreen(mGame, this, mGameInfo));
            } else if (action.equals("back")) {
                mGame.showMainMenu();
            }
        } else if (current.equals("SelectMapScreen")) {
            if (action.equals("next")) {
                mGame.replaceScreen(new RaceScreen(mGame, this, mGameInfo));
            } else if (action.equals("back")) {
                mGame.replaceScreen(new MultiPlayerScreen(mGame, this, mGameInfo));
            }
        } else if (current.equals("RaceScreen")) {
            if (action.equals("restart")) {
                mGame.replaceScreen(new RaceScreen(mGame, this, mGameInfo));
            } else if (action.equals("quit")) {
                mGame.showMainMenu();
            }
        }
    }

    @Override
    public void start() {
        mGame.replaceScreen(new MultiPlayerScreen(mGame, this, mGameInfo));
    }
}
