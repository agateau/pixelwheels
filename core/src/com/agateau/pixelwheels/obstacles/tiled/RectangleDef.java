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

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameWorld;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class RectangleDef implements TiledObstacleDef {
    private static final Polygon sPolygon = new Polygon(new float[8]);
    private static final BodyDef sBodyDef = new BodyDef();

    private final Rectangle mRectangle = new Rectangle();
    private final float mAngle;

    static {
        sBodyDef.type = BodyDef.BodyType.StaticBody;
        sBodyDef.bullet = false;
    }

    public RectangleDef(JsonObject object) {
        mRectangle.x = object.get("x").getAsFloat() - 0.5f;
        mRectangle.y = object.get("y").getAsFloat() - 0.5f;
        mRectangle.width = object.get("width").getAsFloat();
        mRectangle.height = object.get("height").getAsFloat();
        JsonElement angleElement = object.get("angle");
        mAngle = angleElement == null ? 0 : angleElement.getAsFloat();
    }

    @Override
    public void create(
            GameWorld world, int col, int row, int tileSize, TiledMapTileLayer.Cell cell) {
        createRectangle(
                world,
                col,
                row,
                tileSize,
                mRectangle,
                mAngle,
                cell.getRotation(),
                cell.getFlipHorizontally(),
                cell.getFlipVertically());
    }

    static void createRectangle(
            GameWorld world, int col, int row, int tileSize, Rectangle rectangle) {
        createRectangle(world, col, row, tileSize, rectangle, 0, 0, false, false);
    }

    static void createRectangle(
            GameWorld world,
            int col,
            int row,
            int tileSize,
            Rectangle rectangle,
            float angle,
            int cellRotation,
            boolean hflip,
            boolean vflip) {
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
        vertices[0] = rectangle.x;
        vertices[1] = rectangle.y + rectangle.height;
        // B
        vertices[2] = rectangle.x;
        vertices[3] = rectangle.y;
        // C
        vertices[4] = rectangle.x + rectangle.width;
        vertices[5] = rectangle.y;
        // D
        vertices[6] = rectangle.x + rectangle.width;
        vertices[7] = rectangle.y + rectangle.height;

        float hk = hflip ? -k : k;
        float vk = vflip ? -k : k;
        sPolygon.setScale(hk, vk);

        if (hflip) {
            angle = 180 - angle;
        }
        if (vflip) {
            angle = -angle;
        }
        // cellRotation is a value between 0 and 3
        // Always set it because we reuse the Polygon instance
        sPolygon.setRotation(cellRotation * 90 + angle);

        PolygonShape shape = new PolygonShape();
        shape.set(sPolygon.getTransformedVertices());

        sBodyDef.position.set(col, row).add(0.5f, 0.5f).scl(k);
        Body body = box2DWorld.createBody(sBodyDef);
        body.createFixture(shape, 1 /* density */);
        shape.dispose();

        TiledObstacleCreator.setWallCollisionInfo(body);
    }
}
