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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.JsonObject;

class CircleDef implements TiledObstacleDef {
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

        TiledObstacleCreator.setWallCollisionInfo(body);
    }
}
