/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.tinywheels;

import com.agateau.tinywheels.sound.AudioManager;
import com.badlogic.gdx.utils.Pool;
import com.agateau.utils.GylMathUtils;

/**
 * Pool of bonus instances
 */
public abstract class BonusPool extends Pool<Bonus> {
    protected final Assets mAssets;
    protected final GameWorld mGameWorld;
    protected final AudioManager mAudioManager;
    private float[] mCounts;

    public BonusPool(Assets assets, GameWorld gameWorld, AudioManager audioManager) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mAudioManager = audioManager;
    }

    /**
     * Defines how often the bonus may be picked up
     * This is used by getCountForNormalizedRank so the array is a set of values where the lowest is
     * used for normalizedRank == 0 and the highest for normalizedRank == 1
     */
    void setCounts(float[] counts) {
        mCounts = counts;
    }

    /**
     * How many times the bonus may be picked up
     * normalizedRank goes from 0 to 1, so when racer is 1st, normalizedRank is 0, when racer is
     * last, normalizedRank is 1
     */
    float getCountForNormalizedRank(float normalizedRank) {
        return GylMathUtils.arrayLerp(mCounts, normalizedRank);
    }
}
