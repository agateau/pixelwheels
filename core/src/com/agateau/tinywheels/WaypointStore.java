package com.agateau.tinywheels;

import com.agateau.utils.Assert;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Holds all the waypoints used by AI players
 */
public class WaypointStore {
    private static class WaypointInfo implements Comparable {
        float lapDistance;
        Vector2 waypoint;

        @Override
        public int compareTo(Object o) {
            WaypointInfo other = (WaypointInfo) o;
            if (lapDistance < other.lapDistance) {
                return -1;
            } else if (lapDistance == other.lapDistance) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    private Array<WaypointInfo> mWaypointInfos = new Array<WaypointInfo>();

    public void read(MapLayer layer, LapPositionTable lapPositionTable) {
        final float U = Constants.UNIT_FOR_PIXEL;

        for (MapObject object : layer.getObjects()) {
            Assert.check(object instanceof EllipseMapObject, "Waypoints layer should contains only ellipses. " + object + " is not an ellipse.");
            Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
            final LapPosition pos = lapPositionTable.get((int) ellipse.x, (int) ellipse.y);
            WaypointInfo info = new WaypointInfo();
            info.waypoint = new Vector2(ellipse.x * U, ellipse.y * U);
            info.lapDistance = pos.getLapDistance();
            mWaypointInfos.add(info);
        }
        mWaypointInfos.sort();
    }

    public Vector2 getWaypoint(float lapDistance) {
        int nextIdx = 0;
        for (int idx = 0; idx < mWaypointInfos.size; ++idx) {
            if (lapDistance < mWaypointInfos.get(idx).lapDistance) {
                nextIdx = idx;
                break;
            }
        }
        // Target the waypoint after the next one to produce smoother moves
        nextIdx = (nextIdx + 1) % mWaypointInfos.size;
        return mWaypointInfos.get(nextIdx).waypoint;
    }
}
