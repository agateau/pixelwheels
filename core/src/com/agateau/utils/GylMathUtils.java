package com.agateau.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class GylMathUtils {
    private static Vector2 sTmpVector = new Vector2();

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

    /**
     * Compute the vector corresponding to the width side of a rectangle whose length side is made
     * of @p pos1 to @p pos2, with a width of @p width
     *
     * Always return the same vector
     */
    public static Vector2 computeWidthVector(Vector2 pos1, Vector2 pos2, float width) {
        sTmpVector.set(pos2).sub(pos1).nor();
        sTmpVector.set(-sTmpVector.y, sTmpVector.x).scl(width);
        return sTmpVector;
    }

    /**
     * Compute the projection of A on the segment defined by P1 and P2
     *
     *      + A
     *     /
     *    /
     *   /
     *  +---+----------+
     *  P1  H          P2
     *
     * @param a the point to project
     * @param pos1 start of segment
     * @param pos2 end of segment
     * @return the projected point. Vector is reused.
     */
    public static Vector2 project(Vector2 a, Vector2 pos1, Vector2 pos2) {
        sTmpVector.set(pos2).sub(pos1).nor();
        float vx = sTmpVector.x;
        float vy = sTmpVector.y;

        float pos1ToH = (a.x - pos1.x) * vx + (a.y - pos1.y) * vy;
        sTmpVector.x = pos1.x + pos1ToH * vx;
        sTmpVector.y = pos1.y + pos1ToH * vy;
        return sTmpVector;
    }

    /**
     * Returns the angle in degrees between the X axis and the P1, P2 vector
     *     + P2
     *    /
     *   /
     *  /
     * +-------------->
     * P1
     */
    public static float segmentAngle(Vector2 pos1, Vector2 pos2) {
        return (float)Math.atan2(pos2.y - pos1.y, pos2.x - pos1.x) * MathUtils.radiansToDegrees;
    }
}
