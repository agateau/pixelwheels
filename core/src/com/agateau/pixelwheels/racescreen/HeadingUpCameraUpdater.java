/*
 * Copyright 2024 Compl Yue
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
package com.agateau.pixelwheels.racescreen;

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racer.Vehicle;
import com.badlogic.gdx.math.MathUtils;

/** A CameraUpdater tracking a racer */
class HeadingUpCameraUpdater extends CameraUpdater {
    private static final float MAX_ZOOM_SPEED = 75f;
    private static final float MIN_ZOOM = 0.6f;
    private static final float MAX_ZOOM = 2.1f;

    private final Racer mRacer;

    HeadingUpCameraUpdater(GameWorld world, Racer racer) {
        super(world);
        mRacer = racer;
    }

    @Override
    public void update(float delta) {
        Vehicle vehicle = mRacer.getVehicle();

        // Compute viewport size
        mNextCameraInfo.zoom =
                MathUtils.lerp(MIN_ZOOM, MAX_ZOOM, vehicle.getSpeed() / MAX_ZOOM_SPEED);
        limitZoomChange(delta);
        float viewportWidth = GamePlay.instance.viewportWidth * mNextCameraInfo.zoom;
        float viewportHeight = viewportWidth * mScreenHeight / mScreenWidth;
        mNextCameraInfo.viewportWidth = viewportWidth;
        mNextCameraInfo.viewportHeight = viewportHeight;

        if (vehicle.isFlying() || vehicle.isFalling()) {
            // keep camera from rotating
            mNextCameraInfo.cameraUp = mCameraInfo.cameraUp;
        } else {
            // reflect how the vehicle is turning
            final float steerMag = vehicle.isDrifting() ? 0.65f : 0.32f;
            final float bodyAngle = vehicle.getBody().getAngle();
            for (Vehicle.WheelInfo wi : vehicle.getWheelInfos()) {
                final float wheelAngle = wi.joint.getLowerLimit();
                final float targetUp = bodyAngle - steerMag * wheelAngle;
                mNextCameraInfo.cameraUp =
                        mCameraInfo.cameraUp
                                +
                                // smooth the camera turning
                                0.12f * (targetUp - mCameraInfo.cameraUp);
                break; // use only the 1st wheel,
                // assuming all wheels have the same angle
            }
        }

        // Compute pos
        float advance = Math.min(viewportWidth, viewportHeight) * Constants.CAMERA_ADVANCE_PERCENT;
        if (vehicle.isBraking()) {
            advance *= 0.8; // make feel the braking
            // TODO maybe more experiments about this
            // * shorten the distance gives the feel the camera dragged the car
            // * lengthen the distance gives the feel that the car dragged the camera
        }
        // smooth the camera moving
        mNextCameraInfo.cameraAhead =
                mCameraInfo.cameraAhead + 0.12f * (advance - mCameraInfo.cameraAhead);
        // calculate & set next camera position
        mNextCameraInfo
                .position
                .set(mNextCameraInfo.cameraAhead, 0)
                .rotateRad(mNextCameraInfo.cameraUp)
                .add(vehicle.getPosition());
        limitZoomChange(delta);
        applyChanges();
    }
}
