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
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.gameinput.GameInputHandler;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.badlogic.gdx.utils.Array;

/**
 * Details about the game to start
 */
public abstract class GameInfo {
    private final Array<Entrant> mEntrants = new Array<Entrant>();

    public static abstract class Builder<T extends GameInfo> {
        protected final Array<VehicleDef> mVehicleDefs;
        protected final GameConfig mGameConfig;
        protected Array<Player> mPlayers;

        public Builder(Array<VehicleDef> vehicleDefs, GameConfig gameConfig) {
            mVehicleDefs = vehicleDefs;
            mGameConfig = gameConfig;
        }

        public void setPlayers(Array<Player> players) {
            mPlayers = players;
            storePlayersInConfig();
        }

        public abstract T build();

        protected void createEntrants(GameInfo gameInfo) {
            Array<String> vehicleIds = new Array<String>();
            for (VehicleDef vehicleDef : mVehicleDefs) {
                vehicleIds.add(vehicleDef.id);
            }
            for (GameInfo.Player player : mPlayers) {
                vehicleIds.removeValue(player.mVehicleId, /* identity= */ false);
            }
            vehicleIds.shuffle();
            int aiCount = GamePlay.instance.racerCount - mPlayers.size;

            gameInfo.mEntrants.clear();
            for (int idx = 0; idx < aiCount; ++idx) {
                Entrant entrant = new Entrant();
                entrant.mVehicleId = vehicleIds.get(idx % vehicleIds.size);
                gameInfo.mEntrants.add(entrant);
            }
            gameInfo.mEntrants.addAll(mPlayers);
        }

        private void storePlayersInConfig() {
            for (int idx = 0; idx < mGameConfig.vehicles.length; ++idx) {
                String vehicleId = idx < mPlayers.size ? mPlayers.get(idx).mVehicleId : "";
                mGameConfig.vehicles[idx] = vehicleId;
            }
            mGameConfig.flush();
        }
    }

    public static class Entrant {
        protected String mVehicleId;

        private int mScore = 0;
        private float mRaceTime = 0;

        public String getVehicleId() {
            return mVehicleId;
        }

        public int getScore() {
            return mScore;
        }

        public float getRaceTime() {
            return mRaceTime;
        }

        public void addPoints(int points) {
            mScore += points;
        }

        public void addRaceTime(float time) {
            mRaceTime += time;
        }
    }

    public static class Player extends Entrant {
        private final int mIndex;
        private GameInputHandler mInputHandler;

        public GameInputHandler getInputHandler() {
            return mInputHandler;
        }

        public Player(int idx, String vehicleId, GameInputHandler inputHandler) {
            mIndex = idx;
            mVehicleId = vehicleId;
            mInputHandler = inputHandler;
        }

        public int getIndex() {
            return mIndex;
        }
    }

    public abstract Track getTrack();

    public Array<Entrant> getEntrants() {
        return mEntrants;
    }
}
