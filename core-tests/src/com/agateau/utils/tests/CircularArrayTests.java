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

import static junit.framework.TestCase.assertEquals;

import com.agateau.utils.CircularArray;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CircularArrayTests {
    private static class TestArray extends CircularArray<Vector2> {
        public TestArray(int size) {
            super(size);
        }

        @Override
        protected Vector2 createInstance() {
            return new Vector2();
        }
    }

    @Test
    public void testAddItem() {
        TestArray array = new TestArray(2);
        array.add().set(0, 0);
        array.add().set(1, 1);

        int begin = array.getBeginIndex();
        int end = array.getEndIndex();

        assertEquals(0, begin);
        assertEquals(2, end);

        assertEquals(0f, array.get(0).x);
        assertEquals(1f, array.get(1).x);
    }

    @Test
    public void testOverwrite() {
        // Given a full 2-item circular array with
        TestArray array = new TestArray(2);
        array.add().set(0, 0);
        assertEquals(0, array.getBeginIndex());
        assertEquals(1, array.getEndIndex());

        array.add().set(1, 1);
        assertEquals(0, array.getBeginIndex());
        assertEquals(2, array.getEndIndex());

        // When I add a 3rd item
        array.add().set(2, 2);

        // And the end index points to item[0]
        assertEquals(0, array.getEndIndex());

        // Then the begin index points to item[1]
        int idx = array.getBeginIndex();
        assertEquals(1, idx);
        assertEquals(1f, array.get(idx).x);

        // And the next item is item[2]
        idx = array.getNextIndex(idx);
        assertEquals(2, idx);
        assertEquals(2f, array.get(idx).x);

        // And there is no item after it
        idx = array.getNextIndex(idx);
        assertEquals(array.getEndIndex(), idx);
    }
}
