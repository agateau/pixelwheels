package com.greenyetilab.race;

/**
 * The position within a lap
 */
public class LapPosition {
    public final float distance;
    public final int section;

    public LapPosition(int section, float distance) {
        this.section = section;
        this.distance = distance;
    }
}
