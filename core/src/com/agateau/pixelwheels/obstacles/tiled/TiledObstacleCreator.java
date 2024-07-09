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
import com.agateau.pixelwheels.racescreen.CollisionCategories;
import com.agateau.pixelwheels.utils.Box2DUtils;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;

/**
 * Defines static body from tiles
 *
 * <p>For a tile to define a static body it must have an "obstacle" property describing the body to
 * create.
 *
 * <p>The format of the "obstacle" property is documented in docs/map-format.md.
 */
public class TiledObstacleCreator {
    private final FullObstacleCreator mFullObstacleCreator;

    private final HashMap<TiledMapTile, TiledObstacleDef> mDefsForTile = new HashMap<>();

    /** Main entry point: create the obstacles in @p world, according to @p map. */
    public static void createObstacles(GameWorld world, TiledMap map) {
        Array<TiledMapTileLayer> layers = map.getLayers().getByType(TiledMapTileLayer.class);
        TiledMapTileLayer firstLayer = layers.first();

        TiledObstacleCreator creator =
                new TiledObstacleCreator(
                        firstLayer.getWidth(),
                        firstLayer.getHeight(),
                        map.getTileSets().getTileSet(0));
        for (TiledMapTileLayer layer : layers) {
            creator.create(world, layer);
        }
        creator.mFullObstacleCreator.create(world, firstLayer.getTileWidth());
    }

    public TiledObstacleCreator(int width, int height, TiledMapTileSet tileSet) {
        mFullObstacleCreator = new FullObstacleCreator(width, height);
        JsonParser parser = new JsonParser();

        for (TiledMapTile tile : tileSet) {
            String json = tile.getProperties().get("obstacle", String.class);
            if (json == null) {
                continue;
            }
            JsonObject root = parser.parse(json).getAsJsonObject();
            String type = root.get("type").getAsString();
            if (type.equals("full")) {
                // "full" is handled separately because its not supported by MultiDef, so it is not
                // handled by loadDefFromJson().
                mDefsForTile.put(tile, mFullObstacleCreator.getObstacleDef());
            } else {
                TiledObstacleDef def = loadDefFromJson(root);
                mDefsForTile.put(tile, def);
            }
        }
    }

    static TiledObstacleDef loadDefFromJson(JsonObject root) {
        String type = root.get("type").getAsString();
        TiledObstacleDef def;
        switch (type) {
            case "circle":
                def = new CircleDef(root);
                break;
            case "rectangle":
                def = new RectangleDef(root);
                break;
            case "multi":
                def = new MultiDef(root);
                break;
            default:
                throw new RuntimeException("Invalid type value: '" + type + "'");
        }
        return def;
    }

    private void create(GameWorld world, TiledMapTileLayer layer) {
        int tHeight = layer.getHeight();
        int tWidth = layer.getWidth();

        int tileWidth = layer.getTileWidth();

        for (int ty = 0; ty < tHeight; ++ty) {
            for (int tx = 0; tx < tWidth; ++tx) {
                TiledMapTileLayer.Cell cell = layer.getCell(tx, ty);
                if (cell == null) {
                    continue;
                }
                TiledObstacleDef def = mDefsForTile.get(cell.getTile());
                if (def != null) {
                    def.create(world, tx, ty, tileWidth, cell);
                }
            }
        }
    }

    static void setWallCollisionInfo(Body body) {
        Box2DUtils.setCollisionInfo(
                body,
                CollisionCategories.WALL,
                CollisionCategories.RACER
                        | CollisionCategories.EXPLOSABLE
                        | CollisionCategories.RACER_BULLET);
    }
}
