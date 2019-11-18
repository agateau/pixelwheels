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

public class RectangleMapObjectWalker implements MapObjectWalker {
    private MapObject mMapObject;

    @Override
    public void setMapObject(MapObject object) {
        mMapObject = object;
    }

    @Override
    public void walk(float stepWidth, float stepHeight, WalkFunction function) {
        Rectangle rectangle = ((RectangleMapObject) mMapObject).getRectangle();
        float originX = rectangle.getX();
        float originY = rectangle.getY();
        for (float y = originY; y < originY + rectangle.getHeight(); y += stepHeight) {
            for (float x = originX; x < originX + rectangle.getWidth(); x += stepWidth) {
                function.walk(x, y);
            }
        }
    }
}
