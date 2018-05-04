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
package com.agateau.pixelwheels.gamesetup;

import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.racescreen.RaceScreen;
import com.agateau.pixelwheels.screens.MultiPlayerScreen;
import com.agateau.pixelwheels.screens.SelectTrackScreen;
import com.agateau.pixelwheels.screens.SelectVehicleScreen;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

/**
 * Handle a quick race game
 */
public class QuickRaceMaestro implements Maestro {
    private final PwGame mGame;
    private final QuickRaceGameInfo.Builder mGameInfoBuilder;
    private final PlayerCount mPlayerCount;

    public QuickRaceMaestro(PwGame game, PlayerCount playerCount) {
        mGame = game;
        mPlayerCount = playerCount;
        mGameInfoBuilder = new QuickRaceGameInfo.Builder(game.getAssets().vehicleDefs, game.getConfig());
    }

    @Override
    public void start() {
        mGame.pushScreen(createSelectVehicleScreen());
    }

    private Screen createSelectVehicleScreen() {
        if (mPlayerCount == PlayerCount.ONE) {
            return createOnePlayerVehicleScreen();
        } else {
            return createMultiPlayerVehicleScreen();
        }
    }

    private Screen createOnePlayerVehicleScreen() {
        SelectVehicleScreen.Listener listener = new SelectVehicleScreen.Listener() {
            @Override
            public void onBackPressed() {
                mGame.popScreen();
            }

            @Override
            public void onPlayerSelected(GameInfo.Player player) {
                Array<GameInfo.Player> players = new Array<GameInfo.Player>();
                players.add(player);
                mGameInfoBuilder.setPlayers(players);
                mGame.replaceScreen(createSelectTrackScreen());
            }
        };
        return new SelectVehicleScreen(mGame, listener);
    }

    private Screen createMultiPlayerVehicleScreen() {
        MultiPlayerScreen.Listener listener = new MultiPlayerScreen.Listener() {
            @Override
            public void onBackPressed() {
                mGame.popScreen();
            }

            @Override
            public void onPlayersSelected(Array<GameInfo.Player> players) {
                mGameInfoBuilder.setPlayers(players);
                mGame.replaceScreen(createSelectTrackScreen());
            }
        };
        return new MultiPlayerScreen(mGame, listener);
    }

    private Screen createSelectTrackScreen() {
        SelectTrackScreen.Listener listener = new SelectTrackScreen.Listener() {
            @Override
            public void onBackPressed() {
                mGame.replaceScreen(createSelectVehicleScreen());
            }
            @Override
            public void onTrackSelected(Track track) {
                mGameInfoBuilder.setTrack(track);
                mGame.replaceScreen(createRaceScreen());
            }
        };
        return new SelectTrackScreen(mGame, listener);
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
                mGame.showMainMenu();
            }
        };
        QuickRaceGameInfo gameInfo = mGameInfoBuilder.build();
        return new RaceScreen(mGame, listener, gameInfo);
    }
}
