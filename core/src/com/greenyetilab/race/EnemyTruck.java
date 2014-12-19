package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * A truck which drops gifts when destroyed
 */
public class EnemyTruck extends PendingVehicle {
    private final Assets mAssets;
    public EnemyTruck(Assets assets, GameWorld world, float originX, float originY) {
        super(assets.findRegion("truck"), world, originX, originY);
        mAssets = assets;
        setPilot(new BasicPilot(mGameWorld.getMapInfo(), this));
        setInitialHealth(4);

        // Wheels
        TextureRegion wheelRegion = assets.wheel;
        final float U = Constants.UNIT_FOR_PIXEL;
        final float REAR_WHEEL_Y = U * 19f;
        final float WHEEL_BASE = U * 63f;

        float rightX = U * 19f;
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
    }

    @Override
    protected void onHealthDecreased() {
        Gift.drop(mAssets, mGameWorld, getX(), getY(), MathUtils.random(60f, 120f));
    }
}
