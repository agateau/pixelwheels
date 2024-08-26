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
package com.agateau.pixelwheels.gameinput;

import com.agateau.pixelwheels.GamePlay;
import com.badlogic.gdx.math.MathUtils;

public class DigitalSteering {
    private float mRawDirection = 0;

    public float steer(boolean left, boolean right) {
        if (left == right) {
            // Either both left & right are pressed or none of them are
            mRawDirection *= 0.4;
        } else if (left) {
            mRawDirection += GamePlay.instance.steeringStep;
        } else {
            mRawDirection -= GamePlay.instance.steeringStep;
        }
        mRawDirection = MathUtils.clamp(mRawDirection, -1, 1);
        return mRawDirection * mRawDirection * Math.signum(mRawDirection);
    }
}
