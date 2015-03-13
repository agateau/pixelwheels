package com.greenyetilab.race;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;

/**
 * A POJO to store information about the position within a lap
 */
public class LapPosition {
    private static final float UNINITIALIZED = -2;
    private int sectionId = -1;
    private Polygon sectionPolygon;
    private float mX;
    private float mY;
    private float sectionDistance;
    private float mCenterDistance = UNINITIALIZED;

    public void init(int sectionId, Polygon sectionPolygon, float x, float y, float sectionDistance) {
        this.sectionId = sectionId;
        this.sectionPolygon = sectionPolygon;
        this.mX = x;
        this.mY = y;
        this.sectionDistance = sectionDistance;
        this.mCenterDistance = UNINITIALIZED;
    }

    public void copy(LapPosition other) {
        sectionId = other.sectionId;
        sectionPolygon = other.sectionPolygon;
        mX = other.mX;
        mY = other.mY;
        sectionDistance = other.sectionDistance;
        mCenterDistance = other.mCenterDistance;
    }

    public int getSectionId() {
        return this.sectionId;
    }

    public float getSectionDistance() {
        return this.sectionDistance;
    }

    public float getLapDistance() {
        return this.sectionId + this.sectionDistance;
    }

    public float getCenterDistance() {
        if (mCenterDistance < -1) {
            computeCenterDistance();
        }
        return mCenterDistance;
    }

    private void computeCenterDistance() {
            /**
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
        float[] vertices = sectionPolygon.getTransformedVertices();
        float nx = MathUtils.lerp(vertices[0], vertices[2], sectionDistance);
        float ny = MathUtils.lerp(vertices[1], vertices[3], sectionDistance);
        float lx = MathUtils.lerp(vertices[6], vertices[4], sectionDistance);
        float ly = MathUtils.lerp(vertices[7], vertices[5], sectionDistance);
        float nlLength = computeLength2(nx, ny, lx, ly);
        float nmLength = computeLength2(nx, ny, mX, mY);
        mCenterDistance = (float)Math.sqrt(nmLength / nlLength) * 2 - 1;
    }

    private static float computeLength2(float x1, float y1, float x2, float y2) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        return dx * dx + dy * dy;
    }
}
