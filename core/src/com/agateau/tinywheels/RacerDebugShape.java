/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.tinywheels;

import com.agateau.tinywheels.racer.Racer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Draw racer-related debug shapes
 */
public class RacerDebugShape implements DebugShapeMap.Shape {
    private final Racer mRacer;
    private final MapInfo mMapInfo;

    public RacerDebugShape(Racer racer, MapInfo mapInfo) {
        mRacer = racer;
        mMapInfo = mapInfo;
    }

    @Override
    public void draw(ShapeRenderer renderer) {
        WaypointStore store = mMapInfo.getWaypointStore();

        // Render waypoints
        renderer.begin(ShapeRenderer.ShapeType.Line);
        for (int idx = 0; idx < store.getCount(); ++idx) {
            renderer.setColor(idx % 2, 1, 0, 1);
            int prevIdx = store.getPreviousIndex(idx);
            renderer.line(store.getWaypoint(prevIdx), store.getWaypoint(idx));
        }
        renderer.end();

        // Render next & prev waypoints, render projected point
        float lapDistance = mRacer.getLapPositionComponent().getLapDistance();
        int nextIdx = store.getWaypointIndex(lapDistance);
        int prevIdx = store.getPreviousIndex(nextIdx);

        OrientedPoint point = store.getValidPosition(mRacer.getVehicle().getBody().getWorldCenter(), lapDistance);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        float radius = 10 * Constants.UNIT_FOR_PIXEL;
        renderer.setColor(1, 1, 0, 1);
        DrawUtils.drawCross(renderer, store.getWaypoint(prevIdx), radius);
        renderer.setColor(0, 1, 1, 1);
        DrawUtils.drawCross(renderer, store.getWaypoint(nextIdx), radius);
        renderer.setColor(1, 1, 1, 1);
        DrawUtils.drawCross(renderer, point.x, point.y, radius);

        renderer.end();
    }
}
