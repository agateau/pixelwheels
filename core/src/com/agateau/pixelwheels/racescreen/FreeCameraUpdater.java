/*
 * Copyright 2020 Aurélien Gâteau <mail@agateau.com>
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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;

/** A camera updater controlled using VIM keys and +/- for zoom, used for debugging */
class FreeCameraUpdater extends CameraUpdater {
    private static final float MIN_ZOOM = 0.1f;
    private static final float MAX_ZOOM = 10f;
    private static final float UNITS_PER_SECOND = 400f * Constants.UNIT_FOR_PIXEL;
    private static final float ZOOM_PER_SECOND = 5f;
    private static final float SHIFT_FACTOR = 10f;

    FreeCameraUpdater(GameWorld world) {
        super(world);
    }

    @Override
    public void update(float delta) {
        float dx = 0;
        float dy = 0;
        float dz = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            dx = -UNITS_PER_SECOND * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.L)) {
            dx = UNITS_PER_SECOND * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            dy = -UNITS_PER_SECOND * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.K)) {
            dy = UNITS_PER_SECOND * delta;
        }
        if (isKeyPressed(Input.Keys.MINUS, Input.Keys.NUM_6)) {
            dz = ZOOM_PER_SECOND * delta;
        } else if (isKeyPressed(Input.Keys.PLUS, Input.Keys.EQUALS)) {
            dz -= ZOOM_PER_SECOND * delta;
        }
        if (isKeyPressed(Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT)) {
            dx *= SHIFT_FACTOR;
            dy *= SHIFT_FACTOR;
            dz *= SHIFT_FACTOR;
        }

        // Compute viewport size
        mNextCameraInfo.zoom = MathUtils.clamp(mCameraInfo.zoom + dz, MIN_ZOOM, MAX_ZOOM);
        float viewportWidth = GamePlay.instance.viewportWidth * mNextCameraInfo.zoom;
        float viewportHeight = viewportWidth * mScreenHeight / mScreenWidth;
        mNextCameraInfo.viewportWidth = viewportWidth;
        mNextCameraInfo.viewportHeight = viewportHeight;

        // Compute pos
        mNextCameraInfo.position.x = mCameraInfo.position.x + dx;
        mNextCameraInfo.position.y = mCameraInfo.position.y + dy;
        applyChanges();
    }

    private static boolean isKeyPressed(int k1, int k2) {
        return Gdx.input.isKeyPressed(k1) || Gdx.input.isKeyPressed(k2);
    }
}
