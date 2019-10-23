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
    private float mRadius = 100;

    /** name is a string like "left" or "right" */
    public PieButton(Assets assets, Hud hud, String name) {
        super(assets, hud, name);
    }

    public void setSector(int from, int to) {
        mFrom = from;
        mTo = to;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        updateOrigin();
    }

    private void updateOrigin() {
        if (mFrom >= 90) {
            setOrigin(getWidth(), 0);
        } else {
            setOrigin(0, 0);
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        // Let the base implementation perform AABB collision detection
        Actor result = super.hit(x, y, touchable);
        if (result == null) {
            return null;
        }

        // Check if we are outside the radius
        x -= getOriginX();
        y -= getOriginY();
        float radius = mRadius * getHud().getZoom();
        if (x * x + y * y > radius * radius) {
            return null;
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
