package com.greenyetilab.tinywheels;

/**
 * Handle a multi player game
 */
public class MultiPlayerMaestro implements Maestro {
    private final TwGame mGame;
    private final GameInfo mGameInfo = new GameInfo();

    public MultiPlayerMaestro(TwGame game) {
        mGame = game;
    }

    @Override
    public void actionTriggered(String action) {
        String current = mGame.getScreen().getClass().getSimpleName();
        if (current.equals("MultiPlayerScreen")) {
            if (action.equals("next")) {
                mGame.replaceScreen(new SelectMapScreen(mGame, this, mGameInfo, mGame.getConfig().multiPlayer));
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
                ((RaceScreen)mGame.getScreen()).forgetMapInfo();
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
