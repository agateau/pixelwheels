package com.greenyetilab.race;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;

/**
 * A POJO to store information about the position within a lap
 */
public class LapPosition {
    public int sectionId = -1;
    public Polygon sectionPolygon;
    public float sectionDistance;

    public float getLapDistance() {
        return this.sectionId + this.sectionDistance;
    }

    public float computeCenterDistance(int x, int y) {
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
        float nmLength = computeLength2(nx, ny, x, y);
        return (float)Math.sqrt(nmLength / nlLength) * 2 - 1;
    }

    public void copy(LapPosition other) {
        sectionId = other.sectionId;
        sectionDistance = other.sectionDistance;
        sectionPolygon = other.sectionPolygon;
    }

    private static float computeLength2(float x1, float y1, float x2, float y2) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        return dx * dx + dy * dy;
    }
}
