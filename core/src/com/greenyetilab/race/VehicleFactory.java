package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Generate Vehicles
 */
public class VehicleFactory {
    private final Assets mAssets;
    private final GameWorld mGameWorld;

    public VehicleFactory(Assets assets, GameWorld gameWorld) {
        mAssets = assets;
        mGameWorld = gameWorld;
    }

    public Vehicle create(String name, float originX, float originY) {
        // Car
        TextureRegion carRegion = mAssets.findRegion("car/player");
        TextureRegion wheelRegion = mAssets.wheel;
        Vehicle vehicle = new Vehicle(carRegion, mGameWorld, originX, originY);
        vehicle.setUserData(this);
        //mVehicle.setLimitAngle(true);
        //mVehicle.setCorrectAngle(true);

        // Wheels
        final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
        final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 42f;

        float wheelW = Constants.UNIT_FOR_PIXEL * wheelRegion.getRegionWidth();
        float rightX = Constants.UNIT_FOR_PIXEL * carRegion.getRegionWidth() / 2 - wheelW / 2 + 0.05f;
        float leftX = -rightX;
        float rearY = Constants.UNIT_FOR_PIXEL * -carRegion.getRegionHeight() / 2 + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE + 0.2f;

        Vehicle.WheelInfo info;
        info = vehicle.addWheel(wheelRegion, leftX, frontY);
        info.steeringFactor = 1;
        info.wheel.setCanDrift(true);
        info = vehicle.addWheel(wheelRegion, rightX, frontY);
        info.steeringFactor = 1;
        info.wheel.setCanDrift(true);
        info = vehicle.addWheel(wheelRegion, leftX, rearY);
        info.wheel.setCanDrift(true);
        info = vehicle.addWheel(wheelRegion, rightX, rearY);
        info.wheel.setCanDrift(true);

        return vehicle;
    }
}
