package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * Generate Vehicles
 */
public class VehicleFactory {
    private final Assets mAssets;
    private final GameWorld mGameWorld;

    private static class EnemyInfo {
        private final String name;
        private final float speed;

        public EnemyInfo(String name, float speed) {
            this.name = name;
            this.speed = speed;
        }
    }

    private EnemyInfo[] mEnemyInfos = new EnemyInfo[] {
            new EnemyInfo("Ice Man", 1),
            new EnemyInfo("Purple", 1),
            new EnemyInfo("Martin", 0.6f),
            new EnemyInfo("Red Bob", 0.8f),
            new EnemyInfo("Yellow Star", 0.9f),
            new EnemyInfo("Enzo", 1f),
    };

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

            vehicle.setName("You");
        } else {
            int carId = MathUtils.random(mAssets.cars.size - 1);
            EnemyInfo enemyInfo = mEnemyInfos[carId];
            TextureRegion region = mAssets.cars.get(carId);
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

            float maxDrivingForce = GamePlay.instance.maxDrivingForce * enemyInfo.speed;
            Vehicle.WheelInfo info;
            info = vehicle.addWheel(wheelRegion, leftX, frontY);
            info.wheel.setMaxDrivingForce(maxDrivingForce);
            info.steeringFactor = 1;
            info = vehicle.addWheel(wheelRegion, rightX, frontY);
            info.wheel.setMaxDrivingForce(maxDrivingForce);
            info.steeringFactor = 1;
            info = vehicle.addWheel(wheelRegion, leftX, rearY);
            info.wheel.setMaxDrivingForce(maxDrivingForce);
            info.wheel.setCanDrift(true);
            info = vehicle.addWheel(wheelRegion, rightX, rearY);
            info.wheel.setMaxDrivingForce(maxDrivingForce);
            info.wheel.setCanDrift(true);

            vehicle.setName(enemyInfo.name);
        }
        // Set angle *after* adding the wheels!
        vehicle.setInitialAngle(angle);

        return vehicle;
    }
}
