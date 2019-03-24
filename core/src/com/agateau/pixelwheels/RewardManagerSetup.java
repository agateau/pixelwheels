/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels;

import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.rewards.Reward;
import com.agateau.pixelwheels.rewards.RewardManager;
import com.agateau.pixelwheels.rewards.RewardRule;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.utils.CollectionUtils;
import com.badlogic.gdx.utils.Array;

import java.util.Set;

/**
 * Helper class to create the reward manager rules
 */
class RewardManagerSetup {
    private static final Set<String> ALWAYS_UNLOCKED_VEHICLE_IDS = CollectionUtils.newSet("red", "police", "pickup", "roadster", "antonin", "santa", "2cv", "harvester");

    static void createChampionshipRules(RewardManager rewardManager, Array<Championship> championships) {
        rewardManager.addRule(Reward.get(championships.first()), RewardManager.ALWAYS_UNLOCKED);

        for (int idx = 1; idx < championships.size; ++idx) {
            final Championship previous = championships.get(idx - 1);
            rewardManager.addRule(Reward.get(championships.get(idx)), new RewardRule() {
                @Override
                public boolean hasBeenEarned(GameStats gameStats) {
                    return gameStats.getBestChampionshipRank(previous) <= 2;
                }
            });
        }
    }

    static void createVehicleRules(RewardManager rewardManager, Assets assets) {
        for (VehicleDef vehicleDef : assets.vehicleDefs) {
            if (ALWAYS_UNLOCKED_VEHICLE_IDS.contains(vehicleDef.id)) {
                rewardManager.addRule(Reward.get(vehicleDef), RewardManager.ALWAYS_UNLOCKED);
            }
        }
        rewardManager.addRule(Reward.get(assets.findVehicleDefById("rocket")), new RewardRule() {
            @Override
            public boolean hasBeenEarned(GameStats gameStats) {
                return gameStats.getEventCount(GameStats.Event.MISSILE_HIT) >= 10;
            }
        });
    }
}
