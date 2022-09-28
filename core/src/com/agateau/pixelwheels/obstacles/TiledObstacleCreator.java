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
package com.agateau.pixelwheels.obstacles;

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.racescreen.CollisionCategories;
import com.agateau.pixelwheels.utils.Box2DUtils;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
    interface TiledObstacleDef {
        void create(GameWorld world, int col, int row, int tileSize, TiledMapTileLayer.Cell cell);
    }

    private static class MultiDef implements TiledObstacleDef {
        private final Array<TiledObstacleDef> mObstacleDefs = new Array<>();

        public MultiDef(JsonObject root) {
            JsonArray array = root.get("obstacles").getAsJsonArray();
            for (JsonElement element : array) {
                TiledObstacleDef def = loadDefFromJson(element.getAsJsonObject());
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

    private static class CircleDef implements TiledObstacleDef {
        private final float mRadius;
        private final Vector2 mOrigin = new Vector2();
        private final BodyDef mBodyDef = new BodyDef();

        public CircleDef(JsonObject object) {
            mBodyDef.type = BodyDef.BodyType.StaticBody;
            mBodyDef.bullet = false;

            mRadius = object.get("radius").getAsFloat();
            mOrigin.x = object.get("x").getAsFloat();
            mOrigin.y = object.get("y").getAsFloat();
        }

        @Override
        public void create(
                GameWorld world, int col, int row, int tileSize, TiledMapTileLayer.Cell cell) {
            World box2DWorld = world.getBox2DWorld();

            float k = tileSize * Constants.UNIT_FOR_PIXEL;
            mBodyDef.position.set(col, row).add(mOrigin).scl(k);
            Body body = box2DWorld.createBody(mBodyDef);

            CircleShape shape = new CircleShape();
            shape.setRadius(mRadius * k);

            body.createFixture(shape, 1 /* density */);
            shape.dispose();

            setWallCollisionInfo(body);
        }
    }

    private static class RectangleDef implements TiledObstacleDef {
        private final Rectangle mRectangle = new Rectangle();
        private final float mAngle;
        private final BodyDef mBodyDef = new BodyDef();

        public RectangleDef(JsonObject object) {
            mBodyDef.type = BodyDef.BodyType.StaticBody;
            mBodyDef.bullet = false;

            mRectangle.x = object.get("x").getAsFloat() - 0.5f;
            mRectangle.y = object.get("y").getAsFloat() - 0.5f;
            mRectangle.width = object.get("width").getAsFloat();
            mRectangle.height = object.get("height").getAsFloat();
            JsonElement angleElement = object.get("angle");
            mAngle = angleElement == null ? 0 : angleElement.getAsFloat();
        }

        private static final Polygon sPolygon = new Polygon(new float[8]);

        @Override
        public void create(
                GameWorld world, int col, int row, int tileSize, TiledMapTileLayer.Cell cell) {
            World box2DWorld = world.getBox2DWorld();

            float k = tileSize * Constants.UNIT_FOR_PIXEL;

            /*
             A          D
              x--------x
              |        |
              x--------x
             B          C
            */
            float[] vertices = sPolygon.getVertices();
            // A
            vertices[0] = mRectangle.x;
            vertices[1] = mRectangle.y + mRectangle.height;
            // B
            vertices[2] = mRectangle.x;
            vertices[3] = mRectangle.y;
            // C
            vertices[4] = mRectangle.x + mRectangle.width;
            vertices[5] = mRectangle.y;
            // D
            vertices[6] = mRectangle.x + mRectangle.width;
            vertices[7] = mRectangle.y + mRectangle.height;

            float hk = cell.getFlipHorizontally() ? -k : k;
            float vk = cell.getFlipVertically() ? -k : k;
            sPolygon.setScale(hk, vk);

            float angle = mAngle;
            if (cell.getFlipHorizontally()) {
                angle = 180 - angle;
            }
            if (cell.getFlipVertically()) {
                angle = -angle;
            }
            // cell.getRotation() returns a value between 0 and 3
            // Always set it because we reuse the Polygon instance
            sPolygon.setRotation(cell.getRotation() * 90 + angle);

            PolygonShape shape = new PolygonShape();
            shape.set(sPolygon.getTransformedVertices());

            mBodyDef.position.set(col, row).add(0.5f, 0.5f).scl(k);
            Body body = box2DWorld.createBody(mBodyDef);
            body.createFixture(shape, 1 /* density */);
            shape.dispose();

            setWallCollisionInfo(body);
        }
    }

    private final HashMap<TiledMapTile, TiledObstacleDef> mDefsForTile = new HashMap<>();

    /** Main entry point: create the obstacles in @p world, according to @p map. */
    public static void createObstacles(GameWorld world, TiledMap map) {
        TiledObstacleCreator creator = new TiledObstacleCreator(map.getTileSets().getTileSet(0));
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                creator.create(world, (TiledMapTileLayer) layer);
            }
        }
    }

    public TiledObstacleCreator(TiledMapTileSet tileSet) {
        JsonParser parser = new JsonParser();

        for (TiledMapTile tile : tileSet) {
            String json = tile.getProperties().get("obstacle", String.class);
            if (json == null) {
                continue;
            }
            JsonObject root = parser.parse(json).getAsJsonObject();
            TiledObstacleDef def = loadDefFromJson(root);
            mDefsForTile.put(tile, def);
        }
    }

    private static TiledObstacleDef loadDefFromJson(JsonObject root) {
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
        int rows = layer.getHeight();
        int cols = layer.getWidth();

        int tileWidth = layer.getTileWidth();

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell == null) {
                    continue;
                }
                TiledObstacleDef def = mDefsForTile.get(cell.getTile());
                if (def != null) {
                    def.create(world, col, row, tileWidth, cell);
                }
            }
        }
    }

    private static void setWallCollisionInfo(Body body) {
        Box2DUtils.setCollisionInfo(
                body,
                CollisionCategories.WALL,
                CollisionCategories.RACER
                        | CollisionCategories.EXPLOSABLE
                        | CollisionCategories.RACER_BULLET);
    }
}
