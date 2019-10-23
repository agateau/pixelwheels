/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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
import com.badlogic.gdx.math.Vector2;

class SinglePlayerCameraUpdater extends CameraUpdater {
    private static final float MAX_ZOOM_SPEED = 75f;
    private static final float MIN_ZOOM = 0.6f;
    private static final float MAX_ZOOM = 2.1f;

    private final Vector2 sDelta = new Vector2();

    SinglePlayerCameraUpdater(GameWorld world) {
        super(world);
    }

    @Override
    public void update(float delta) {
        boolean immediate = delta < 0;
        Racer racer = mWorld.getPlayerRacers().first();
        Vehicle vehicle = racer.getVehicle();

        // Compute viewport size
        mNextCameraInfo.zoom =
                MathUtils.lerp(MIN_ZOOM, MAX_ZOOM, vehicle.getSpeed() / MAX_ZOOM_SPEED);
        limitZoomChange(delta);
        float viewportWidth = GamePlay.instance.viewportWidth * mNextCameraInfo.zoom;
        float viewportHeight = viewportWidth * mScreenHeight / mScreenWidth;
        mNextCameraInfo.viewportWidth = viewportWidth;
        mNextCameraInfo.viewportHeight = viewportHeight;

        // Compute pos
        float advance = Math.min(viewportWidth, viewportHeight) * Constants.CAMERA_ADVANCE_PERCENT;
        sDelta.set(advance, 0)
                .rotate(racer.getCameraAngle())
                .add(vehicle.getPosition())
                .sub(mCameraInfo.position);
        mNextCameraInfo.position.set(mCameraInfo.position).add(sDelta);
        if (!immediate) {
            sDelta.limit(MAX_CAMERA_DELTA * delta);
        }
        limitZoomChange(delta);
        applyChanges();
    }
}
