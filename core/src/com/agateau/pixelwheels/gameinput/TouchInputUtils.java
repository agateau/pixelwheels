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
package com.agateau.pixelwheels.gameinput;

import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.racescreen.HudButton;
import com.badlogic.gdx.math.MathUtils;

class TouchInputUtils {
    static float applyDirectionInput(HudButton leftButton, HudButton rightButton, float direction) {
        if (leftButton.isPressed()) {
            direction += GamePlay.instance.steeringStep;
        } else if (rightButton.isPressed()) {
            direction -= GamePlay.instance.steeringStep;
        } else {
            direction = 0;
        }
        return MathUtils.clamp(direction, -1, 1);
    }
}
