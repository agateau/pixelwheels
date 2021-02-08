/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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

/** Handle a quick race game */
public class QuickRaceMaestro extends Maestro {
    private final QuickRaceGameInfo.Builder mGameInfoBuilder;

    public QuickRaceMaestro(PwGame game, PlayerCount playerCount) {
        super(game, playerCount);
        mGameInfoBuilder =
                new QuickRaceGameInfo.Builder(game.getAssets().vehicleDefs, game.getConfig());
    }

    @Override
    public void start() {
        getGame().pushScreen(createSelectTrackScreen());
    }

    private Screen createSelectVehicleScreen() {
        if (getPlayerCount() == PlayerCount.ONE) {
            return createOnePlayerVehicleScreen();
        } else {
            return createMultiPlayerVehicleScreen();
        }
    }

    private Screen createOnePlayerVehicleScreen() {
        SelectVehicleScreen.Listener listener =
                new SelectVehicleScreen.Listener() {
                    @Override
                    public void onBackPressed() {
                        getGame().replaceScreen(createSelectTrackScreen());
                    }

                    @Override
                    public void onPlayerSelected(GameInfo.Player player) {
                        Array<GameInfo.Player> players = new Array<>();
                        players.add(player);
                        mGameInfoBuilder.setPlayers(players);
                        getGame().replaceScreen(createRaceScreen());
                    }
                };
        return new SelectVehicleScreen(getGame(), listener);
    }

    private Screen createMultiPlayerVehicleScreen() {
        MultiPlayerScreen.Listener listener =
                new MultiPlayerScreen.Listener() {
                    @Override
                    public void onBackPressed() {
                        getGame().replaceScreen(createSelectTrackScreen());
                    }

                    @Override
                    public void onPlayersSelected(Array<GameInfo.Player> players) {
                        mGameInfoBuilder.setPlayers(players);
                        getGame().replaceScreen(createRaceScreen());
                    }
                };
        return new MultiPlayerScreen(getGame(), listener);
    }

    private Screen createSelectTrackScreen() {
        SelectTrackScreen.Listener listener =
                new SelectTrackScreen.Listener() {
                    @Override
                    public void onBackPressed() {
                        stopGamepadInputWatcher();
                        getGame().popScreen();
                    }

                    @Override
                    public void onTrackSelected(Track track) {
                        mGameInfoBuilder.setTrack(track);
                        getGame().replaceScreen(createSelectVehicleScreen());
                    }
                };
        return new SelectTrackScreen(getGame(), listener);
    }

    private Screen createRaceScreen() {
        RaceScreen.Listener listener =
                new RaceScreen.Listener() {
                    @Override
                    public void onRestartPressed() {
                        ((RaceScreen) getGame().getScreen()).forgetTrack();
                        getGame().replaceScreen(createRaceScreen());
                    }

                    @Override
                    public void onQuitPressed() {
                        stopGamepadInputWatcher();
                        getGame().showMainMenu();
                    }

                    @Override
                    public void onNextTrackPressed() {
                        stopGamepadInputWatcher();
                        showUnlockedRewardScreen(() -> getGame().showMainMenu());
                    }
                };
        QuickRaceGameInfo gameInfo = mGameInfoBuilder.build();
        return new RaceScreen(getGame(), listener, gameInfo);
    }
}
