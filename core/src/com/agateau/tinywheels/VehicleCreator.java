package com.agateau.tinywheels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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

    Vector2 sWheelPos = new Vector2();
    public Vehicle create(VehicleDef vehicleDef, Vector2 position, float angle) {
        final float U = Constants.UNIT_FOR_PIXEL;
        float maxDrivingForce = GamePlay.instance.maxDrivingForce * vehicleDef.speed;

        TextureRegion mainRegion = mAssets.findRegion("vehicles/" + vehicleDef.mainImage);
        TextureRegion wheelRegion = mAssets.wheel;

        Vehicle vehicle = new Vehicle(mainRegion, mGameWorld, position.x, position.y, angle);
        vehicle.setName(vehicleDef.name);
        vehicle.setId(vehicleDef.id);

        for (AxleDef axle : vehicleDef.axles) {
            /*
              axle assumes the vehicle is facing top, like this:

               ____
              /    \
             []----[] ^
              |    |  |
              |    |  | axle.y
             []----[] |
              |____|  |

              <---->
               axle.width

              The body, on the other hand, assumes that if angle is 0, the vehicle is facing right.
              We have to swap coordinates to take this into account.
             */
            float wheelY = axle.width * U / 2;
            float wheelX = (axle.y - mainRegion.getRegionWidth() / 2) * U;
            float drive = maxDrivingForce * axle.drive;

            // Left wheel
            sWheelPos.set(wheelX, wheelY).rotate(angle);
            createWheel(vehicle, wheelRegion, sWheelPos.x, sWheelPos.y, axle, drive, angle);

            // Right wheel
            sWheelPos.set(wheelX, -wheelY).rotate(angle);
            createWheel(vehicle, wheelRegion, sWheelPos.x, sWheelPos.y, axle, drive, angle);
        }
        return vehicle;
    }

    private void createWheel(Vehicle vehicle, TextureRegion region, float x, float y, AxleDef axle, float drive, float angle) {
        Vehicle.WheelInfo info = vehicle.addWheel(region, x, y, angle);
        info.steeringFactor = axle.steer;
        info.wheel.setCanDrift(axle.drift);
        info.wheel.setMaxDrivingForce(drive);
    }
}
