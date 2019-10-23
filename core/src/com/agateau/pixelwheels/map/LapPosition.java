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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;

/** A POJO to store information about the position within a lap */
public class LapPosition {
    private static final float UNINITIALIZED = -2;
    private int mSectionId = -1;
    private Polygon mSectionPolygon;
    private float mX;
    private float mY;
    private float mSectionDistance;
    private float mCenterDistance = UNINITIALIZED;

    public void init(
            int sectionId, Polygon sectionPolygon, float x, float y, float sectionDistance) {
        mSectionId = sectionId;
        mSectionPolygon = sectionPolygon;
        mX = x;
        mY = y;
        mSectionDistance = sectionDistance;
        mCenterDistance = UNINITIALIZED;
    }

    public void copy(LapPosition other) {
        mSectionId = other.mSectionId;
        mSectionPolygon = other.mSectionPolygon;
        mX = other.mX;
        mY = other.mY;
        mSectionDistance = other.mSectionDistance;
        mCenterDistance = other.mCenterDistance;
    }

    public int getSectionId() {
        return mSectionId;
    }

    public float getSectionDistance() {
        return mSectionDistance;
    }

    public float getLapDistance() {
        return mSectionId + mSectionDistance;
    }

    public float getCenterDistance() {
        if (mCenterDistance < -1) {
            computeCenterDistance();
        }
        return mCenterDistance;
    }

    private void computeCenterDistance() {
        /*
         ^     V3      L     V2
         |     ,-------*-----,
         |    /          M    \_
        y-   /           *      \_
         |  /_____________*_______\
         |  V0             N       V1
         |
         +---------------|--------------->
                         x

           M coordinates are (x, y)

         */
        float[] vertices = mSectionPolygon.getTransformedVertices();
        float nx = MathUtils.lerp(vertices[0], vertices[2], mSectionDistance);
        float ny = MathUtils.lerp(vertices[1], vertices[3], mSectionDistance);
        float lx = MathUtils.lerp(vertices[6], vertices[4], mSectionDistance);
        float ly = MathUtils.lerp(vertices[7], vertices[5], mSectionDistance);
        float nlLength = computeLength2(nx, ny, lx, ly);
        float nmLength = computeLength2(nx, ny, mX, mY);
        mCenterDistance = (float) Math.sqrt(nmLength / nlLength) * 2 - 1;
    }

    private static float computeLength2(float x1, float y1, float x2, float y2) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        return dx * dx + dy * dy;
    }
}
