/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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

import static org.junit.Assert.assertEquals;

import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for the AgcMathUtils class */
@RunWith(JUnit4.class)
public class AgcMathUtilsTests {
    @Test
    public void testArrayLerp() {
        class ArrayLerpData {
            final float[] array;
            final float k;
            final float expected;

            ArrayLerpData(float[] array, float k, float expected) {
                this.array = array;
                this.k = k;
                this.expected = expected;
            }
        }
        ArrayLerpData[] dataSet =
                new ArrayLerpData[] {
                    new ArrayLerpData(new float[] {0, 1, 4}, 0, 0),
                    new ArrayLerpData(new float[] {0, 1, 4}, 0.25f, 0.5f),
                    new ArrayLerpData(new float[] {0, 1, 4}, 0.5f, 1),
                    new ArrayLerpData(new float[] {0, 1, 4}, 0.75f, 2.5f),
                    new ArrayLerpData(new float[] {0, 1, 4}, 1, 4),
                };
        for (ArrayLerpData data : dataSet) {
            float actual = AgcMathUtils.arrayLerp(data.array, data.k);
            assertEquals(data.expected, actual, 0.001f);
        }
    }

    @Test
    public void testProject() {
        class Data {
            final Vector2 input = new Vector2();
            final Vector2 p1 = new Vector2();
            final Vector2 p2 = new Vector2();
            final Vector2 expected = new Vector2();

            Data setInput(float x, float y) {
                input.set(x, y);
                return this;
            }

            Data setP1(float x, float y) {
                p1.set(x, y);
                return this;
            }

            Data setP2(float x, float y) {
                p2.set(x, y);
                return this;
            }

            Data setExpected(float x, float y) {
                expected.set(x, y);
                return this;
            }
        }
        Data[] dataSet =
                new Data[] {
                    new Data().setInput(1, 1).setP1(0, 0).setP2(2, 0).setExpected(1, 0),
                    new Data().setInput(1, 10).setP1(0, 0).setP2(2, 0).setExpected(1, 0),
                    new Data().setInput(1, -5).setP1(0, 0).setP2(2, 0).setExpected(1, 0),
                    new Data().setInput(1, 1).setP1(0, 0).setP2(0, 2).setExpected(0, 1),
                };
        for (Data data : dataSet) {
            Vector2 result = AgcMathUtils.project(data.input, data.p1, data.p2);
            assertEquals(data.expected.x, result.x, 0.001f);
            assertEquals(data.expected.y, result.y, 0.001f);
        }
    }
}
