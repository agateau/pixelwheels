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

import static com.agateau.translations.Translator.trn;

import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.rewards.ChampionshipRankRewardRule;
import com.agateau.pixelwheels.rewards.CounterRewardRule;
import com.agateau.pixelwheels.rewards.Reward;
import com.agateau.pixelwheels.rewards.RewardManager;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.utils.CollectionUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.utils.Array;
import java.util.Set;

/** Helper class to create the reward manager rules */
class RewardManagerSetup {
    private static final int UNLOCK_ROCKET_COUNT = 10;
    private static final int UNLOCK_HARVESTER_COUNT = 50;
    private static final int UNLOCK_SANTA_COUNT = 20;
    private static final int UNLOCK_DARK_M_COUNT = 40;
    private static final int UNLOCK_JEEP_COUNT = 100;

    private static final Set<String> ALWAYS_UNLOCKED_VEHICLE_IDS =
            CollectionUtils.newSet(
                    "red", "police", "pickup", "roadster", "antonin", "santa", "2cv", "harvester");

    static void createChampionshipRules(
            RewardManager rewardManager, Array<Championship> championships) {
        rewardManager.addRule(Reward.get(championships.first()), RewardManager.ALWAYS_UNLOCKED);

        for (int idx = 1; idx < championships.size; ++idx) {
            final Championship previous = championships.get(idx - 1);
            final Championship current = championships.get(idx);
            final int currentIdx = idx;

            rewardManager.addRule(
                    Reward.get(championships.get(idx)),
                    new ChampionshipRankRewardRule(previous, 2) {
                        @Override
                        public boolean hasBeenUnlocked(GameStats gameStats) {
                            if (super.hasBeenUnlocked(gameStats)) {
                                return true;
                            }
                            if (hasAlreadyRacedChampionshipOrAfter(
                                    championships, currentIdx, gameStats)) {
                                // Hack to handle case where a new championship has been added at
                                // the beginning of the game:
                                //
                                // Say we have championships C1 and C2, and player has already
                                // unlocked C2 by finishing C1. If in a new version of the game we
                                // insert championship C0 before C1, then C2 is unlocked because we
                                // recorded the performance on C1, but C1 is not because unlocking
                                // it now requires a good performance on C0. This would be
                                // surprising for the player.
                                //
                                // To avoid that, unlock a championship if we raced it or one after
                                // it once. In our example C1 would be unlocked because we raced it
                                // once.
                                NLog.i(
                                        "Unlock '%s' even if the rank on previous ('%s') is not enough, because we already raced '%s' or a championship after it in the past",
                                        current, previous, current);
                                return true;
                            }
                            return false;
                        }

                        private boolean hasAlreadyRacedChampionshipOrAfter(
                                Array<Championship> championships,
                                int currentIdx,
                                GameStats gameStats) {
                            for (int idx = currentIdx; idx < championships.size; ++idx) {
                                if (gameStats.getBestChampionshipRank(championships.get(idx))
                                        < Integer.MAX_VALUE) {
                                    return true;
                                }
                            }
                            return false;
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
                        GameStats.Event.MISSILE_HIT,
                        UNLOCK_ROCKET_COUNT,
                        trn(
                                "Hit one vehicle with a missile",
                                "Hit %# vehicles with a missile",
                                UNLOCK_ROCKET_COUNT)));

        rewardManager.addRule(
                Reward.get(assets.findVehicleDefById("harvester")),
                new CounterRewardRule(
                        GameStats.Event.LEAVING_ROAD,
                        UNLOCK_HARVESTER_COUNT,
                        trn("Leave road one time", "Leave road %# times", UNLOCK_HARVESTER_COUNT)));

        rewardManager.addRule(
                Reward.get(assets.findVehicleDefById("santa")),
                new CounterRewardRule(
                        GameStats.Event.PICKED_BONUS,
                        UNLOCK_SANTA_COUNT,
                        trn("Pick one bonus", "Pick %# bonuses", UNLOCK_SANTA_COUNT)));

        rewardManager.addRule(
                Reward.get(assets.findVehicleDefById("dark-m")),
                new CounterRewardRule(
                        GameStats.Event.MISSILE_HIT,
                        UNLOCK_DARK_M_COUNT,
                        trn(
                                "Hit one vehicle with a missile",
                                "Hit %# vehicles with a missile",
                                UNLOCK_DARK_M_COUNT)));

        rewardManager.addRule(
                Reward.get(assets.findVehicleDefById("jeep")),
                new CounterRewardRule(
                        GameStats.Event.LEAVING_ROAD,
                        UNLOCK_JEEP_COUNT,
                        trn("Leave road one time", "Leave road %# times", UNLOCK_JEEP_COUNT)));

        rewardManager.addRule(
                Reward.get(assets.findVehicleDefById("miramar")),
                new ChampionshipRankRewardRule(assets.findChampionshipById("city"), 1));
    }
}
