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
package com.agateau.pixelwheels.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class MapObjectWalkerTest {
    @Mock MapObjectWalker.WalkFunction mWalkFunction;

    @Rule public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Test
    public void testCreateOne() {
        int itemSize = 10;
        float originX = 10;
        float originY = 20;
        // GIVEN a RectangleMapObject
        RectangleMapObject mapObject = new RectangleMapObject(originX, originY, 8, 8);

        // AND a MapObjectWalker
        MapObjectWalker walker = MapObjectWalkerFactory.get(mapObject);

        // WHEN I walk the rectangle
        walker.walk(itemSize, itemSize, mWalkFunction);

        // THEN a single object is created
        ArgumentCaptor<Float> xArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> yArg = ArgumentCaptor.forClass(Float.class);
        verify(mWalkFunction).walk(xArg.capture(), yArg.capture());

        // AND the obstacle is in the top-left corner of the rectangle
        assertThat(xArg.getValue(), is(originX + itemSize / 2));
        assertThat(yArg.getValue(), is(originY + itemSize / 2));
    }

    @Test
    public void testFillRectangle() {
        int itemSize = 10;
        int colCount = 3;
        int rowCount = 2;
        float originX = 10;
        float originY = 20;

        // GIVEN a RectangleMapObject
        RectangleMapObject mapObject = new RectangleMapObject(originX, originY,
                colCount * itemSize, rowCount * itemSize);

        // AND a MapObjectWalker
        MapObjectWalker walker = MapObjectWalkerFactory.get(mapObject);

        // WHEN I walk the rectangle
        walker.walk(itemSize, itemSize, mWalkFunction);

        // THEN the rectangle is filled with objects
        ArgumentCaptor<Float> xArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> yArg = ArgumentCaptor.forClass(Float.class);
        verify(mWalkFunction, times(6)).walk(xArg.capture(), yArg.capture());

        Set<Vector2> vectors = vectorSetFromCaptors(xArg, yArg);
        Set<Vector2> expectedVectors = new HashSet<>();

        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < colCount; ++col) {
                float x = originX + col * itemSize + itemSize / 2f;
                float y = originY + row * itemSize + itemSize / 2f;
                expectedVectors.add(new Vector2(x, y));
            }
        }
        assertThat(vectors, is(expectedVectors));
    }

    @Test
    public void testFillRotatedRectangle() {
        int itemSize = 10;
        int colCount = 3;
        int rowCount = 2;
        float originX = 10;
        float originY = 20;
        float angle = 45;

        // GIVEN a rotated RectangleMapObject
        RectangleMapObject mapObject = new RectangleMapObject(originX, originY,
                colCount * itemSize, rowCount * itemSize);
        MapUtils.setObjectRotation(mapObject, angle);

        // AND a MapObjectWalker
        MapObjectWalker walker = MapObjectWalkerFactory.get(mapObject);

        // WHEN I walk the rectangle
        walker.walk(itemSize, itemSize, mWalkFunction);

        // THEN the rectangle is filled with objects
        ArgumentCaptor<Float> xArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> yArg = ArgumentCaptor.forClass(Float.class);
        verify(mWalkFunction, times(6)).walk(xArg.capture(), yArg.capture());

        Set<Vector2> vectors = vectorSetFromCaptors(xArg, yArg);

        Set<Vector2> expectedVectors = new HashSet<>();
        float rectHeight = rowCount * itemSize;
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < colCount; ++col) {
                Vector2 vector = new Vector2(col * itemSize, row * itemSize - rectHeight);
                vector.add(itemSize / 2, itemSize / 2);
                vector.rotate(angle).add(originX, originY + rectHeight);
                expectedVectors.add(vector);
            }
        }

    assertThat(vectors, is(expectedVectors));
    }

    private static Set<Vector2> vectorSetFromCaptors(ArgumentCaptor<Float> xArg, ArgumentCaptor<Float> yArg) {
        Set<Vector2> vectors = new HashSet<>();
        List<Float> xList = xArg.getAllValues();
        List<Float> yList = yArg.getAllValues();
        for (int idx = 0; idx < xList.size(); ++idx) {
            Vector2 vector = new Vector2(xList.get(idx), yList.get(idx));
            vectors.add(vector);
        }
        return vectors;
    }
}
