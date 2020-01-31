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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

import com.agateau.pixelwheels.utils.ClosestBodyFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ClosestBodyFinderTests {
    @Test
    public void testEmpty() {
        World world = createWorld();
        ClosestBodyFinder finder = new ClosestBodyFinder();
        Body body = finder.find(world, new Vector2(0, 0), new Vector2(1, 1));

        assertNull(body);
    }

    @Test
    public void testOneFixture() {
        World world = createWorld();
        ClosestBodyFinder finder = new ClosestBodyFinder();
        Body target = createStaticBody(world, 1, 1);

        Body found = finder.find(world, new Vector2(0, 0), new Vector2(0, 1));
        assertNull(found);

        found = finder.find(world, new Vector2(0, 0), new Vector2(1, 1));
        assertEquals(target, found);
    }

    @Test
    public void testFilter() {
        World world = createWorld();
        // Create a finder which only accepts static body and place a dynamic body closer
        ClosestBodyFinder finder =
                new ClosestBodyFinder(body -> body.getType() == BodyDef.BodyType.StaticBody);
        Body staticBody = createStaticBody(world, 3, 3);
        createDynamicBody(world, 1, 1);

        Body found = finder.find(world, new Vector2(0, 0), new Vector2(3, 3));
        assertEquals(staticBody, found);
    }

    private World createWorld() {
        return new World(new Vector2(0, 0), true);
    }

    private Body createStaticBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);
        body.createFixture(shape, 0);

        return body;
    }

    @SuppressWarnings("UnusedReturnValue")
    private Body createDynamicBody(World world, float x, float y) {
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
