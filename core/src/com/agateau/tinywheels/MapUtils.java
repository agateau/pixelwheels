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

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;

/**
 * Utilities to work with Tiled maps
 */
public class MapUtils {
    @SuppressWarnings("unused")
    public static float getFloatProperty(MapProperties properties, String key, float defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Float.valueOf(value.toString());
    }

    public static boolean getBooleanProperty(MapProperties properties, String key, boolean defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        String v = value.toString();
        if (v.equals("true")) {
            return true;
        } else if (v.equals("false")) {
            return  false;
        }
        NLog.e("invalid boolean value: %s", v);
        return defaultValue;
    }

    public static void renderObjectLayer(ShapeRenderer renderer, MapLayer layer) {
        final float U = Constants.UNIT_FOR_PIXEL;
        for (MapObject object : layer.getObjects()) {
            if (object instanceof PolygonMapObject) {
                float[] vertices = ((PolygonMapObject)object).getPolygon().getTransformedVertices();
                for (int idx = 2; idx < vertices.length; idx += 2) {
                    renderer.line(vertices[idx - 2] * U, vertices[idx - 1] * U, vertices[idx] * U, vertices[idx + 1] * U);
                }
            } else if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject)object).getRectangle();
                renderer.rect(rect.x * U, rect.y * U, rect.width * U, rect.height * U);
            }
        }
    }

    public static Material getTileMaterial(TiledMapTile tile) {
        if (tile == null) {
            return Material.ROAD;
        }
        Object value = tile.getProperties().get("material");
        if (value == null) {
            return Material.ROAD;
        }
        String materialName = value.toString();
        if (materialName.isEmpty()) {
            return Material.ROAD;
        }
        return Material.valueOf(materialName);
    }
}
