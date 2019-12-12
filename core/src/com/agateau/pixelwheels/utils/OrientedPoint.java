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
package com.agateau.pixelwheels.utils;

import java.util.Locale;

/** Represents a position and an angle */
public class OrientedPoint {
    public float x = 0;
    public float y = 0;
    public float angle = 0;

    public OrientedPoint() {}

    public OrientedPoint(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "x=%f y=%f angle=%f", x, y, angle);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof OrientedPoint)) {
            return false;
        }
        OrientedPoint point = (OrientedPoint) other;
        if (point == this) {
            return true;
        }
        return x == point.x && y == point.y && angle == point.angle;
    }

    @Override
    public int hashCode() {
        return (int) (x * y * angle * 100000);
    }
}
