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
import com.agateau.pixelwheels.gameobjet.GameObject;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ObstacleCreatorTest {
    @Mock private GameWorld mGameWorld;
    @Mock private TextureRegion mTextureRegion;

    @Rule public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Test
    public void testCreateOne() {
        // GIVEN a world
        World box2DWorld = createBox2DWorld();
        when(mGameWorld.getBox2DWorld()).thenReturn(box2DWorld);

        when(mTextureRegion.getRegionWidth()).thenReturn(12);
        ObstacleDef def = ObstacleDef.createCircle("tyre", mTextureRegion, 1);

        // AND an obstacle creator
        ObstacleCreator creator = new ObstacleCreator();
        creator.addObstacleDef(def);

        // WHEN I call create() with a rectangle smaller than the obstacle shape
        RectangleMapObject mapObject = new RectangleMapObject(10, 20, 4, 4);
        mapObject.getProperties().put("type", def.id);

        creator.create(mGameWorld, mapObject);

        // THEN a single object is created
        ArgumentCaptor<GameObject> gameObjectCaptor = ArgumentCaptor.forClass(GameObject.class);
        verify(mGameWorld).addGameObject(gameObjectCaptor.capture());

        Obstacle obstacle = (Obstacle) gameObjectCaptor.getValue();

        // AND the obstacle is centered in the rectangle
        assertThat(obstacle.getX(), is(12f * Constants.UNIT_FOR_PIXEL));
        assertThat(obstacle.getY(), is(22f * Constants.UNIT_FOR_PIXEL));

        // AND it has a body
        Array<Body> bodies = new Array<>();
        box2DWorld.getBodies(bodies);
        assertThat(bodies.size, is(1));
        Body body = bodies.get(0);

        // AND the body has a fixture
        Array<Fixture> fixtures = body.getFixtureList();
        assertThat(fixtures.size, is(1));
    }

    private static World createBox2DWorld() {
        return new World(new Vector2(0, 0), true);
    }
}
