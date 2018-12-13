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

import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.racescreen.RaceScreen;
import com.agateau.pixelwheels.screens.ChampionshipFinishedScreen;
import com.agateau.pixelwheels.screens.MultiPlayerScreen;
import com.agateau.pixelwheels.screens.SelectChampionshipScreen;
import com.agateau.pixelwheels.screens.SelectVehicleScreen;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

/**
 * Handle a championship game
 */
public class ChampionshipMaestro extends Maestro {
    private final ChampionshipGameInfo.Builder mGameInfoBuilder;
    private ChampionshipGameInfo mGameInfo;

    public ChampionshipMaestro(PwGame game, PlayerCount playerCount) {
        super(game, playerCount);
        mGameInfoBuilder = new ChampionshipGameInfo.Builder(getGame().getAssets().vehicleDefs, getGame().getConfig());
    }

    @Override
    public void start() {
        getGame().pushScreen(createChampionshipScreen());
    }

    private Screen createSelectVehicleScreen() {
        if (getPlayerCount() == PlayerCount.ONE) {
            return createOnePlayerVehicleScreen();
        } else {
            return createMultiPlayerVehicleScreen();
        }
    }

    private Screen createOnePlayerVehicleScreen() {
        SelectVehicleScreen.Listener listener = new SelectVehicleScreen.Listener() {
            @Override
            public void onBackPressed() {
                getGame().replaceScreen(createChampionshipScreen());
            }

            @Override
            public void onPlayerSelected(GameInfo.Player player) {
                Array<GameInfo.Player> players = new Array<GameInfo.Player>();
                players.add(player);
                mGameInfoBuilder.setPlayers(players);
                startChampionship();
            }
        };
        return new SelectVehicleScreen(getGame(), listener);
    }

    private Screen createMultiPlayerVehicleScreen() {
        MultiPlayerScreen.Listener listener = new MultiPlayerScreen.Listener() {
            @Override
            public void onBackPressed() {
                getGame().replaceScreen(createChampionshipScreen());
            }

            @Override
            public void onPlayersSelected(Array<GameInfo.Player> players) {
                mGameInfoBuilder.setPlayers(players);
                startChampionship();
            }
        };
        return new MultiPlayerScreen(getGame(), listener);
    }

    private Screen createChampionshipScreen() {
        final GameConfig gameConfig = getGame().getConfig();
        SelectChampionshipScreen.Listener listener = new SelectChampionshipScreen.Listener() {
            @Override
            public void onBackPressed() {
                stopGamepadInputWatcher();
                getGame().popScreen();
            }

            @Override
            public void onChampionshipSelected(Championship championship) {
                mGameInfoBuilder.setChampionship(championship);
                getGame().replaceScreen(createSelectVehicleScreen());
            }
        };

        return new SelectChampionshipScreen(getGame(), listener, gameConfig.championship);
    }

    private void startChampionship() {
        mGameInfo = mGameInfoBuilder.build();
        getGame().replaceScreen(createRaceScreen());
    }

    private Screen createRaceScreen() {
        RaceScreen.Listener listener = new RaceScreen.Listener() {
            @Override
            public void onRestartPressed() {
                ((RaceScreen)getGame().getScreen()).forgetTrack();
                getGame().replaceScreen(createRaceScreen());
            }

            @Override
            public void onQuitPressed() {
                stopGamepadInputWatcher();
                getGame().showMainMenu();
            }

            @Override
            public void onNextTrackPressed() {
                if (mGameInfo.isLastTrack()) {
                    getGame().replaceScreen(createChampionshipFinishedScreen());
                } else {
                    mGameInfo.selectNextTrack();
                    getGame().replaceScreen(createRaceScreen());
                }
            }
        };
        return new RaceScreen(getGame(), listener, mGameInfo);
    }

    private Screen createChampionshipFinishedScreen() {
        return new ChampionshipFinishedScreen(getGame(), mGameInfo);
    }
}
