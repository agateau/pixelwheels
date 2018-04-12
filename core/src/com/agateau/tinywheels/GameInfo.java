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
import com.badlogic.gdx.utils.Array;

/**
 * Details about the game to start
 */
public class GameInfo {
    public static class Player {
        String vehicleId;
        GameInputHandler inputHandler;
    }
    public Track track;
    private final Array<Player> mPlayers = new Array<Player>();

    public Array<Player> getPlayers() {
        return mPlayers;
    }

    public void addPlayer(String vehicleId, GameInputHandler inputHandler) {
        Player player = new Player();
        player.vehicleId = vehicleId;
        player.inputHandler = inputHandler;
        mPlayers.add(player);
    }

    public void clearPlayers() {
        mPlayers.clear();
    }
}
