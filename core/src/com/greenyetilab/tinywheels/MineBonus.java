package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

/**
 * A mine which can be dropped behind the racer
 */
public class MineBonus extends BonusAdapter implements Pool.Poolable {

    private final Pool mPool;
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private Mine mMine;
    private boolean mTriggered;

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
        mTriggered = false;
    }

    @Override
    public TextureRegion getIconRegion() {
        return mAssets.findRegion("hud-mine");
    }

    @Override
    public void onPicked(Racer racer) {
        super.onPicked(racer);
        mMine = Mine.create(mGameWorld, mAssets, mRacer);
    }

    @Override
    public void trigger() {
        mTriggered = true;
    }

    @Override
    public void onOwnerHit() {
        mTriggered = true;
    }

    @Override
    public void act(float delta) {
        if (mTriggered) {
            mRacer.resetBonus();
            mMine.drop();
            mPool.free(this);
        }
    }

    @Override
    public void aiAct(float delta) {
        mRacer.triggerBonus();
    }
}
