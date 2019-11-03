/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.bonus;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.utils.ReflectionPool;

/** Pool of bonus instances */
public class BonusPool<T extends Bonus> extends ReflectionPool<T> {
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final AudioManager mAudioManager;
    private float[] mCounts;

    public BonusPool(Class<T> type, Assets assets, GameWorld gameWorld, AudioManager audioManager) {
        super(type);
        mAssets = assets;
        mGameWorld = gameWorld;
        mAudioManager = audioManager;
    }

    /**
     * Defines how often the bonus may be picked up.
     *
     * <p>This is used by getCountForNormalizedRank so the array is a set of values where the lowest
     * is used for normalizedRank == 0 and the highest for normalizedRank == 1
     */
    public void setCounts(float[] counts) {
        mCounts = counts;
    }

    /**
     * How many times the bonus may be picked up.
     *
     * <p>normalizedRank goes from 0 to 1, so when racer is 1st, normalizedRank is 0, when racer is
     * last, normalizedRank is 1
     */
    public float getCountForNormalizedRank(float normalizedRank) {
        return AgcMathUtils.arrayLerp(mCounts, normalizedRank);
    }

    public GameWorld getGameWorld() {
        return mGameWorld;
    }

    @Override
    protected T newObject() {
        T object = super.newObject();
        object.init(this, mAssets, mGameWorld, mAudioManager);
        return object;
    }
}
