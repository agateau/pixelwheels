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
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RectangleMapObjectWalker implements MapObjectWalker {
    private MapObject mMapObject;

    @Override
    public void setMapObject(MapObject object) {
        mMapObject = object;
    }

    @Override
    public void walk(float stepWidth, float stepHeight, WalkFunction function) {
        Vector2 vector2 = new Vector2();
        // Rectangles are in pixel coordinates, with y going up
        // getX(), getY() returns  the bottom-left corner, but the rotation center
        // is the top-left corner!
        Rectangle rectangle = ((RectangleMapObject) mMapObject).getRectangle();
        float angle = MapUtils.getObjectRotation(mMapObject);

        float originX = rectangle.getX();
        float originY = rectangle.getY() + rectangle.getHeight();
        for (float y = -rectangle.getHeight() + stepHeight / 2; y < 0; y += stepHeight) {
            for (float x = stepWidth / 2; x < rectangle.getWidth(); x += stepWidth) {
                vector2.set(x, y).rotate(angle).add(originX, originY);
                function.walk(vector2.x, vector2.y, 0);
            }
        }
    }
}
