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
import com.agateau.pixelwheels.gameinput.InputWatcher;
import com.agateau.pixelwheels.screens.NotEnoughControllersScreen;

/**
 * Orchestrate changes between screens for a game
 */
public abstract class Maestro implements InputWatcher.Listener {
    private final PwGame mGame;
    private final PlayerCount mPlayerCount;
    private final InputWatcher mInputWatcher;

    public Maestro(PwGame game, PlayerCount playerCount) {
        mGame = game;
        mPlayerCount = playerCount;
        mInputWatcher = new InputWatcher(mGame.getConfig(), this);
        mInputWatcher.setInputCount(playerCount.toInt());
    }

    public abstract void start();

    public void stop() {
        mInputWatcher.setInputCount(0);
        mGame.showMainMenu();
    }

    public PlayerCount getPlayerCount() {
        return mPlayerCount;
    }

    protected PwGame getGame() {
        return mGame;
    }

    @Override
    public void onNotEnoughControllers() {
        mGame.pushScreen(new NotEnoughControllersScreen(mGame.getAssets().ui));
    }

    @Override
    public void onEnoughControllers() {
        mGame.popScreen();
    }
}
