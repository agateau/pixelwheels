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
package com.agateau.pixelwheels.racer;

import com.agateau.pixelwheels.Assets;

/** Make a vehicle slow down for a short duration */
public class DisruptedComponent implements Racer.Component {
    private static final float DURATION = 1f;
    private final Assets mAssets;
    private final Racer mRacer;
    private boolean mActive = false;
    private float mRemainingDuration = 0;

    public DisruptedComponent(Assets assets, Racer racer) {
        mAssets = assets;
        mRacer = racer;
    }

    public boolean isActive() {
        return mActive;
    }

    public float getNormalizedDuration() {
        return mRemainingDuration / DURATION;
    }

    public void start() {
        mRemainingDuration = DURATION;
        if (mActive) {
            return;
        }
        mActive = true;
    }

    @Override
    public void act(float delta) {
        if (!mActive) {
            return;
        }
        mRemainingDuration -= delta;
        if (mRemainingDuration <= 0) {
            stop();
        }
    }

    private void stop() {
        mRemainingDuration = 0;
        mActive = false;
    }
}
