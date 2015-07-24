package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Handles the Turbo
 */
public class TurboComponent {
    private static final float DURATION = 0.25f;

    private final Assets mAssets;
    private final Racer mRacer;
    private float mRemainingTime;
    private float mAnimationTime = 0;

    private final Renderer mTurboRenderer = new Renderer() {
        @Override
        public void draw(Batch batch, int zIndex) {
            TextureRegion region = mAssets.turboFlame.getKeyFrame(mAnimationTime, true);
            Vehicle vehicle = mRacer.getVehicle();
            Body body = vehicle.getBody();
            Vector2 center = body.getPosition();
            float angle = body.getAngle() * MathUtils.radiansToDegrees;
            float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
            float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
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

    public TurboComponent(Assets assets, Racer racer) {
        mAssets = assets;
        mRacer = racer;
    }

    public void act(float delta) {
        if (mRemainingTime <= 0) {
            return;
        }
        mAnimationTime += delta;
        mRemainingTime -= delta;
        if (mRemainingTime > 0) {
            float turbo = Interpolation.pow2.apply(mRemainingTime / DURATION);
            mRacer.getVehicle().setTurbo(turbo);
        } else {
            mRacer.getVehicle().setTurbo(0);
            mRacer.getVehicleRenderer().removeRenderer(mTurboRenderer);
        }
    }

    public void trigger() {
        if (mRemainingTime <= 0) {
            mRacer.getVehicleRenderer().addRenderer(mTurboRenderer);
            mAnimationTime = 0;
        }
        mRemainingTime = DURATION;
    }
}
