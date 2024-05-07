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

import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.utils.Array;

/** Details about the game to start */
public abstract class GameInfo {
    public enum GameType {
        QUICK_RACE,
        CHAMPIONSHIP
    }

    private final Array<Entrant> mEntrants = new Array<>();
    private final GameType mGameType;

    public abstract static class Builder<T extends GameInfo> {
        final Array<VehicleDef> mVehicleDefs;
        final GameConfig mGameConfig;
        Array<Player> mPlayers;

        Builder(Array<VehicleDef> vehicleDefs, GameConfig gameConfig) {
            mVehicleDefs = vehicleDefs;
            mGameConfig = gameConfig;
        }

        public void setPlayers(Array<Player> players) {
            mPlayers = players;
            storePlayersInConfig();
        }

        public abstract T build();

        void createEntrants(GameInfo gameInfo) {
            Array<String> vehicleIds = new Array<>();
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
            gameInfo.mEntrants.shuffle();
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

        private int mPoints = 0;
        private int mLastRacePoints = 0;
        private float mRaceTime = 0;

        public String getVehicleId() {
            return mVehicleId;
        }

        public int getPoints() {
            return mPoints;
        }

        public float getRaceTime() {
            return mRaceTime;
        }

        public int getLastRacePoints() {
            return mLastRacePoints;
        }

        public void addPoints(int points) {
            mLastRacePoints = points;
            mPoints += points;
        }

        public void addRaceTime(float time) {
            mRaceTime += time;
        }

        public boolean isPlayer() {
            return false;
        }
    }

    public static class Player extends Entrant {
        private final int mIndex;

        public Player(int idx, String vehicleId) {
            mIndex = idx;
            mVehicleId = vehicleId;
        }

        public int getIndex() {
            return mIndex;
        }

        @Override
        public boolean isPlayer() {
            return true;
        }
    }

    protected GameInfo(GameType gameType) {
        mGameType = gameType;
    }

    public GameType getGameType() {
        return mGameType;
    }

    public abstract Track getTrack();

    public Array<Entrant> getEntrants() {
        return mEntrants;
    }

    public void sortEntrants() {
        mEntrants.sort(
                (e1, e2) -> {
                    int cmp = -Integer.compare(e1.getPoints(), e2.getPoints());
                    if (cmp != 0) {
                        return cmp;
                    }
                    // If it's a tie, the fastest gets the best place
                    return Float.compare(e1.getRaceTime(), e2.getRaceTime());
                });
    }

    public int getBestRank() {
        for (int idx = 0; idx < mEntrants.size; ++idx) {
            GameInfo.Entrant entrant = mEntrants.get(idx);
            if (entrant.isPlayer()) {
                return idx;
            }
        }
        NLog.e("No player entrants found!");
        return Integer.MAX_VALUE;
    }
}
