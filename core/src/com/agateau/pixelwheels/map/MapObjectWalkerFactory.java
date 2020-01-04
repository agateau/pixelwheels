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
import com.badlogic.gdx.maps.objects.RectangleMapObject;

/** Provides instances of MapObjectWalker for a given MapObject */
public class MapObjectWalkerFactory {
    private static final RectangleMapObjectWalker sRectangleWalker = new RectangleMapObjectWalker();
    private static final PolylineMapObjectWalker sPolylineWalker = new PolylineMapObjectWalker();

    public static MapObjectWalker get(MapObject object) {
        // This assumes we do not create fillers from multiple threads
        MapObjectWalker walker;
        if (object instanceof RectangleMapObject) {
            walker = sRectangleWalker;
        } else if (object instanceof PolylineMapObject) {
            walker = sPolylineWalker;
        } else {
            throw new RuntimeException("Unsupported MapObject type: " + object);
        }
        walker.setMapObject(object);
        return walker;
    }
}
