package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * An enemy car
 */
public class EnemyCar extends Vehicle implements DisposableWhenOutOfSight {
    private static final float ACTIVE_EXTRA_HEIGHT = Constants.VIEWPORT_WIDTH / 2;

    public EnemyCar(GameWorld world, Assets assets, float originX, float originY) {
        super(selectCarTextureRegion(assets), world, originX, originY);

        // Wheels
        TextureRegion wheelRegion = assets.wheel;
        final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
        final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 46f;

        float wheelW = Constants.UNIT_FOR_PIXEL * wheelRegion.getRegionWidth();
        float rightX = getWidth() / 2 - wheelW / 2 + 0.05f;
        float leftX = -rightX;
        float rearY = -getHeight() / 2 + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE;

        Vehicle.WheelInfo info;
        info = addWheel(wheelRegion, leftX, frontY);
        info.steeringFactor = 1;
        info = addWheel(wheelRegion, rightX, frontY);
        info.steeringFactor = 1;
        info = addWheel(wheelRegion, leftX, rearY);
        info.wheel.setCanDrift(true);
        info = addWheel(wheelRegion, rightX, rearY);
        info.wheel.setCanDrift(true);

        mBody.setAwake(false);
    }

    public void setDrivingAngle(float angle) {
        angle = (angle - 90) * MathUtils.degreesToRadians;
        mBody.setTransform(mBody.getPosition(), angle);
    }

    @Override
    public boolean act(float dt) {
        float bottomY = mGameWorld.getBottomVisibleY() - ACTIVE_EXTRA_HEIGHT;
        float topY = mGameWorld.getTopVisibleY() + ACTIVE_EXTRA_HEIGHT;
        boolean isActive = bottomY <= getY() && getY() <= topY;
        if (isActive) {
            mBody.setAwake(true);
        } else {
            mBody.setAwake(false);
        }
        return super.act(dt);
    }

    private static TextureRegion selectCarTextureRegion(Assets assets) {
        return assets.cars.get(MathUtils.random(assets.cars.size - 1));
    }
}
