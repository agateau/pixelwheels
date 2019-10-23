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

import static com.agateau.pixelwheels.Constants.UNIT_FOR_PIXEL;
import static com.agateau.pixelwheels.GameWorld.BOX2D_TIME_STEP;
import static com.agateau.pixelwheels.GameWorld.POSITION_ITERATIONS;
import static com.agateau.pixelwheels.GameWorld.VELOCITY_ITERATIONS;
import static junit.framework.Assert.assertEquals;

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

@RunWith(JUnit4.class)
public class MissileGuidingSystemTests {
    private static final float MISSILE_WIDTH = 32 * UNIT_FOR_PIXEL;
    private static final float MISSILE_HEIGHT = 6 * UNIT_FOR_PIXEL;
    private static final long MAX_DURATION_MILLIS = 200;

    interface WorldCallback {
        void act();

        boolean isDone();
    }

    private void iterate(World world, WorldCallback callback) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < MAX_DURATION_MILLIS) {
            world.step(BOX2D_TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            callback.act();
            if (callback.isDone()) {
                return;
            }
        }
        Assert.fail("Took too long");
    }

    @Test
    public void noTarget() {
        World world = createWorld();
        final Body body = createMissileBody(world, 0, 0);
        final Vector2 end = new Vector2(100 * UNIT_FOR_PIXEL, 0);

        final MissileGuidingSystem guidingSystem = new MissileGuidingSystem();
        guidingSystem.init(body);

        iterate(
                world,
                new WorldCallback() {
                    @Override
                    public void act() {
                        float velocity = body.getLinearVelocity().len();
                        Assert.assertTrue(velocity <= MissileGuidingSystem.MAX_SPEED);
                        guidingSystem.act(null);
                    }

                    @Override
                    public boolean isDone() {
                        return hasBodyReachedPoint(body, end);
                    }
                });
        assertEquals(0f, body.getAngle());
    }

    @Test
    public void hitTargetInFrontOfMissile() {
        World world = createWorld();
        final Body body = createMissileBody(world, 0, 0);
        final Vector2 target = new Vector2(200 * UNIT_FOR_PIXEL, 0);

        final MissileGuidingSystem guidingSystem = new MissileGuidingSystem();
        guidingSystem.init(body);
        iterate(
                world,
                new WorldCallback() {
                    @Override
                    public void act() {
                        guidingSystem.act(target);
                    }

                    @Override
                    public boolean isDone() {
                        return hasBodyReachedPoint(body, target);
                    }
                });
    }

    @Test
    public void hitTargetBehind() {
        World world = createWorld();
        final Body body = createMissileBody(world, 0, 0);
        final Vector2 target = new Vector2(-200 * UNIT_FOR_PIXEL, 0);

        final MissileGuidingSystem guidingSystem = new MissileGuidingSystem();
        guidingSystem.init(body);
        iterate(
                world,
                new WorldCallback() {
                    @Override
                    public void act() {
                        guidingSystem.act(target);
                    }

                    @Override
                    public boolean isDone() {
                        return hasBodyReachedPoint(body, target);
                    }
                });
    }

    @Test
    public void hitStillTargetAbove() {
        World world = createWorld();
        final Body body = createMissileBody(world, 0, 0);
        final Vector2 target = new Vector2(200 * UNIT_FOR_PIXEL, 40 * UNIT_FOR_PIXEL);

        final MissileGuidingSystem guidingSystem = new MissileGuidingSystem();
        guidingSystem.init(body);
        iterate(
                world,
                new WorldCallback() {
                    @Override
                    public void act() {
                        guidingSystem.act(target);
                    }

                    @Override
                    public boolean isDone() {
                        return hasBodyReachedPoint(body, target);
                    }
                });
    }

    @Test
    public void hitStillTargetBelow() {
        World world = createWorld();
        final Body body = createMissileBody(world, 0, 0);
        final Vector2 target = new Vector2(200 * UNIT_FOR_PIXEL, -80 * UNIT_FOR_PIXEL);

        final MissileGuidingSystem guidingSystem = new MissileGuidingSystem();
        guidingSystem.init(body);
        iterate(
                world,
                new WorldCallback() {
                    @Override
                    public void act() {
                        guidingSystem.act(target);
                    }

                    @Override
                    public boolean isDone() {
                        return hasBodyReachedPoint(body, target);
                    }
                });
    }

    @Test
    public void hitMovingTarget() {
        World world = createWorld();
        final Body body = createMissileBody(world, 0, 0);
        final Vector2 target = new Vector2(200 * UNIT_FOR_PIXEL, 20 * UNIT_FOR_PIXEL);

        final MissileGuidingSystem guidingSystem = new MissileGuidingSystem();
        guidingSystem.init(body);
        iterate(
                world,
                new WorldCallback() {
                    @Override
                    public void act() {
                        target.y += 4 * UNIT_FOR_PIXEL;
                        guidingSystem.act(target);
                    }

                    @Override
                    public boolean isDone() {
                        return hasBodyReachedPoint(body, target);
                    }
                });
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
