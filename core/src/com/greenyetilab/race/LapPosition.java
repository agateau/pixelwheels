package com.greenyetilab.race;

/**
 * A POJO to store information about the position within a lap
 */
public class LapPosition {
    public int sectionId = -1;
    public float sectionDistance;

    public float getLapDistance() {
        return this.sectionId + this.sectionDistance;
    }

    public void copy(LapPosition other) {
        sectionId = other.sectionId;
        sectionDistance = other.sectionDistance;
    }
}
