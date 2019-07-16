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
import com.agateau.pixelwheels.racer.Vehicle;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
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
    private final Map<Reward, RewardRule> mRules = new HashMap<>();
    private final Set<Reward> mUnlockedRewards = new HashSet<>();
    private boolean mNeedApplyRules = true;

    public static final RewardRule ALWAYS_UNLOCKED = new RewardRule() {
        @Override
        public boolean hasBeenUnlocked(GameStats gameStats) {
            return true;
        }

        @Override
        public String getUnlockText(GameStats gameStats) {
            return "";
        }
    };

    public RewardManager(GameStats gameStats, Array<Championship> championships) {
        mGameStats = gameStats;
        mGameStats.setListener(() -> mNeedApplyRules = true);
        mChampionships = championships;
    }

    public boolean isTrackUnlocked(Track track) {
        for (Championship championship : mChampionships) {
            if (championship.getTracks().contains(track, true /* identity */)) {
                return isChampionshipUnlocked(championship);
            }
        }
        Championship championship = findTrackChampionship(track);
        if (championship == null) {
            NLog.e("Track %s does not belong to any championship!", track);
            return false;
        } else {
            return isChampionshipUnlocked(championship);
        }
    }

    public boolean isChampionshipUnlocked(Championship championship) {
        return getUnlockedRewards().contains(Reward.get(championship));
    }

    public boolean isVehicleUnlocked(VehicleDef vehicleDef) {
        return getUnlockedRewards().contains(Reward.get(vehicleDef));
    }

    public Set<Reward> getUnlockedRewards() {
        if (mNeedApplyRules) {
            applyRules();
            mNeedApplyRules = false;
        }
        return mUnlockedRewards;
    }

    public void addRule(Reward reward, RewardRule rule) {
        mRules.put(reward, rule);
    }

    public String getUnlockText(Track track) {
        Championship championship = findTrackChampionship(track);
        return getUnlockText(Reward.get(championship));
    }

    public String getUnlockText(Championship championship) {
        return getUnlockText(Reward.get(championship));
    }

    public String getUnlockText(VehicleDef vehicle) {
        return getUnlockText(Reward.get(vehicle));
    }

    private String getUnlockText(Reward reward) {
        if (mUnlockedRewards.contains(reward)) {
            return "";
        } else {
            return mRules.get(reward).getUnlockText(mGameStats);
        }
    }

    private void applyRules() {
        for (Map.Entry<Reward, RewardRule> rule : mRules.entrySet()) {
            Reward reward = rule.getKey();
            if (mUnlockedRewards.contains(reward)) {
                continue;
            }
            if (rule.getValue().hasBeenUnlocked(mGameStats)) {
                mUnlockedRewards.add(reward);
            }
        }
    }

    private Championship findTrackChampionship(Track track) {
        for (Championship championship : mChampionships) {
            if (championship.getTracks().contains(track, true /* identity */)) {
                return championship;
            }
        }
        return null;
    }
}
