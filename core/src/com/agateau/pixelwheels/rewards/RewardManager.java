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

import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * Manage which rewards have been unlocked
 *
 * Contains a set of rules applied against the game stats. These rules decide if a
 * reward is unlocked.
 */
public class RewardManager {
    private final GameStats mGameStats;
    private final Array<Championship> mChampionships;
    private Map<Reward, RewardRule> mRules = new HashMap<Reward, RewardRule>();

    public RewardManager(GameStats gameStats, Array<Championship> championships) {
        mGameStats = gameStats;
        mChampionships = championships;
        addRule(Reward.Category.CHAMPIONSHIP, "city", new RewardRule() {
            @Override
            public boolean hasBeenEarned(GameStats gameStats) {
                return gameStats.getBestChampionshipRank("snow") <= 2;
            }
        });
    }

    public boolean isTrackUnlocked(String trackId) {
        for (Championship championship : mChampionships) {
            for (Track track : championship.getTracks()) {
                if (track.getId().equals(trackId)) {
                    return isChampionshipUnlocked(championship.getId());
                }
            }
        }
        NLog.e("Track %s does not belong to any championship!", trackId);
        return false;
    }

    public boolean isChampionshipUnlocked(String championshipId) {
        return isRewardUnlocked(Reward.Category.CHAMPIONSHIP, championshipId);
    }

    public boolean isVehicleUnlocked(String vehicleId) {
        return isRewardUnlocked(Reward.Category.VEHICLE, vehicleId);
    }

    private boolean isRewardUnlocked(Reward.Category category, String id) {
        Reward reward = Reward.get(category, id);
        RewardRule rule = mRules.get(reward);
        return rule == null || rule.hasBeenEarned(mGameStats);
    }

    private void addRule(Reward.Category category, String id, RewardRule rule) {
        Reward reward = Reward.get(category, id);
        mRules.put(reward, rule);
    }
}
