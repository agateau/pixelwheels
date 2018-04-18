/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.tinywheels;

import com.agateau.tinywheels.map.Championship;
import com.agateau.tinywheels.racescreen.RaceScreen;
import com.agateau.tinywheels.screens.SelectChampionshipScreen;
import com.agateau.tinywheels.screens.SelectVehicleScreen;
import com.badlogic.gdx.Screen;

/**
 * Handle a one player game
 */
public class OnePlayerChampionshipMaestro implements Maestro {
    private final TwGame mGame;
    private final ChampionshipGameInfo mGameInfo;

    public OnePlayerChampionshipMaestro(TwGame game) {
        mGame = game;
        mGameInfo = new ChampionshipGameInfo(mGame.getConfig(), mGame.getConfig().onePlayer);
    }

    @Override
    public void start() {
        mGame.pushScreen(createSelectVehicleScreen());
    }

    private Screen createSelectVehicleScreen() {
        SelectVehicleScreen.Listener listener = new SelectVehicleScreen.Listener() {
            @Override
            public void onBackPressed() {
                mGame.popScreen();
            }

            @Override
            public void onVehicleSelected(String vehicleId) {
                // If we came here from the track screen then a player has already been added, remove it
                mGameInfo.clearPlayers();
                mGameInfo.addPlayer(vehicleId);
                mGame.replaceScreen(createChampionshipScreen());
            }
        };
        return new SelectVehicleScreen(mGame, listener);
    }

    private Screen createChampionshipScreen() {
        final GameInfoConfig gameInfoConfig = mGame.getConfig().onePlayer;
        SelectChampionshipScreen.Listener listener = new SelectChampionshipScreen.Listener() {
            @Override
            public void onBackPressed() {
                mGame.replaceScreen(createSelectVehicleScreen());
            }

            @Override
            public void onChampionshipSelected(Championship championship) {
                mGameInfo.setChampionship(championship);
                mGameInfo.restart();
                mGame.replaceScreen(createRaceScreen());
            }
        };

        return new SelectChampionshipScreen(mGame, listener, gameInfoConfig.championship);
    }

    private Screen createRaceScreen() {
        RaceScreen.Listener listener = new RaceScreen.Listener() {
            @Override
            public void onRestartPressed() {
                ((RaceScreen)mGame.getScreen()).forgetTrack();
                mGame.replaceScreen(createRaceScreen());
            }

            @Override
            public void onQuitPressed() {
                mGame.showMainMenu();
            }

            @Override
            public void onNextTrackPressed() {
                if (mGameInfo.isLastTrack()) {
                    mGame.showMainMenu();
                } else {
                    mGameInfo.selectNextTrack();
                    mGame.replaceScreen(createRaceScreen());
                }
            }
        };
        return new RaceScreen(mGame, listener, mGameInfo);
    }
}
