package com.greenyetilab.race;

/**
 * The position within a lap
 */
public class LapPosition {
    public final float distance;
    public final int section;

    public LapPosition(float distance, int section) {
        this.distance = distance;
        this.section = section;
    }
}
