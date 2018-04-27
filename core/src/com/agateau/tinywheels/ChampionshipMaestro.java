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
import com.agateau.tinywheels.screens.MultiPlayerScreen;
import com.agateau.tinywheels.screens.SelectChampionshipScreen;
import com.agateau.tinywheels.screens.SelectVehicleScreen;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

/**
 * Handle a championship game
 */
public class ChampionshipMaestro implements Maestro {
    private final TwGame mGame;
    private final ChampionshipGameInfo.Builder mGameInfoBuilder;
    private final PlayerCount mPlayerCount;
    private ChampionshipGameInfo mGameInfo;

    public ChampionshipMaestro(TwGame game, PlayerCount playerCount) {
        mGame = game;
        mPlayerCount = playerCount;
        mGameInfoBuilder = new ChampionshipGameInfo.Builder(mGame.getAssets().vehicleDefs, mGame.getConfig());
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
                mGame.replaceScreen(createChampionshipScreen());
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
                mGame.replaceScreen(createChampionshipScreen());
            }
        };
        return new MultiPlayerScreen(mGame, listener);
    }

    private Screen createChampionshipScreen() {
        final GameConfig gameConfig = mGame.getConfig();
        SelectChampionshipScreen.Listener listener = new SelectChampionshipScreen.Listener() {
            @Override
            public void onBackPressed() {
                mGame.replaceScreen(createSelectVehicleScreen());
            }

            @Override
            public void onChampionshipSelected(Championship championship) {
                mGameInfoBuilder.setChampionship(championship);
                startChampionship();
            }
        };

        return new SelectChampionshipScreen(mGame, listener, gameConfig.championship);
    }

    private void startChampionship() {
        mGameInfo = mGameInfoBuilder.build();
        mGame.replaceScreen(createRaceScreen());
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
                    mGame.replaceScreen(createChampionshipFinishedScreen());
                } else {
                    mGameInfo.selectNextTrack();
                    mGame.replaceScreen(createRaceScreen());
                }
            }
        };
        return new RaceScreen(mGame, listener, mGameInfo);
    }

    private Screen createChampionshipFinishedScreen() {
        return new ChampionshipFinishedScreen(mGame, mGameInfo);
    }
}
