/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.utils.tests;

import com.agateau.pixelwheels.bonus.MissileGuidingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.agateau.pixelwheels.Constants.UNIT_FOR_PIXEL;
import static com.agateau.pixelwheels.GameWorld.BOX2D_TIME_STEP;
import static com.agateau.pixelwheels.GameWorld.POSITION_ITERATIONS;
import static com.agateau.pixelwheels.GameWorld.VELOCITY_ITERATIONS;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MissileGuidingSystemTests {
    private static final float MISSILE_WIDTH = 32 * UNIT_FOR_PIXEL;
    private static final float MISSILE_HEIGHT = 6 * UNIT_FOR_PIXEL;

    @Test
    public void noTarget() {
        World world = createWorld();
        Body body = createMissileBody(world, 0, 0);
        Vector2 end = new Vector2(10 * UNIT_FOR_PIXEL, 0);

        MissileGuidingSystem guidingSystem = new MissileGuidingSystem();
        guidingSystem.init(body, 100);

        long start = System.currentTimeMillis();
        while (!hasBodyReachedPoint(body, end)) {
            if (System.currentTimeMillis() - start > 100) {
                Assert.fail("Took too long");
            }
            world.step(BOX2D_TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            guidingSystem.act(null);
        }
        assertEquals(0f, body.getAngle());
    }

    @Test
    public void hitTargetInFrontOfMissile() {
        World world = createWorld();
        Body body = createMissileBody(world, 0, 0);
        Vector2 target = new Vector2(10 * UNIT_FOR_PIXEL, 0);

        MissileGuidingSystem guidingSystem = new MissileGuidingSystem();
        guidingSystem.init(body, 100);
        assertTrue(iterate(guidingSystem, world, body, target, 2000));
    }

    @Test
    public void hitStillTarget() {
        World world = createWorld();
        Body body = createMissileBody(world, 0, 0);
        Vector2 target = new Vector2(400 * UNIT_FOR_PIXEL, 80 * UNIT_FOR_PIXEL);

        MissileGuidingSystem guidingSystem = new MissileGuidingSystem();
        guidingSystem.init(body, 10);
        assertTrue(iterate(guidingSystem, world, body, target, 2000));
    }

    private boolean iterate(MissileGuidingSystem guidingSystem, World world, Body body, Vector2 target, long maxDuration) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < maxDuration) {
            world.step(BOX2D_TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            guidingSystem.act(target);
            if (hasBodyReachedPoint(body, target)) {
                return true;
            }
            if (body.getPosition().x > target.x) {
                break;
            }
        }
        return false;
    }

    private boolean hasBodyReachedPoint(Body body, Vector2 target) {
        return body.getFixtureList().first().testPoint(target);
    }

    private World createWorld() {
        return new World(new Vector2(0, 0), true);
    }

    private Body createMissileBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.bullet = true;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(MISSILE_WIDTH / 2, MISSILE_HEIGHT / 2);
        body.createFixture(shape, 1);

        return body;
    }
}
