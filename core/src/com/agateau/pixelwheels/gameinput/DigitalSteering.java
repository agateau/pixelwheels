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
    private int mSign = 0;
    private float mSteering = 0;

    public float steer(boolean left, boolean right) {
        int sign;
        if (left == right) {
            sign = 0;
        } else if (right) {
            sign = -1;
        } else {
            sign = 1;
        }
        if (sign != mSign) {
            mSteering = 0;
        }
        if (sign != 0) {
            // Interesting effect
            // mSteering += GamePlay.instance.steeringStep
            mSteering = Math.min(mSteering + GamePlay.instance.steeringStep, 1f);
        }
        mSign = sign;
        if (mSign == 0) {
            return 0;
        }
        // mSteering: 0 -> 1
        // k: -pi/2 -> pi/2
        float k = mSteering * MathUtils.PI - MathUtils.HALF_PI;
        return (0.5f + MathUtils.sin(k) * 0.5f) * mSign;
    }
}
