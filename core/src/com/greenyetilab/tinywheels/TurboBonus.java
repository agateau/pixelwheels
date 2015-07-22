package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Pool;
import com.greenyetilab.utils.log.NLog;

/**
 * A turbo bonus
 */
public class TurboBonus extends BonusAdapter implements Pool.Poolable {
    private static final float DURATION = 0.5f;
    private static final float STRENGTH = 100;

    public static class Pool extends BonusPool {
        public Pool(Assets assets, GameWorld gameWorld) {
            super(assets, gameWorld);
        }

        @Override
        protected Bonus newObject() {
            return new TurboBonus(this, mAssets, mGameWorld);
        }
    }

    private final Pool mPool;
    private final Assets mAssets;
    private final GameWorld mGameWorld;

    private boolean mTriggered;
    private float mRemainingTime;
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
            float refW = vehicle.getWidth();
            float refH = vehicle.getHeight() / 2;
            float x = center.x + refH * MathUtils.cosDeg(angle - 90);
            float y = center.y + refH * MathUtils.sinDeg(angle - 90);
            batch.draw(region,
                    x - w / 2, y - h, // pos
                    w / 2, h, // origin
                    w, h, // size
                    1, 1, // scale
                    angle);
        }
    };

    public TurboBonus(Pool pool, Assets assets, GameWorld gameWorld) {
        mPool = pool;
        mAssets = assets;
        mGameWorld = gameWorld;
        reset();
    }

    @Override
    public void reset() {
        mTriggered = false;
        mAnimationTime = 0;
        mRemainingTime = DURATION;
    }

    @Override
    public TextureRegion getIconRegion() {
        return mAssets.findRegion("hud-fire");
    }

    @Override
    public void onOwnerHit() {
        resetBonus();
    }

    @Override
    public void trigger() {
        if (!mTriggered) {
            mTriggered = true;
            mRacer.getVehicleRenderer().addRenderer(mBonusRenderer);
        }
    }

    @Override
    public void act(float delta) {
        if (!mTriggered) {
            return;
        }
        mAnimationTime += delta;
        Box2DUtils.applyDrag(mRacer.getVehicle().getBody(), -STRENGTH);

        mRemainingTime -= delta;
        if (mRemainingTime < 0) {
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
