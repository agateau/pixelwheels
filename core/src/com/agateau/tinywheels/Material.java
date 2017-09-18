package com.agateau.tinywheels;

/**
 * Represents the type of material vehicles are driving on
 */
enum Material {
    ROAD,
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
}
