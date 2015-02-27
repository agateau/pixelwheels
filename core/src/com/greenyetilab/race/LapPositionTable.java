package com.greenyetilab.race;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.Assert;

/**
 * Can provide the position within a lap based on x, y (in tile pixels)
 */
public class LapPositionTable {
    private final Array<LapZone> mZones = new Array<LapZone>();

    private static class LapZone {
        private int mSection;
        private Polygon mPolygon;
        private final Warper mWarper = new Warper();

        public LapZone(int section, Polygon polygon) {
            mSection = section;
            mPolygon = polygon;
            float[] vertices = mPolygon.getTransformedVertices();
            int verticeCount = vertices.length / 2;
            Assert.check(verticeCount == 4, "Polygon " + section + " must have 4 vertices, not " + verticeCount);
            mWarper.setSource(
                    vertices[0], vertices[1],
                    vertices[2], vertices[3],
                    vertices[4], vertices[5],
                    vertices[6], vertices[7]
            );
            mWarper.setDestination(
                    0, 0,
                    1, 0,
                    1, 1,
                    0, 1
            );
        }

        public int computePosition(float x, float y) {
            Vector2 out = mWarper.warp(x, y);
            return createPosition(mSection, out.x);
        }

        private static int createPosition(int section, float distance) {
            return (section << 16) | ((int)(distance * 255) << 8) | 0xff;
        }
    }

    public void addZone(int section, Polygon polygon) {
        mZones.add(new LapZone(section, polygon));
    }

    public int get(int x, int y) {
        for (LapZone zone : mZones) {
            if (zone.mPolygon.contains(x, y)) {
                return zone.computePosition(x, y);
            }
        }
        return 0;
    }

    public static float distanceFromPosition(int value) {
        return (float)(value & 0xff00) / 255;
    }

    public static int sectionFromPosition(int value) {
        return (value & 0xff0000) >> 16;
    }
}
