package com.agateau.tinywheels;

import com.badlogic.gdx.utils.Pool;
import com.agateau.utils.GylMathUtils;

/**
 * Pool of bonus instances
 */
public abstract class BonusPool extends Pool<Bonus> {
    protected final Assets mAssets;
    protected final GameWorld mGameWorld;
    private float[] mCounts;

    public BonusPool(Assets assets, GameWorld gameWorld) {
        mAssets = assets;
        mGameWorld = gameWorld;
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
