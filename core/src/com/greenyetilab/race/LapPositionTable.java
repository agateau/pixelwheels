package com.greenyetilab.race;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.Assert;

/**
 * Can provide the position within a lap based on x, y (in tile pixels)
 */
public class LapPositionTable {
    private final Array<LapSection> mZones = new Array<LapSection>();

    private static class LapSection {
        private int mSectionId;
        private Polygon mPolygon;
        private final Warper mWarper = new Warper();

        public LapSection(int sectionId, Polygon polygon) {
            mSectionId = sectionId;
            mPolygon = polygon;
            float[] vertices = mPolygon.getTransformedVertices();
            int verticeCount = vertices.length / 2;
            Assert.check(verticeCount == 4, "Polygon " + sectionId + " must have 4 vertices, not " + verticeCount);
            mWarper.setSource(
                    vertices[0], vertices[1],
                    vertices[2], vertices[3],
                    vertices[4], vertices[5],
                    vertices[6], vertices[7]
            );
            mWarper.setDestination(
                    0, -1,
                    1, -1,
                    1, 1,
                    0, 1
            );
        }

        private final LapPosition mLapPosition = new LapPosition();
        public LapPosition computePosition(float x, float y) {
            Vector2 out = mWarper.warp(x, y);
            mLapPosition.sectionId = mSectionId;
            mLapPosition.sectionPolygon = mPolygon;
            mLapPosition.sectionDistance = out.x;
            return mLapPosition;
        }
    }

    public void addSection(int section, Polygon polygon) {
        mZones.add(new LapSection(section, polygon));
    }

    public LapPosition get(int x, int y) {
        for (LapSection zone : mZones) {
            if (zone.mPolygon.contains(x, y)) {
                return zone.computePosition(x, y);
            }
        }
        return null;
    }

    public int getSectionCount() {
        return mZones.size;
    }
}
