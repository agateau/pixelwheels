/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class AgcMathUtils {
    private static final Vector2 sTmpVector = new Vector2();

    public static final float msToKmh = 3600 / 1000f;
    public static final float kmhToMs = 1 / msToKmh;

    /** Wrap angles if they are less than 0 or greater than 360 */
    public static float normalizeAngle(float angle) {
        return modulo(angle, 360);
    }

    public static float normalizeAngleRad(float angle) {
        return modulo(angle, MathUtils.PI2);
    }

    /** Wrap angles so that they are between -180 and 180 */
    public static float normalizeAngle180(float angle) {
        angle = normalizeAngle(angle);
        if (angle > 180) {
            angle -= 360;
        }
        return angle;
    }

    public static float normalizeAnglePiRad(float angle) {
        angle = normalizeAngleRad(angle);
        if (angle > MathUtils.PI) {
            angle -= MathUtils.PI2;
        }
        return angle;
    }

    /**
     * Pick a value from array, interpolating linearly between its elements.
     *
     * <p>For example, assuming array = [0, 1, 4]
     *
     * <pre>
     * arrayLerp(array, 0) => 0
     * arrayLerp(array, 0.25) => 0.5
     * arrayLerp(array, 0.5) => 1
     * arrayLerp(array, 0.75) => 2.5
     * arrayLerp(array, 1) => 4
     * </pre>
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
     * <p>Always return the same vector
     */
    public static Vector2 computeWidthVector(Vector2 pos1, Vector2 pos2, float width) {
        sTmpVector.set(pos2).sub(pos1).nor();
        //noinspection SuspiciousNameCombination
        sTmpVector.set(-sTmpVector.y, sTmpVector.x).scl(width);
        return sTmpVector;
    }

    /**
     * Compute the projection of A on the segment defined by P1 and P2
     *
     * <pre>
     *      + A
     *     /
     *    /
     *   /
     *  +---+----------+
     *  P1  H          P2
     * </pre>
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
     *
     * <pre>
     *     + P2
     *    /
     *   /
     *  /
     * +-------------->
     * P1
     * </pre>
     */
    public static float segmentAngle(Vector2 pos1, Vector2 pos2) {
        return (float) Math.atan2(pos2.y - pos1.y, pos2.x - pos1.x) * MathUtils.radiansToDegrees;
    }

    /** Grows @p rect in all directions by @p amount. A negative value will shrink it. */
    public static void adjustRectangle(Rectangle rect, float amount) {
        rect.x -= amount;
        rect.y -= amount;
        rect.width += 2 * amount;
        rect.height += 2 * amount;
    }

    /** Returns the angle in degrees between two angles */
    public static float angleDelta(float angle1, float angle2) {
        return normalizeAngle(angle2) - normalizeAngle(angle1);
    }

    /**
     * A modulo which works with negative values.
     *
     * <pre>
     * modulo(4, 3) == 1
     * modulo(-1, 3) == 2
     * </pre>
     */
    public static float modulo(float value, float divisor) {
        while (value < 0) {
            value += divisor;
        }
        return value % divisor;
    }

    /**
     * Returns true if the quadrilateral formed by the point p1, p2, p3 and p4 is convex
     *
     * <pre>
     *      _,--+ 4
     *    _/     \
     * 1 +        \
     *    \     ___+ 3
     *   2 `+--'
     * </pre>
     */
    public static boolean isQuadrilateralConvex(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4) {
        // A quadrilateral is convex if the segments of its diagonals crosses.
        return lineCrossesSegment(p1, p3, p2, p4) && lineCrossesSegment(p2, p4, p1, p3);
    }

    public static boolean lineCrossesSegment(
            Vector2 lineP, Vector2 lineQ, Vector2 segmentP1, Vector2 segmentP2) {
        int side1 = lineCrossesSegmentHelper(lineP, lineQ, segmentP1);
        int side2 = lineCrossesSegmentHelper(lineP, lineQ, segmentP2);
        return side1 != side2;
    }

    /**
     * Return 1 if point is on one side of the PQ line, -1 if it is on the other side, 0 if it is on
     * the line. Based on: https://math.stackexchange.com/a/1001871.
     *
     * <blockquote>
     *
     * given two points P = (Xp, Yp) and Q = (Xq, Yq) which determine the line (PQ) in the (x, y)
     * plane, the following function f(x,y) is zero on the line, positive on one side of that line,
     * and negative on its other side:
     *
     * <p>f(x, y) = (x − xP) / (xQ − xP) − (y − yP) / (yQ − * yP)
     *
     * </blockquote>
     */
    private static int lineCrossesSegmentHelper(Vector2 lineP, Vector2 lineQ, Vector2 point) {
        float f =
                (point.x - lineP.x) / (lineQ.x - lineP.x)
                        - (point.y - lineP.y) / (lineQ.y - lineP.y);
        return f > 0 ? 1 : f < 0 ? -1 : 0;
    }
}
