/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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

import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.map.Track;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

abstract class CameraUpdater {
    static final float IMMEDIATE = -1;

    final GameWorld mWorld;
    private OrthographicCamera mCamera;
    int mScreenWidth;
    int mScreenHeight;

    static class CameraInfo {
        float viewportWidth;
        float viewportHeight;
        Vector2 position = new Vector2();
        float zoom = 1;

        void clampPosition(Track track) {
            float minWidth = viewportWidth / 2;
            float minHeight = viewportHeight / 2;
            float maxWidth = track.getMapWidth() - viewportWidth / 2;
            float maxHeight = track.getMapHeight() - viewportHeight / 2;
            position.x = MathUtils.clamp(position.x, minWidth, maxWidth);
            position.y = MathUtils.clamp(position.y, minHeight, maxHeight);
        }
    }

    CameraInfo mCameraInfo = new CameraInfo();
    CameraInfo mNextCameraInfo = new CameraInfo();

    CameraUpdater(GameWorld world) {
        mWorld = world;
    }

    public void init(OrthographicCamera camera, int screenWidth, int screenHeight) {
        mCamera = camera;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
    }

    public abstract void update(float delta);

    void applyChanges() {
        mNextCameraInfo.clampPosition(mWorld.getTrack());

        // Apply changes
        mCamera.viewportWidth = mNextCameraInfo.viewportWidth;
        mCamera.viewportHeight = mNextCameraInfo.viewportHeight;
        mCamera.position.set(mNextCameraInfo.position, 0);
        mCamera.update();

        // Swap instances
        CameraInfo tmp = mCameraInfo;
        mCameraInfo = mNextCameraInfo;
        mNextCameraInfo = tmp;
    }
}
