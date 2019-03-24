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
import com.badlogic.gdx.math.Vector2;

class MultiPlayerCameraUpdater extends CameraUpdater {
    MultiPlayerCameraUpdater(GameWorld world) {
        super(world);
    }

    @Override
    public void update(float delta) {
        // Compute viewport size
        float viewportWidth = GamePlay.instance.viewportWidth;
        float viewportHeight = viewportWidth * mScreenHeight / mScreenWidth;

        // Compute rect containing all players
        float x1 = mWorld.getTrack().getMapWidth();
        float y1 = mWorld.getTrack().getMapHeight();
        float x2 = 0;
        float y2 = 0;
        for (Racer racer : mWorld.getPlayerRacers()) {
            Vector2 pos = racer.getVehicle().getPosition();
            x1 = Math.min(x1, pos.x);
            x2 = Math.max(x2, pos.x);
            y1 = Math.min(y1, pos.y);
            y2 = Math.max(y2, pos.y);
        }
        float padding = Constants.CAMERA_ADVANCE_PERCENT * viewportWidth;
        x1 -= padding;
        y1 -= padding;
        x2 += padding;
        y2 += padding;

        // Compute pos
        mNextCameraInfo.position.set((x1 + x2) / 2, (y1 + y2) / 2);
        mNextCameraInfo.zoom = Math.max((x2 - x1) / viewportWidth, (y2 - y1) / viewportHeight);
        limitZoomChange(delta);
        mNextCameraInfo.viewportWidth = viewportWidth * mNextCameraInfo.zoom;
        mNextCameraInfo.viewportHeight = viewportHeight * mNextCameraInfo.zoom;
        applyChanges();
    }
}
