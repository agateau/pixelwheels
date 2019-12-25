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
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.TextureRegionProvider;
import com.agateau.pixelwheels.map.MapObjectWalker;
import com.agateau.pixelwheels.map.MapObjectWalkerFactory;
import com.agateau.pixelwheels.map.MapUtils;
import com.agateau.pixelwheels.racescreen.CollisionCategories;
import com.agateau.pixelwheels.utils.Box2DUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import java.util.HashMap;

/** Helper class to create GameObjects and Box2D bodies from the Obstacles layer of a map */
public class ObstacleCreator {
    private final HashMap<String, ObstacleDef> mObstacleDefs = new HashMap<>();
    private final HashMap<ObstacleDef, BodyDef> mBodyDefs = new HashMap<>();

    public void addObstacleDef(ObstacleDef obstacleDef) {
        mObstacleDefs.put(obstacleDef.id, obstacleDef);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type =
                obstacleDef.dynamic ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody;
        bodyDef.bullet = false;
        mBodyDefs.put(obstacleDef, bodyDef);
    }

    public void create(
            GameWorld gameWorld, TextureRegionProvider textureRegionProvider, MapObject mapObject) {
        String id = MapUtils.getObstacleId(mapObject);
        if (id == null) {
            // Special case: an obstacle with no id is a border
            createBorder(gameWorld.getBox2DWorld(), mapObject);
            return;
        }
        ObstacleDef obstacleDef = mObstacleDefs.get(id);
        final BodyDef bodyDef = mBodyDefs.get(obstacleDef);
        MapObjectWalker walker = MapObjectWalkerFactory.get(mapObject);
        TextureRegion obstacleRegion = obstacleDef.getImage(textureRegionProvider);
        walker.walk(
                obstacleRegion.getRegionWidth(),
                obstacleRegion.getRegionHeight(),
                (x, y, angle) -> {
                    bodyDef.position.set(x, y).scl(Constants.UNIT_FOR_PIXEL);
                    bodyDef.angle = angle;
                    Obstacle obstacle =
                            new Obstacle(
                                    gameWorld.getBox2DWorld(),
                                    textureRegionProvider,
                                    obstacleDef,
                                    bodyDef);
                    gameWorld.addGameObject(obstacle);
                });
    }

    private static void createBorder(World world, MapObject mapObject) {
        Body body = Box2DUtils.createStaticBodyForMapObject(world, mapObject);
        Box2DUtils.setCollisionInfo(
                body,
                CollisionCategories.WALL,
                CollisionCategories.RACER
                        | CollisionCategories.EXPLOSABLE
                        | CollisionCategories.RACER_BULLET);
        Box2DUtils.setBodyRestitution(body, GamePlay.instance.borderRestitution / 10.0f);
    }
}
