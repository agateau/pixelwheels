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

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.TextureRegionProvider;
import com.agateau.pixelwheels.gameobjet.GameObject;
import com.agateau.pixelwheels.map.MapUtils;
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

@RunWith(JUnit4.class)
public class ObstacleCreatorTest {
    @Mock private GameWorld mGameWorld;
    @Mock private TextureRegionProvider mTextureRegionProvider;
    @Mock private TextureRegion mTextureRegion;

    @Rule public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Test
    public void testCreateOne() {
        final float posX = 10;
        final float posY = 20;
        final int obstacleSize = 12;
        // GIVEN a world
        World box2DWorld = createBox2DWorld();
        when(mGameWorld.getBox2DWorld()).thenReturn(box2DWorld);

        when(mTextureRegionProvider.findRegion("obstacle-tire")).thenReturn(mTextureRegion);
        when(mTextureRegion.getRegionWidth()).thenReturn(obstacleSize);
        when(mTextureRegion.getRegionHeight()).thenReturn(obstacleSize);
        ObstacleDef def = ObstacleDef.createCircle(mTextureRegionProvider, "tire", 1);

        // AND an obstacle creator
        ObstacleCreator creator = new ObstacleCreator();
        creator.addObstacleDef(def);

        // WHEN I call create() with a rectangle the size of the obstacle
        RectangleMapObject mapObject =
                new RectangleMapObject(posX, posY, obstacleSize, obstacleSize);
        MapUtils.setObstacleId(mapObject, def.id);

        creator.create(mGameWorld, mTextureRegionProvider, mapObject);

        // THEN a single object is created
        ArgumentCaptor<GameObject> gameObjectCaptor = ArgumentCaptor.forClass(GameObject.class);
        verify(mGameWorld).addGameObject(gameObjectCaptor.capture());

        Obstacle obstacle = (Obstacle) gameObjectCaptor.getValue();

        // AND the obstacle is in the top-left corner of the rectangle
        assertTrue(isCloseTo(obstacle.getX() / Constants.UNIT_FOR_PIXEL, posX + obstacleSize / 2f));
        assertTrue(isCloseTo(obstacle.getY() / Constants.UNIT_FOR_PIXEL, posY + obstacleSize / 2f));

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

    private static boolean isCloseTo(float v1, float v2) {
        return Math.abs(v1 - v2) < 0.001f;
    }
}
