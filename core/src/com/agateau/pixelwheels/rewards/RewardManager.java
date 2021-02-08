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
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manage which rewards have been unlocked
 *
 * <p>Contains a set of rules applied against the game stats. These rules decide if a reward is
 * unlocked.
 */
public class RewardManager {
    private final GameStats mGameStats;
    private final Map<Reward, RewardRule> mRules = new HashMap<>();

    public void markAllUnlockedRewardsSeen() {
        mUnlockedRewards.markAllSeen();
    }

    /**
     * Returns the unlocked rewards which have not yet been shown to the player The returned set is
     * a copy, so it's not affected by calls to markAllUnlockedRewardsSeen()
     */
    public Set<Reward> getUnseenUnlockedRewards() {
        return mUnlockedRewards.getUnseen();
    }

    /**
     * Wraps the set of unlocked rewards, making sure other code does not access it without applying
     * any pending update.
     */
    private class UnlockedRewards {
        private final Set<Reward> mRewards = new HashSet<>();
        private final Set<Reward> mUnseenRewards = new HashSet<>();
        private boolean mNeedsUpdate = true;

        Set<Reward> get() {
            if (mNeedsUpdate) {
                update();
            }
            return mRewards;
        }

        Set<Reward> getUnseen() {
            if (mNeedsUpdate) {
                update();
            }
            return new HashSet<>(mUnseenRewards);
        }

        void markAllSeen() {
            if (mNeedsUpdate) {
                update();
            }
            mUnseenRewards.clear();
        }

        void scheduleUpdate() {
            mNeedsUpdate = true;
        }

        private void update() {
            mNeedsUpdate = false;
            for (Map.Entry<Reward, RewardRule> rule : mRules.entrySet()) {
                Reward reward = rule.getKey();
                if (mRewards.contains(reward)) {
                    continue;
                }
                if (rule.getValue().hasBeenUnlocked(mGameStats)) {
                    mRewards.add(reward);
                    mUnseenRewards.add(reward);
                }
            }
        }
    }

    private final UnlockedRewards mUnlockedRewards = new UnlockedRewards();

    public static final RewardRule ALWAYS_UNLOCKED =
            new RewardRule() {
                @Override
                public boolean hasBeenUnlocked(GameStats gameStats) {
                    return true;
                }

                @Override
                public String getUnlockText(GameStats gameStats) {
                    return "";
                }
            };

    public RewardManager(GameStats gameStats) {
        mGameStats = gameStats;
        mGameStats.setListener(mUnlockedRewards::scheduleUpdate);
    }

    public boolean isTrackUnlocked(Track track) {
        Championship championship = track.getChampionship();
        return isChampionshipUnlocked(championship);
    }

    public boolean isChampionshipUnlocked(Championship championship) {
        return getUnlockedRewards().contains(Reward.get(championship));
    }

    public boolean isVehicleUnlocked(VehicleDef vehicleDef) {
        return getUnlockedRewards().contains(Reward.get(vehicleDef));
    }

    public Set<Reward> getUnlockedRewards() {
        return mUnlockedRewards.get();
    }

    public void addRule(Reward reward, RewardRule rule) {
        mRules.put(reward, rule);
    }

    public String getUnlockText(Track track) {
        Championship championship = track.getChampionship();
        return getUnlockText(Reward.get(championship));
    }

    public String getUnlockText(Championship championship) {
        return getUnlockText(Reward.get(championship));
    }

    public String getUnlockText(VehicleDef vehicle) {
        return getUnlockText(Reward.get(vehicle));
    }

    private String getUnlockText(Reward reward) {
        if (mUnlockedRewards.get().contains(reward)) {
            return "";
        } else {
            return mRules.get(reward).getUnlockText(mGameStats);
        }
    }
}
