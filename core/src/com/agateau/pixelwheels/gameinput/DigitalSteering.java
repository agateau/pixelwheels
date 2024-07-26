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

    public float direction() {
        // use parabolic curve to smooth the ctrl
        return mRawDirection * mRawDirection * Math.signum(mRawDirection);
    }

    public float steer(boolean left, boolean right) {
        if (left && right) {
            // both left and right, full steer in original direction.
            // currently there are 2 folds to do it this way:
            //
            // * with two-sides button control, I seldom purposfully do a
            // straight braking/slow-down, almost always meant to reverse at
            // a direction, then I always press the other button too quickly
            // after pressed the 1st button, making it reverse almost
            // straightly, the treatment here meant to make that op easy.
            // also there are times I'd mean slow-down & turn-hard, this case
            // works pretty well like a trick - just release the other button
            // after the speed is sufficiently slowed down, and keep holding
            // the 1st button to keep hard turning toward the original direction
            // using full steering during the whole course.
            //
            // * for kbd control (maybe also pie-touch), this happens to enable
            // a hard-turning trick - the player first press the button for the
            // target direction, immediately followed by a press on the other
            // button, it'll skip the gradually increasing steering output, and
            // do an instant, full steering.
            mRawDirection = Math.signum(mRawDirection);
        } else if (left) {
            mRawDirection += GamePlay.instance.steeringStep;
        } else if (right) {
            mRawDirection -= GamePlay.instance.steeringStep;
        } else {
            mRawDirection *= 0.4;
        }
        mRawDirection = MathUtils.clamp(mRawDirection, -1, 1);
        return this.direction();
    }
}
