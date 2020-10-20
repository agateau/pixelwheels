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

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.racescreen.RaceScreen;
import com.agateau.pixelwheels.rewards.Reward;
import com.agateau.pixelwheels.screens.ChampionshipFinishedScreen;
import com.agateau.pixelwheels.screens.MultiPlayerScreen;
import com.agateau.pixelwheels.screens.NavStageScreen;
import com.agateau.pixelwheels.screens.SelectChampionshipScreen;
import com.agateau.pixelwheels.screens.SelectVehicleScreen;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import java.util.Set;

/** Handle a championship game */
public class ChampionshipMaestro extends Maestro {
    private final ChampionshipGameInfo.Builder mGameInfoBuilder;
    private ChampionshipGameInfo mGameInfo;

    public ChampionshipMaestro(PwGame game, PlayerCount playerCount) {
        super(game, playerCount);
        mGameInfoBuilder =
                new ChampionshipGameInfo.Builder(
                        getGame().getAssets().vehicleDefs, getGame().getConfig());
    }

    public boolean isFirstTrack() {
        return mGameInfo.isFirstTrack();
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
        SelectVehicleScreen.Listener listener =
                new SelectVehicleScreen.Listener() {
                    @Override
                    public void onBackPressed() {
                        getGame().replaceScreen(createChampionshipScreen());
                    }

                    @Override
                    public void onPlayerSelected(GameInfo.Player player) {
                        Array<GameInfo.Player> players = new Array<>();
                        players.add(player);
                        mGameInfoBuilder.setPlayers(players);
                        startChampionship();
                    }
                };
        return new SelectVehicleScreen(getGame(), listener);
    }

    private Screen createMultiPlayerVehicleScreen() {
        MultiPlayerScreen.Listener listener =
                new MultiPlayerScreen.Listener() {
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
        SelectChampionshipScreen.Listener listener =
                new SelectChampionshipScreen.Listener() {
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
        if (Constants.DEBUG_SCREEN.equals("ChampionshipFinished:podium")) {
            // Players are always last at the beginning, move the last player to the top
            GameInfo.Entrant player = mGameInfo.getEntrants().pop();
            mGameInfo.getEntrants().insert(0, player);
            getGame().replaceScreen(createChampionshipFinishedScreen());
        } else if (Constants.DEBUG_SCREEN.equals("ChampionshipFinished:nopodium")) {
            getGame().replaceScreen(createChampionshipFinishedScreen());
        } else {
            getGame().replaceScreen(createRaceScreen());
        }
    }

    private Screen createRaceScreen() {
        RaceScreen.Listener listener =
                new RaceScreen.Listener() {
                    @Override
                    public void onRestartPressed() {
                        throw new RuntimeException(
                                "Restart should not be called in championship mode");
                    }

                    @Override
                    public void onQuitPressed() {
                        stopGamepadInputWatcher();
                        getGame().showMainMenu();
                    }

                    @Override
                    public void onNextTrackPressed() {
                        mGameInfo.sortEntrants();
                        if (mGameInfo.isLastTrack()) {
                            getGame().onChampionshipFinished(mGameInfo);
                            getGame().replaceScreen(createChampionshipFinishedScreen());
                        } else {
                            mGameInfo.selectNextTrack();
                            final Set<Reward> rewards = getNewlyUnlockedRewards();
                            updateAlreadyUnlockedRewards();
                            showUnlockedRewardScreen(
                                    rewards, () -> getGame().replaceScreen(createRaceScreen()));
                        }
                    }
                };
        return new RaceScreen(getGame(), listener, mGameInfo);
    }

    private Screen createChampionshipFinishedScreen() {
        final Set<Reward> rewards = getNewlyUnlockedRewards();
        updateAlreadyUnlockedRewards();
        final NavStageScreen.NextListener navListener =
                new NavStageScreen.NextListener() {
                    @Override
                    public void onNextPressed() {
                        showUnlockedRewardScreen(rewards, () -> getGame().showMainMenu());
                    }
                };
        return new ChampionshipFinishedScreen(getGame(), mGameInfo, navListener);
    }
}
