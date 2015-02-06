package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

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

    public Vehicle create(String name, float originX, float originY, float angle) {
        Vehicle vehicle;
        if (name.equals("player")) {
            // Car
            TextureRegion carRegion = mAssets.findRegion("car/player");
            TextureRegion wheelRegion = mAssets.wheel;
            vehicle = new Vehicle(carRegion, mGameWorld, originX, originY);
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
        } else {
            TextureRegion region = mAssets.cars.get(MathUtils.random(mAssets.cars.size - 1));
            vehicle = new Vehicle(region, mGameWorld, originX, originY);

            // Wheels
            TextureRegion wheelRegion = mAssets.wheel;
            final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
            final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 46f;

            float wheelW = Constants.UNIT_FOR_PIXEL * wheelRegion.getRegionWidth();
            float rightX = vehicle.getWidth() / 2 - wheelW / 2 + 0.05f;
            float leftX = -rightX;
            float rearY = -vehicle.getHeight() / 2 + REAR_WHEEL_Y;
            float frontY = rearY + WHEEL_BASE;

            Vehicle.WheelInfo info;
            info = vehicle.addWheel(wheelRegion, leftX, frontY);
            info.steeringFactor = 1;
            info = vehicle.addWheel(wheelRegion, rightX, frontY);
            info.steeringFactor = 1;
            info = vehicle.addWheel(wheelRegion, leftX, rearY);
            info.wheel.setCanDrift(true);
            info = vehicle.addWheel(wheelRegion, rightX, rearY);
            info.wheel.setCanDrift(true);
        }
        // Set angle *after* adding the wheels!
        vehicle.setInitialAngle(angle);

        return vehicle;
    }
}
