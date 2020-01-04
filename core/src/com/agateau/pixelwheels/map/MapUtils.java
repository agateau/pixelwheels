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

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

/** Utilities to work with Tiled maps */
public class MapUtils {
    @SuppressWarnings("unused")
    public static float getFloatProperty(MapProperties properties, String key, float defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Float.valueOf(value.toString());
    }

    public static boolean getBooleanProperty(
            MapProperties properties, String key, boolean defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        String v = value.toString();
        if (v.equals("true")) {
            return true;
        } else if (v.equals("false")) {
            return false;
        }
        NLog.e("invalid boolean value: %s", v);
        return defaultValue;
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

    /** Returns the ID of an obstacle from the Obstacle layer, or null if it is a border */
    public static String getObstacleId(MapObject object) {
        Object value = object.getProperties().get("type");
        return value == null ? null : value.toString();
    }

    public static void setObstacleId(MapObject object, String id) {
        object.getProperties().put("type", id);
    }

    public static boolean isBorderObstacle(MapObject object) {
        return getObstacleId(object) == null;
    }

    public static float getObjectRotation(MapObject object) {
        return -object.getProperties().get("rotation", 0f, Float.class);
    }

    public static void setObjectRotation(MapObject object, float rotation) {
        object.getProperties().put("rotation", -rotation);
    }
}
