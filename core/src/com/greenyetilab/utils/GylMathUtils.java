package com.greenyetilab.utils;

public class GylMathUtils {
    /**
     * Wrap angles if they are less than 0 or greater than 360
     */
    public static float normalizeAngle(float angle) {
        while (angle < 0) {
            angle += 360;
        }
        return angle % 360;
    }
}
