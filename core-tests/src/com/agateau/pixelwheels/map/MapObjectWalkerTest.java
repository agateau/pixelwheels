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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.agateau.pixelwheels.utils.OrientedPoint;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

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
        ArgumentCaptor<Float> angleArg = ArgumentCaptor.forClass(Float.class);
        verify(mWalkFunction).walk(xArg.capture(), yArg.capture(), angleArg.capture());

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
        RectangleMapObject mapObject =
                new RectangleMapObject(originX, originY, colCount * itemSize, rowCount * itemSize);

        // AND a MapObjectWalker
        MapObjectWalker walker = MapObjectWalkerFactory.get(mapObject);

        // WHEN I walk the rectangle
        walker.walk(itemSize, itemSize, mWalkFunction);

        // THEN the rectangle is filled with objects
        ArgumentCaptor<Float> xArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> yArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> angleArg = ArgumentCaptor.forClass(Float.class);
        verify(mWalkFunction, times(6)).walk(xArg.capture(), yArg.capture(), angleArg.capture());

        Set<Vector2> vectors = vector2SetFromCaptors(xArg, yArg);
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
        RectangleMapObject mapObject =
                new RectangleMapObject(originX, originY, colCount * itemSize, rowCount * itemSize);
        MapUtils.setObjectRotation(mapObject, angle);

        // AND a MapObjectWalker
        MapObjectWalker walker = MapObjectWalkerFactory.get(mapObject);

        // WHEN I walk the rectangle
        walker.walk(itemSize, itemSize, mWalkFunction);

        // THEN the rectangle is filled with objects
        ArgumentCaptor<Float> xArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> yArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> angleArg = ArgumentCaptor.forClass(Float.class);
        verify(mWalkFunction, times(6)).walk(xArg.capture(), yArg.capture(), angleArg.capture());

        Set<Vector2> vectors = vector2SetFromCaptors(xArg, yArg);

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

    @Test
    public void testPolyline_horizontal() {
        int itemSize = 10;
        float[] vertices = {10, 20, 10 + 3 * itemSize, 20};

        // GIVEN a polyline
        PolylineMapObject mapObject = new PolylineMapObject(vertices);

        // AND a MapObjectWalker
        MapObjectWalker walker = MapObjectWalkerFactory.get(mapObject);

        // WHEN I walk the line
        walker.walk(itemSize, itemSize, mWalkFunction);

        // THEN the line is filled with objects
        ArgumentCaptor<Float> xArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> yArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> angleArg = ArgumentCaptor.forClass(Float.class);
        verify(mWalkFunction, times(3)).walk(xArg.capture(), yArg.capture(), angleArg.capture());

        Set<Vector2> vectors = vector2SetFromCaptors(xArg, yArg);

        Set<Vector2> expectedVectors = new HashSet<>();
        expectedVectors.add(new Vector2(15, 20));
        expectedVectors.add(new Vector2(25, 20));
        expectedVectors.add(new Vector2(35, 20));

        assertThat(vectors, is(expectedVectors));
    }

    @Test
    public void testPolyline_vertical() {
        int itemSize = 10;
        float[] vertices = {10, 20, 10, 20 + 3 * itemSize};

        // GIVEN a polyline
        PolylineMapObject mapObject = new PolylineMapObject(vertices);

        // AND a MapObjectWalker
        MapObjectWalker walker = MapObjectWalkerFactory.get(mapObject);

        // WHEN I walk the line
        walker.walk(itemSize, itemSize, mWalkFunction);

        // THEN the line is filled with objects
        ArgumentCaptor<Float> xArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> yArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> angleArg = ArgumentCaptor.forClass(Float.class);
        verify(mWalkFunction, times(3)).walk(xArg.capture(), yArg.capture(), angleArg.capture());

        Set<OrientedPoint> points = orientedPointSetFromCaptors(xArg, yArg, angleArg);

        Set<OrientedPoint> orientedPoints = new HashSet<>();
        float angle = 90;
        orientedPoints.add(new OrientedPoint(10, 25, angle));
        orientedPoints.add(new OrientedPoint(10, 35, angle));
        orientedPoints.add(new OrientedPoint(10, 45, angle));

        assertThat(points, is(orientedPoints));
    }

    private static Set<Vector2> vector2SetFromCaptors(
            ArgumentCaptor<Float> xArg, ArgumentCaptor<Float> yArg) {
        Set<Vector2> vectors = new HashSet<>();
        List<Float> xList = xArg.getAllValues();
        List<Float> yList = yArg.getAllValues();
        for (int idx = 0; idx < xList.size(); ++idx) {
            Vector2 vector = new Vector2(xList.get(idx), yList.get(idx));
            vectors.add(vector);
        }
        return vectors;
    }

    private static Set<OrientedPoint> orientedPointSetFromCaptors(
            ArgumentCaptor<Float> xArg,
            ArgumentCaptor<Float> yArg,
            ArgumentCaptor<Float> angleArg) {
        Set<OrientedPoint> points = new HashSet<>();
        List<Float> xList = xArg.getAllValues();
        List<Float> yList = yArg.getAllValues();
        List<Float> angleList = angleArg.getAllValues();
        for (int idx = 0; idx < xList.size(); ++idx) {
            float angle = flooredDegFromRad(angleList.get(idx));
            float x = MathUtils.floor(xList.get(idx));
            float y = MathUtils.floor(yList.get(idx));
            OrientedPoint point = new OrientedPoint(x, y, angle);
            points.add(point);
        }
        return points;
    }

    /** Make angles easier to compare */
    private static float flooredDegFromRad(float radAngle) {
        return MathUtils.floor(radAngle * MathUtils.radiansToDegrees);
    }
}
