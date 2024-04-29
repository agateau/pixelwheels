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

import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.map.Track;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

abstract class CameraUpdater {
    static final float IMMEDIATE = -1;
    private static final float MAX_ZOOM_DELTA = 0.6f;
    static final float MAX_CAMERA_DELTA = 180;

    final GameWorld mWorld;
    private OrthographicCamera mCamera;
    int mScreenWidth;
    int mScreenHeight;

    static class CameraInfo {
        float viewportWidth;
        float viewportHeight;
        Vector2 position = new Vector2();
        float cameraUp = 0;
        float cameraAhead = 0;
        float zoom = 1;

        void clampPositionToTrack(Track track) {
            float minWidth = viewportWidth / 2;
            float minHeight = viewportHeight / 2;
            float maxWidth = track.getMapWidth() - viewportWidth / 2;
            float maxHeight = track.getMapHeight() - viewportHeight / 2;
            if (minWidth < maxWidth) {
                position.x = MathUtils.clamp(position.x, minWidth, maxWidth);
            } else {
                position.x = track.getMapWidth() / 2;
            }
            if (minHeight < maxHeight) {
                position.y = MathUtils.clamp(position.y, minHeight, maxHeight);
            } else {
                position.y = track.getMapHeight() / 2;
            }
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
        // Apply changes
        if (GamePlay.instance.headingUpCamera) {
            mCamera.up.set(
                    (float) Math.cos(mNextCameraInfo.cameraUp),
                    (float) Math.sin(mNextCameraInfo.cameraUp),
                    0);
        } else {
            mNextCameraInfo.clampPositionToTrack(mWorld.getTrack());
        }
        mCamera.viewportWidth = mNextCameraInfo.viewportWidth;
        mCamera.viewportHeight = mNextCameraInfo.viewportHeight;
        mCamera.position.set(mNextCameraInfo.position, 0);
        mCamera.update();

        // Swap instances
        CameraInfo tmp = mCameraInfo;
        mCameraInfo = mNextCameraInfo;
        mNextCameraInfo = tmp;
    }

    void limitZoomChange(float delta) {
        if (delta < 0) {
            return;
        }
        float zoomDelta = MAX_ZOOM_DELTA * delta;
        mNextCameraInfo.zoom =
                MathUtils.clamp(
                        mNextCameraInfo.zoom,
                        mCameraInfo.zoom - zoomDelta,
                        mCameraInfo.zoom + zoomDelta);
    }
}
