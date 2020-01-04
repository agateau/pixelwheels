/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;

class PolylineMapObjectWalker implements MapObjectWalker {
    private MapObject mMapObject;

    @Override
    public void setMapObject(MapObject object) {
        mMapObject = object;
    }

    @Override
    public void walk(float stepWidth, float stepHeight, WalkFunction function) {
        Vector2 v1 = new Vector2();
        Vector2 v2 = new Vector2();
        Polyline polyline = ((PolylineMapObject) mMapObject).getPolyline();

        float[] vertices = polyline.getTransformedVertices();
        int count = vertices.length / 2;
        v1.set(vertices[0], vertices[1]);
        for (int idx = 1; idx < count; ++idx) {
            v2.set(vertices[2 * idx], vertices[2 * idx + 1]);
            walkVector(v1, v2, stepWidth, function);
            v1.set(v2);
        }
    }

    private static final Vector2 mTmp = new Vector2();

    private void walkVector(Vector2 v1, Vector2 v2, float stepSize, WalkFunction function) {
        mTmp.set(v2).sub(v1);
        float distance = mTmp.len() - stepSize;
        float angle = mTmp.angleRad();
        int itemCount = MathUtils.floor(distance / stepSize);

        mTmp.nor().scl(stepSize);
        // Space between items
        float dx = mTmp.x;
        float dy = mTmp.y;

        mTmp.set(v1).add(dx / 2, dy / 2);
        for (int i = 0; i <= itemCount; ++i) {
            function.walk(mTmp.x, mTmp.y, angle);
            mTmp.add(dx, dy);
        }
    }
}
