package com.agateau.tinywheels;

import com.agateau.utils.Assert;

/**
 * Represents the type of material vehicles are driving on
 */
enum Material {
    ROAD,
    TURBO,
    SAND,
    SNOW,
    DEEP_WATER,
    WATER,
    AIR;

    boolean isHole() {
        return this == DEEP_WATER;
    }

    boolean isWater() {
        return this == DEEP_WATER || this == WATER;
    }

    float getSpeed() {
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
        }
        Assert.check(false, "Missing material speed for " + toString());
        return 0;
    }
}
