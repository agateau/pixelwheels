package com.agateau.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

/**
 * A turbo bonus
 */
public class TurboBonus extends BonusAdapter implements Pool.Poolable {
    public static class Pool extends BonusPool {
        public Pool(Assets assets, GameWorld gameWorld) {
            super(assets, gameWorld);
            setCounts(new float[]{0, 1, 2});
        }

        @Override
        protected Bonus newObject() {
            return new TurboBonus(this, mAssets);
        }
    }

    private final Pool mPool;
    private final Assets mAssets;

    private boolean mTriggered = false;
    private float mAnimationTime;

    private final Renderer mBonusRenderer = new Renderer() {
        @Override
        public void draw(Batch batch, int zIndex) {
            TextureRegion region = mAssets.turbo.getKeyFrame(mAnimationTime, true);
            Vehicle vehicle = mRacer.getVehicle();
            Body body = vehicle.getBody();
            Vector2 center = body.getPosition();
            float angle = body.getAngle() * MathUtils.radiansToDegrees;
            float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
            float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
            float refH = vehicle.getHeight() / 3;
            float x = center.x + refH * MathUtils.cosDeg(angle - 90);
            float y = center.y + refH * MathUtils.sinDeg(angle - 90);
            batch.draw(region,
                    x - w / 2, y - h / 2, // pos
                    w / 2, h / 2, // origin
                    w, h, // size
                    1, 1, // scale
                    angle);
        }
    };

    public TurboBonus(Pool pool, Assets assets) {
        mPool = pool;
        mAssets = assets;
        reset();
    }

    @Override
    public void reset() {
        mAnimationTime = 0;
        mTriggered = false;
    }

    @Override
    public void onPicked(Racer racer) {
        super.onPicked(racer);
        mRacer.getVehicleRenderer().addRenderer(mBonusRenderer);
    }

    @Override
    public TextureRegion getIconRegion() {
        return mAssets.turbo.getKeyFrame(0);
    }

    @Override
    public void onOwnerHit() {
        resetBonus();
    }

    @Override
    public void trigger() {
        if (!mTriggered) {
            mRacer.getVehicle().triggerTurbo();
            mTriggered = true;
        }
    }

    @Override
    public void act(float delta) {
        if (!mTriggered) {
            return;
        }
        mAnimationTime += delta;
        if (mAnimationTime > mAssets.turbo.getAnimationDuration()) {
            resetBonus();
        }
    }

    @Override
    public void aiAct(float delta) {
        mRacer.triggerBonus();
    }

    private void resetBonus() {
        mRacer.getVehicleRenderer().removeRenderer(mBonusRenderer);
        mPool.free(this);
        mRacer.resetBonus();
    }
}
