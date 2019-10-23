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

import com.agateau.utils.Assert;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/** Can provide the position within a lap based on x, y (in tile pixels) */
public class LapPositionTable {
    private final Array<LapSection> mSections = new Array<>();

    private static class LapSection {
        private final int mSectionId;
        private final Polygon mPolygon;
        private final Warper mWarper = new Warper();

        public LapSection(int sectionId, Polygon polygon) {
            mSectionId = sectionId;
            mPolygon = polygon;
            float[] vertices = mPolygon.getTransformedVertices();
            int verticeCount = vertices.length / 2;
            Assert.check(
                    verticeCount == 4,
                    "Polygon " + sectionId + " must have 4 vertices, not " + verticeCount);
            mWarper.setSource(
                    vertices[0], vertices[1],
                    vertices[2], vertices[3],
                    vertices[4], vertices[5],
                    vertices[6], vertices[7]);
            mWarper.setDestination(
                    0, -1,
                    1, -1,
                    1, 1,
                    0, 1);
        }

        private final LapPosition mLapPosition = new LapPosition();

        public LapPosition computePosition(float x, float y) {
            Vector2 out = mWarper.warp(x, y);
            mLapPosition.init(mSectionId, mPolygon, x, y, out.x);
            return mLapPosition;
        }
    }

    public void addSection(int section, Polygon polygon) {
        mSections.add(new LapSection(section, polygon));
    }

    public LapPosition get(int x, int y) {
        for (LapSection zone : mSections) {
            if (zone.mPolygon.contains(x, y)) {
                return zone.computePosition(x, y);
            }
        }
        return null;
    }

    public int getSectionCount() {
        return mSections.size;
    }
}
