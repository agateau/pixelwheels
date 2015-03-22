package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

/**
 * A gun bonus
 */
public class GunBonus implements Bonus, Pool.Poolable {
    public static class Pool extends BonusPool {
        public Pool(Assets assets, GameWorld gameWorld) {
            super(assets, gameWorld);
        }

        @Override
        protected Bonus newObject() {
            return new GunBonus(mAssets, mGameWorld);
        }
    }

    public GunBonus(Assets assets, GameWorld gameWorld) {

    }

    @Override
    public void reset() {

    }

    @Override
    public TextureRegion getIconRegion() {
        return null;
    }
}
