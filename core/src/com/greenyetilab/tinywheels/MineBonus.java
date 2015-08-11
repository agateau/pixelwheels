package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;

/**
 * A mine which can be dropped behind the racer
 */
public class MineBonus extends BonusAdapter implements Pool.Poolable {
    private static final float AI_KEEP_BONUS_MIN_TIME = 2f;
    private static final float AI_KEEP_BONUS_MAX_TIME = 5f;

    private final Pool mPool;
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private Mine mMine;
    private boolean mTriggered;

    private float mAiKeepTime;

    public static class Pool extends BonusPool {
        public Pool(Assets assets, GameWorld gameWorld) {
            super(assets, gameWorld);
            setCounts(new float[]{2, 1, 0});
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
        mAiKeepTime = MathUtils.random(AI_KEEP_BONUS_MIN_TIME, AI_KEEP_BONUS_MAX_TIME);
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
        mAiKeepTime -= delta;
        if (mAiKeepTime <= 0) {
            mRacer.triggerBonus();
        }
    }
}
