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

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.utils.OrientedPoint;
import com.agateau.utils.AgcMathUtils;
import com.agateau.utils.Assert;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/** Holds all the waypoints used by AI players */
public class WaypointStore {
    private static final OrientedPoint tmpPoint = new OrientedPoint();

    private static class WaypointInfo implements Comparable {
        final Vector2 waypoint = new Vector2();
        float lapDistance;

        @Override
        public int compareTo(Object o) {
            WaypointInfo other = (WaypointInfo) o;
            return Float.compare(lapDistance, other.lapDistance);
        }
    }

    private final Array<WaypointInfo> mWaypointInfos = new Array<>();

    public void read(MapLayer layer, LapPositionTable lapPositionTable) {
        final float U = Constants.UNIT_FOR_PIXEL;

        Assert.check(
                layer.getObjects().getCount() == 1,
                "Waypoints layer should contain 1 and only 1 object");

        PolylineMapObject polylineMapObject = (PolylineMapObject) layer.getObjects().get(0);
        float[] vertices = polylineMapObject.getPolyline().getTransformedVertices();
        int count = vertices.length / 2;

        for (int idx = 0; idx < count; ++idx) {
            int x = (int) vertices[2 * idx];
            int y = (int) vertices[2 * idx + 1];
            final LapPosition pos = lapPositionTable.get(x, y);
            Assert.check(pos != null, "No position at " + x + "x" + y);

            WaypointInfo info = new WaypointInfo();
            info.waypoint.set(x, y).scl(U);
            info.lapDistance = pos.getLapDistance();
            mWaypointInfos.add(info);
        }
        mWaypointInfos.sort();
    }

    public Vector2 getWaypoint(int index) {
        return mWaypointInfos.get(index).waypoint;
    }

    public int getPreviousIndex(int index) {
        return (index > 0 ? index : mWaypointInfos.size) - 1;
    }

    public int getNextIndex(int index) {
        return (index + 1) % mWaypointInfos.size;
    }

    public int getCount() {
        return mWaypointInfos.size;
    }

    /** unit: world */
    public OrientedPoint getValidPosition(Vector2 pos, float lapDistance) {
        int nextIdx = getWaypointIndex(lapDistance);
        int prevIdx = getPreviousIndex(nextIdx);
        Vector2 prev = mWaypointInfos.get(prevIdx).waypoint;
        Vector2 next = mWaypointInfos.get(nextIdx).waypoint;
        Vector2 projected = AgcMathUtils.project(pos, prev, next);
        float waypointSquareLength = prev.dst2(next);
        if (projected.dst2(prev) > waypointSquareLength) {
            // projected is after the [prev, next] segment
            projected.set(next);
        } else if (projected.dst2(next) > waypointSquareLength) {
            // projected is before the [prev, next] segment
            projected.set(prev);
        }
        tmpPoint.x = projected.x;
        tmpPoint.y = projected.y;
        tmpPoint.angle = AgcMathUtils.normalizeAngle(AgcMathUtils.segmentAngle(prev, next));
        return tmpPoint;
    }

    public int getWaypointIndex(float lapDistance) {
        for (int idx = 0; idx < mWaypointInfos.size; ++idx) {
            if (lapDistance < mWaypointInfos.get(idx).lapDistance) {
                return idx;
            }
        }
        return 0;
    }
}
