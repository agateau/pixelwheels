/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.utils.tests;

import com.agateau.pixelwheels.utils.ClosestFixtureFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

@RunWith(JUnit4.class)
public class ClosestFixtureFinderTests {
    @Test
    public void testEmpty() {
        World world = createWorld();
        ClosestFixtureFinder finder = new ClosestFixtureFinder(world);
        Fixture fixture = finder.run(new Vector2(0, 0), new Vector2(1, 0));

        assertNull(fixture);
    }

    @Test
    public void testOneFixture() {
        World world = createWorld();
        ClosestFixtureFinder finder = new ClosestFixtureFinder(world);
        Body body = createSquareBody(world, 1, 1);

        Fixture fixture = finder.run(new Vector2(0, 0), new Vector2(1, 0));
        assertNull(fixture);

        fixture = finder.run(new Vector2(0, 0), new Vector2(2, 2));
        assertEquals(body.getFixtureList().first(), fixture);
    }

    @Test
    public void testTwoFixtures() {
        World world = createWorld();
        ClosestFixtureFinder finder = new ClosestFixtureFinder(world);
        Body closestBody = createSquareBody(world, 1, 1);
        createSquareBody(world, 3, 3);

        Fixture fixture = finder.run(new Vector2(0, 0), new Vector2(4, 4));
        assertEquals(closestBody.getFixtureList().first(), fixture);
    }

    @Test
    public void testFilter() {
        World world = createWorld();
        ClosestFixtureFinder finder = new ClosestFixtureFinder(world);
        final Body ignoredBody = createSquareBody(world, 1, 1);
        Body acceptedBody = createSquareBody(world, 3, 3);

        finder.setFixtureFilter(new ClosestFixtureFinder.FixtureFilter() {
            @Override
            public boolean acceptFixture(Fixture fixture) {
                return fixture.getBody() != ignoredBody;
            }
        });

        Fixture fixture = finder.run(new Vector2(0, 0), new Vector2(4, 4));
        assertEquals(acceptedBody.getFixtureList().first(), fixture);
    }

    private World createWorld() {
        return new World(new Vector2(0, 0), true);
    }

    private Body createSquareBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);
        body.createFixture(shape, 0);

        return body;
    }
}
