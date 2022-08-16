/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.vehicledef;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.racer.Vehicle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/** Create a Vehicle from VehicleIO data */
public class VehicleCreator {
    private final GameWorld mGameWorld;
    private final Assets mAssets;

    public VehicleCreator(Assets assets, GameWorld gameWorld) {
        mGameWorld = gameWorld;
        mAssets = assets;
    }

    private final Vector2 sWheelPos = new Vector2();

    public Vehicle create(VehicleDef vehicleDef, Vector2 position, float angle) {
        final float U = Constants.UNIT_FOR_PIXEL;
        float maxDrivingForce = GamePlay.instance.maxDrivingForce * vehicleDef.speed;

        TextureRegion mainRegion = vehicleDef.getImage(mAssets);

        Vehicle vehicle =
                new Vehicle(mAssets, mGameWorld, position.x, position.y, vehicleDef, angle);

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
            float wheelX = (axle.y - mainRegion.getRegionWidth() / 2f) * U;
            float drive = maxDrivingForce * axle.drive;

            TextureRegion wheelRegion = axle.getTexture(mAssets);

            // Left wheel
            sWheelPos.set(wheelX, wheelY).rotate(angle);
            createWheel(vehicle, wheelRegion, sWheelPos.x, sWheelPos.y, axle, drive, angle);

            // Right wheel
            sWheelPos.set(wheelX, -wheelY).rotate(angle);
            createWheel(vehicle, wheelRegion, sWheelPos.x, sWheelPos.y, axle, drive, angle);
        }
        return vehicle;
    }

    private void createWheel(
            Vehicle vehicle,
            TextureRegion region,
            float x,
            float y,
            AxleDef axle,
            float drive,
            float angle) {
        Vehicle.WheelInfo info = vehicle.addWheel(region, x, y, angle);
        info.steeringFactor = axle.steer;
        info.wheel.setCanDrift(axle.drift);
        info.wheel.setMaxDrivingForce(drive);
    }
}
