/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.rewards;

/**
 * Manage which rewards have been unlocked
 *
 * Contains a set of rules applied against the game stats. These rules decide if a
 * reward is unlocked.
 */
public class RewardManager {
    public boolean isTrackUnlocked(String trackId) {
        return isRewardUnlocked(Reward.Category.TRACK, trackId);
    }

    public boolean isChampionshipUnlocked(String championshipId) {
        return isRewardUnlocked(Reward.Category.CHAMPIONSHIP, championshipId);
    }

    public boolean isVehicleUnlocked(String vehicleId) {
        return isRewardUnlocked(Reward.Category.VEHICLE, vehicleId);
    }

    private boolean isRewardUnlocked(Reward.Category category, String id) {
        return true;
    }
}
