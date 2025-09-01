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
package com.agateau.pixelwheels.racescreen;

import com.agateau.pixelwheels.Assets;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/** Indicate an input zone on the hud */
public class PieButton extends HudButton {
    private float mFrom = 0f;
    private float mTo = 90f;

    /** name is a string like "left" or "right" */
    public PieButton(Assets assets, Hud hud, String name) {
        super(assets, hud, name);
    }

    public void setSector(int from, int to) {
        mFrom = from;
        mTo = to;
    }

    private boolean isOnTheRight() {
        return mFrom >= 90;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        // Check hit is below the pause button
        if (y >= getHud().getInputUiHeight()) {
            return null;
        }

        // Check hit is on the same side as the button
        if (isOnTheRight()) {
            // Adjust x to be relative to the right edge
            x -= getWidth();
            if (x <= -getHud().getInputUiWidth() / 2) {
                return null;
            }
        } else {
            if (x >= getHud().getInputUiWidth() / 2) {
                return null;
            }
        }

        // Now check if we hit the right sector
        float angle = MathUtils.atan2(y, x) * MathUtils.radDeg;
        if (mFrom <= angle && angle <= mTo) {
            return this;
        } else {
            return null;
        }
    }
}
