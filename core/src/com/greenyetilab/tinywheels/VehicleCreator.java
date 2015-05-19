package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Create a Vehicle from VehicleIO data
 */
public class VehicleCreator {
    private final GameWorld mGameWorld;
    private final Assets mAssets;

    public VehicleCreator(Assets assets, GameWorld gameWorld) {
        mGameWorld = gameWorld;
        mAssets = assets;
    }

    public Vehicle create(VehicleDef vehicleDef, Vector2 position, float startAngle) {
        final float U = Constants.UNIT_FOR_PIXEL;
        float maxDrivingForce = GamePlay.instance.maxDrivingForce * vehicleDef.speed;

        TextureRegion mainRegion = mAssets.findRegion("vehicles/" + vehicleDef.mainImage);
        TextureRegion wheelRegion = mAssets.wheel;

        Vehicle vehicle = new Vehicle(mainRegion, mGameWorld, position.x, position.y);
        vehicle.setName(vehicleDef.name);

        for (AxleDef axle : vehicleDef.axles) {
            float width = axle.width * U;
            float y = (axle.y - mainRegion.getRegionHeight() / 2) * U;
            float steer = axle.steer;
            float drive = maxDrivingForce * axle.drive;

            createWheel(vehicle, wheelRegion, width / 2, y, steer, drive);
            createWheel(vehicle, wheelRegion, -width / 2, y, steer, drive);
        }
        vehicle.setInitialAngle(startAngle);
        return vehicle;
    }

    private void createWheel(Vehicle vehicle, TextureRegion region, float x, float y, float steer, float drive) {
        Vehicle.WheelInfo info = vehicle.addWheel(region, x, y);
        info.steeringFactor = steer;
        info.wheel.setCanDrift(true);
        info.wheel.setMaxDrivingForce(drive);
    }
}
