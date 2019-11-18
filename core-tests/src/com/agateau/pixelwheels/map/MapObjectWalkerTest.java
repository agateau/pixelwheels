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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class MapObjectWalkerTest {
    @Mock MapObjectWalker.WalkFunction mWalkFunction;

    @Rule public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Test
    public void testCreateOne() {
        // GIVEN a RectangleMapObject
        RectangleMapObject mapObject = new RectangleMapObject(10, 20, 4, 4);

        // AND a MapObjectWalker
        MapObjectWalker walker = MapObjectWalkerFactory.get(mapObject);

        // WHEN I walk the rectangle
        walker.walk(12, 12, mWalkFunction);

        // THEN a single object is created
        ArgumentCaptor<Float> xArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> yArg = ArgumentCaptor.forClass(Float.class);
        verify(mWalkFunction).walk(xArg.capture(), yArg.capture());

        // AND the obstacle is in the top-left corner of the rectangle
        assertThat(xArg.getValue(), is(10f));
        assertThat(yArg.getValue(), is(20f));
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

        List<Vector2> vectors = vectorListFromCaptors(xArg, yArg);
        vectors.sort((o1, o2) -> {
            if (o1.y != o2.y) {
                return Float.compare(o1.y, o2.y);
            }
            return Float.compare(o1.x, o2.x);
        });

        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < colCount; ++col) {
                int idx = row * colCount + col;
                assertTrue("index (" + idx + ") should be less than the number of objects (" + vectors.size() + ")",
                        idx < vectors.size());
                Vector2 vector = vectors.get(idx);

                assertThat(vector.x, is(originX + col * itemSize));
                assertThat(vector.y, is(originY + row * itemSize));
            }
        }
    }

    private static List<Vector2> vectorListFromCaptors(ArgumentCaptor<Float> xArg, ArgumentCaptor<Float> yArg) {
        List<Vector2> vectors = new ArrayList<>();
        List<Float> xList = xArg.getAllValues();
        List<Float> yList = yArg.getAllValues();
        for (int idx = 0; idx < xList.size(); ++idx) {
            Vector2 vector = new Vector2(xList.get(idx), yList.get(idx));
            vectors.add(vector);
        }
        return vectors;
    }
}
