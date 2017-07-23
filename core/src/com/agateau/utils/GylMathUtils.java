package com.agateau.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

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

    /**
     * Pick a value from array, interpolating linearly between its elements. For example, assuming
     * array = [0, 1, 4]
     *
     * arrayLerp(array, 0) => 0
     * arrayLerp(array, 0.25) => 0.5
     * arrayLerp(array, 0.5) => 1
     * arrayLerp(array, 0.75) => 2.5
     * arrayLerp(array, 1) => 4
     */
    public static float arrayLerp(float[] array, float k) {
        k = MathUtils.clamp(k, 0, 1);
        float k2 = k * (array.length - 1);
        int idx = MathUtils.floor(k2);
        if (idx >= array.length - 1) {
            return array[array.length - 1];
        }
        return MathUtils.lerp(array[idx], array[idx + 1], k2 - idx);
    }

    private static Vector2 sWidthVector = new Vector2();

    /**
     * Compute the vector corresponding to the width side of a rectangle whose length side is made
     * of @p pos1 to @p pos2, with a width of @p width
     *
     * Always return the same vector
     */
    public static Vector2 computeWidthVector(Vector2 pos1, Vector2 pos2, float width) {
        sWidthVector.set(pos2).sub(pos1).nor();
        sWidthVector.set(-sWidthVector.y, sWidthVector.x).scl(width);
        return sWidthVector;
    }
}
