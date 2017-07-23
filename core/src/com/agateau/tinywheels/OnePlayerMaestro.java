package com.agateau.tinywheels;

/**
 * Handle a one player game
 */
public class OnePlayerMaestro implements Maestro {
    private final TwGame mGame;
    private final GameInfo mGameInfo = new GameInfo();

    public OnePlayerMaestro(TwGame game) {
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
