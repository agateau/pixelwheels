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
import com.agateau.pixelwheels.rewards.CounterRewardRule;
import com.agateau.pixelwheels.rewards.Reward;
import com.agateau.pixelwheels.rewards.RewardManager;
import com.agateau.pixelwheels.rewards.RewardRule;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.utils.CollectionUtils;
import com.badlogic.gdx.utils.Array;
import java.util.Set;

/** Helper class to create the reward manager rules */
class RewardManagerSetup {
    private static final Set<String> ALWAYS_UNLOCKED_VEHICLE_IDS =
            CollectionUtils.newSet(
                    "red", "police", "pickup", "roadster", "antonin", "santa", "2cv", "harvester");

    static void createChampionshipRules(
            RewardManager rewardManager, Array<Championship> championships) {
        rewardManager.addRule(Reward.get(championships.first()), RewardManager.ALWAYS_UNLOCKED);

        for (int idx = 1; idx < championships.size; ++idx) {
            final Championship previous = championships.get(idx - 1);
            final Championship next =
                    idx < championships.size - 1 ? championships.get(idx + 1) : null;
            rewardManager.addRule(
                    Reward.get(championships.get(idx)),
                    new RewardRule() {
                        @Override
                        public boolean hasBeenUnlocked(GameStats gameStats) {
                            if (next != null && rewardManager.isChampionshipUnlocked(next)) {
                                // Hack to handle case where a new championship has been added at
                                // the beginning of the game:
                                //
                                // Say we have Champ. X and Y, and player has already unlocked Y by
                                // finishing X. If in a new
                                // version of the game we add Champ. W before Champ X, then Y is
                                // unlocked because we
                                // recorded the performance on X, but X is not because unlocking it
                                // now requires a good
                                // performance on W. This would be surprising for the player.
                                //
                                // To avoid that, unlock a championship if the next one has already
                                // been unlocked. In our
                                // example X would be unlocked because Y has already been unlocked.
                                return true;
                            }
                            return gameStats.getBestChampionshipRank(previous) <= 2;
                        }

                        @Override
                        public String getUnlockText(GameStats gameStats) {
                            return StringUtils.format(
                                    "Rank 3 or better at %s championship", previous.getName());
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
        rewardManager.addRule(
                Reward.get(assets.findVehicleDefById("rocket")),
                new CounterRewardRule(
                        GameStats.Event.MISSILE_HIT, 10, "Hit %d vehicles with a missile"));

        rewardManager.addRule(
                Reward.get(assets.findVehicleDefById("harvester")),
                new CounterRewardRule(GameStats.Event.LEAVING_ROAD, 50, "Leave road %d times"));

        rewardManager.addRule(
                Reward.get(assets.findVehicleDefById("santa")),
                new CounterRewardRule(GameStats.Event.PICKED_BONUS, 20, "Pick %d bonuses"));

        rewardManager.addRule(
                Reward.get(assets.findVehicleDefById("dark-m")),
                new CounterRewardRule(
                        GameStats.Event.MISSILE_HIT, 40, "Hit %d vehicles with a missile"));
    }
}
