package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A truck which drops gift when destroyed
 */
public class EnemyTruck extends EnemyCar {
    public EnemyTruck(Assets assets, GameWorld world, float originX, float originY) {
        super(assets.findRegion("truck"), world, originX, originY);
        setPilot(new BasicPilot(mGameWorld.getMapInfo(), this));

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
}
