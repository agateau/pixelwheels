package com.greenyetilab.race;

import com.badlogic.gdx.utils.Pool;

/**
 * Pool of bonus instances
 */
public abstract class BonusPool extends Pool<Bonus> {
    protected final Assets mAssets;
    protected final GameWorld mGameWorld;

    public BonusPool(Assets assets, GameWorld gameWorld) {
        mAssets = assets;
        mGameWorld = gameWorld;
    }
}
