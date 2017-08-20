package com.agateau.tinywheels;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

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
