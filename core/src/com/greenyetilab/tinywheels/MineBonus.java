package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

/**
 * A mine which can be dropped behind the racer
 */
public class MineBonus implements Bonus, Pool.Poolable {

    private final Pool mPool;
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private Racer mRacer;
    private Mine mMine;

    public static class Pool extends BonusPool {
        public Pool(Assets assets, GameWorld gameWorld) {
            super(assets, gameWorld);
        }

        @Override
        protected Bonus newObject() {
            return new MineBonus(this, mAssets, mGameWorld);
        }
    }

    public MineBonus(Pool pool, Assets assets, GameWorld gameWorld) {
        mPool = pool;
        mAssets = assets;
        mGameWorld = gameWorld;
    }

    @Override
    public void reset() {

    }

    @Override
    public TextureRegion getIconRegion() {
        return mAssets.findRegion("hud-mine");
    }

    @Override
    public void onPicked(Racer racer) {
        mRacer = racer;
        mMine = Mine.create(mGameWorld, mAssets, mRacer);
    }

    @Override
    public void trigger() {
        mMine.drop();
        mPool.free(this);
    }

    @Override
    public void aiAct(float delta) {
        mRacer.triggerBonus();
    }
}
