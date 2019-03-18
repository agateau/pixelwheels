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
package com.agateau.pixelwheels.rewards;

import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manage which rewards have been unlocked
 *
 * Contains a set of rules applied against the game stats. These rules decide if a
 * reward is unlocked.
 */
public class RewardManager {
    private final GameStats mGameStats;
    private final Array<Championship> mChampionships;
    private final Map<Reward, RewardRule> mRules = new HashMap<Reward, RewardRule>();
    private final Set<Reward> mUnlockedRewards = new HashSet<Reward>();

    public static final RewardRule ALWAYS_UNLOCKED = new RewardRule() {
        @Override
        public boolean hasBeenEarned(GameStats gameStats) {
            return true;
        }
    };

    public RewardManager(GameStats gameStats, Array<Championship> championships) {
        mGameStats = gameStats;
        mChampionships = championships;
    }

    public boolean isTrackUnlocked(Track track) {
        for (Championship championship : mChampionships) {
            if (championship.getTracks().contains(track, true /* identity */)) {
                return isChampionshipUnlocked(championship);
            }
        }
        NLog.e("Track %s does not belong to any championship!", track);
        return false;
    }

    public boolean isChampionshipUnlocked(Championship championship) {
        return isRewardUnlocked(Reward.Category.CHAMPIONSHIP, championship.getId());
    }

    public boolean isVehicleUnlocked(String vehicleId) {
        return isRewardUnlocked(Reward.Category.VEHICLE, vehicleId);
    }

    public Set<Reward> getUnlockedRewards() {
        return mUnlockedRewards;
    }

    private boolean isRewardUnlocked(Reward.Category category, String id) {
        Reward reward = Reward.get(category, id);
        return mUnlockedRewards.contains(reward);
    }

    public void addRule(Reward.Category category, String id, RewardRule rule) {
        Reward reward = Reward.get(category, id);
        mRules.put(reward, rule);
    }

    public void applyRules() {
        for (Map.Entry<Reward, RewardRule> rule : mRules.entrySet()) {
            Reward reward = rule.getKey();
            if (mUnlockedRewards.contains(reward)) {
                continue;
            }
            if (rule.getValue().hasBeenEarned(mGameStats)) {
                mUnlockedRewards.add(reward);
            }
        }
    }
}
