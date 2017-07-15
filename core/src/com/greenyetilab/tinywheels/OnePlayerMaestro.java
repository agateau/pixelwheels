package com.greenyetilab.tinywheels;

/**
 * Handle a one player game
 */
public class OnePlayerMaestro implements Maestro {
    private final TheGame mGame;
    private final GameInfo mGameInfo = new GameInfo();

    public OnePlayerMaestro(TheGame game) {
        mGame = game;
    }

    @Override
    public void actionTriggered(String action) {
        String current = mGame.getScreen().getClass().getSimpleName();
        if (current.equals("SelectVehicleScreen")) {
            if (action.equals("next")) {
                mGame.replaceScreen(new SelectMapScreen(mGame, this, mGameInfo, mGame.getConfig().onePlayer));
            } else if (action.equals("back")) {
                mGame.showMainMenu();
            }
        } else if (current.equals("SelectMapScreen")) {
            if (action.equals("next")) {
                mGame.replaceScreen(new RaceScreen(mGame, this, mGameInfo));
            } else if (action.equals("back")) {
                mGame.replaceScreen(new SelectVehicleScreen(mGame, this, mGameInfo));
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
        mGame.replaceScreen(new SelectVehicleScreen(mGame, this, mGameInfo));
    }
}
