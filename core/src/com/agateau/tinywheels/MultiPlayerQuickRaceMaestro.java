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

import com.agateau.tinywheels.gameinput.GameInputHandler;
import com.agateau.tinywheels.map.Track;
import com.agateau.tinywheels.racescreen.RaceScreen;
import com.agateau.tinywheels.screens.MultiPlayerScreen;
import com.agateau.tinywheels.screens.SelectTrackScreen;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

/**
 * Handle a multi player game
 */
public class MultiPlayerQuickRaceMaestro implements Maestro {
    private final TwGame mGame;
    private final QuickRaceGameInfo mGameInfo;

    public MultiPlayerQuickRaceMaestro(TwGame game) {
        mGame = game;
        mGameInfo = new QuickRaceGameInfo(mGame.getAssets().vehicleDefs, mGame.getConfig().multiPlayer);
    }

    @Override
    public void start() {
        mGame.replaceScreen(createMultiPlayerScreen());
    }

    private Screen createMultiPlayerScreen() {
        MultiPlayerScreen.Listener listener = new MultiPlayerScreen.Listener() {
            @Override
            public void onBackPressed() {
                mGame.showMainMenu();
            }

            @Override
            public void onPlayersSelected(Array<GameInfo.Player> players) {
                mGameInfo.setPlayers(players);
                mGame.replaceScreen(createSelectTrackScreen());
            }
        };
        return new MultiPlayerScreen(mGame, listener);
    }

    private Screen createSelectTrackScreen() {
        SelectTrackScreen.Listener listener = new SelectTrackScreen.Listener() {
            @Override
            public void onBackPressed() {
                mGame.replaceScreen(createMultiPlayerScreen());
            }
            @Override
            public void onTrackSelected(Track track) {
                mGameInfo.setTrack(track);
                mGame.replaceScreen(createRaceScreen());
            }
        };
        return new SelectTrackScreen(mGame, listener, mGame.getConfig().onePlayer);
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
        return new RaceScreen(mGame, listener, mGameInfo);
    }
}
