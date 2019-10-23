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
package com.agateau.pixelwheels.map;

import com.agateau.utils.Assert;

/** Represents the type of material vehicles are driving on */
public enum Material {
    ROAD,
    TURBO,
    SAND,
    SNOW,
    DEEP_WATER,
    WATER,
    AIR,
    ICE;

    public boolean isHole() {
        return this == DEEP_WATER;
    }

    public boolean isWater() {
        return this == DEEP_WATER || this == WATER;
    }

    public boolean isRoad() {
        return this == ROAD || this == TURBO || this == ICE;
    }

    public float getSpeed() {
        switch (this) {
            case ROAD:
                return 1;
            case TURBO:
                return 4;
            case SAND:
                return 0.6f;
            case SNOW:
                return 0.5f;
            case DEEP_WATER:
                return 0;
            case WATER:
                return 0.3f;
            case AIR:
                return 0;
            case ICE:
                return 0.3f;
        }
        Assert.check(false, "Missing material speed for " + toString());
        return 0;
    }

    public float getGrip() {
        if (this == ICE) {
            return 0.1f;
        }
        return 1f;
    }
}
