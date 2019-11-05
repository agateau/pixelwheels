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
package com.agateau.pixelwheels.obstacles;

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameWorld;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;

import java.util.HashMap;

public class ObstacleCreator {
    private final HashMap<String, ObstacleDef> mObstacleDefs = new HashMap<>();
    private final HashMap<ObstacleDef, BodyDef> mBodyDefs = new HashMap<>();

    public void addObstacleDef(ObstacleDef obstacleDef) {
        mObstacleDefs.put(obstacleDef.id, obstacleDef);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.bullet = false;
        mBodyDefs.put(obstacleDef, bodyDef);
    }

    public void create(GameWorld gameWorld, RectangleMapObject rectangleMapObject) {
        String id = rectangleMapObject.getProperties().get("type").toString();
        ObstacleDef obstacleDef = mObstacleDefs.get(id);

        Rectangle rectangle = rectangleMapObject.getRectangle();
        float x = rectangle.getX() + rectangle.getWidth() / 2;
        float y = rectangle.getY() + rectangle.getHeight() / 2;

        BodyDef bodyDef = mBodyDefs.get(obstacleDef);
        bodyDef.position.set(x, y).scl(Constants.UNIT_FOR_PIXEL);
        Obstacle obstacle =
                new Obstacle(gameWorld.getBox2DWorld(), obstacleDef, bodyDef);
        gameWorld.addGameObject(obstacle);
    }
}
