/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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

import static com.agateau.translations.Translator.tr;

import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.utils.Assert;

/** A RewardRule to unlock based on championship rank */
public class ChampionshipRankRewardRule implements RewardRule {
    private final Championship mChampionship;
    private final int mRank;

    public ChampionshipRankRewardRule(Championship championship, int rank) {
        mChampionship = championship;
        mRank = rank;
        Assert.check(0 <= rank && rank <= 2, "Rank must be between 0 and 2");
    }

    @Override
    public boolean hasBeenUnlocked(GameStats gameStats) {
        return gameStats.getBestChampionshipRank(mChampionship) <= mRank;
    }

    @Override
    public String getUnlockText(GameStats gameStats) {
        String msg;
        if (mRank == 2) {
            msg = tr("Finish third or better at %s championship");
        } else if (mRank == 1) {
            msg = tr("Finish second or better at %s championship");
        } else {
            // mRank is 0 here, because of the check in the constructor
            msg = tr("Finish first at %s championship");
        }
        return StringUtils.format(msg, mChampionship.getName());
    }
}
