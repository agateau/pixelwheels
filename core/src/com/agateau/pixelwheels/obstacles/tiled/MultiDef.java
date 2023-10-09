/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.obstacles.tiled;

import com.agateau.pixelwheels.GameWorld;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class MultiDef implements TiledObstacleDef {
    private final Array<TiledObstacleDef> mObstacleDefs = new Array<>();

    public MultiDef(JsonObject root) {
        JsonArray array = root.get("obstacles").getAsJsonArray();
        for (JsonElement element : array) {
            TiledObstacleDef def = TiledObstacleCreator.loadDefFromJson(element.getAsJsonObject());
            mObstacleDefs.add(def);
        }
    }

    @Override
    public void create(
            GameWorld world, int col, int row, int tileSize, TiledMapTileLayer.Cell cell) {
        for (TiledObstacleDef def : mObstacleDefs) {
            def.create(world, col, row, tileSize, cell);
        }
    }
}
