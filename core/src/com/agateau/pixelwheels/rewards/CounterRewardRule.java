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

import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.utils.StringUtils;

/** A RewardRule for simple counter-based rewards */
public class CounterRewardRule implements RewardRule {
    private final GameStats.Event mEvent;
    private final int mCount;
    private final String mUnlockText;

    public CounterRewardRule(GameStats.Event event, int count, String unlockText) {
        mEvent = event;
        mCount = count;
        mUnlockText = unlockText;
    }

    @Override
    public boolean hasBeenUnlocked(GameStats gameStats) {
        return gameStats.getEventCount(mEvent) >= mCount;
    }

    @Override
    public String getUnlockText(GameStats gameStats) {
        int current = gameStats.getEventCount(mEvent);
        String text = StringUtils.format(mUnlockText, mCount);
        return StringUtils.format("%s (%d/%d)", text, current, mCount);
    }
}
