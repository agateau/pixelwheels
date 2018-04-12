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

import com.agateau.tinywheels.racescreen.RaceScreen;
import com.agateau.tinywheels.screens.SelectChampionshipScreen;
import com.agateau.tinywheels.screens.SelectVehicleScreen;

/**
 * Handle a one player game
 */
public class OnePlayerChampionshipMaestro implements Maestro {
    private final TwGame mGame;
    private final ChampionshipGameInfo mGameInfo = new ChampionshipGameInfo();

    public OnePlayerChampionshipMaestro(TwGame game) {
        mGame = game;
    }

    @Override
    public void actionTriggered(String action) {
        Class current = mGame.getScreen().getClass();
        if (current == SelectVehicleScreen.class) {
            if (action.equals("next")) {
                mGame.replaceScreen(new SelectChampionshipScreen(mGame, this, mGameInfo, mGame.getConfig().onePlayer));
            } else if (action.equals("back")) {
                mGame.popScreen();
            }
        } else if (current == SelectChampionshipScreen.class) {
            if (action.equals("next")) {
                mGameInfo.restart();
                mGame.replaceScreen(new RaceScreen(mGame, this, mGameInfo));
            } else if (action.equals("back")) {
                mGame.replaceScreen(new SelectVehicleScreen(mGame, this, mGameInfo));
            }
        } else if (current == RaceScreen.class) {
            if (action.equals("restart")) {
                ((RaceScreen)mGame.getScreen()).forgetTrack();
                mGameInfo.restart();
                mGame.replaceScreen(new RaceScreen(mGame, this, mGameInfo));
            } else if (action.equals("quit")) {
                if (mGameInfo.isLastTrack()) {
                    mGame.showMainMenu();
                } else {
                    mGameInfo.selectNextTrack();
                    mGame.replaceScreen(new RaceScreen(mGame, this, mGameInfo));
                }
            }
        }
    }

    @Override
    public void start() {
        mGame.pushScreen(new SelectVehicleScreen(mGame, this, mGameInfo));
    }
}
