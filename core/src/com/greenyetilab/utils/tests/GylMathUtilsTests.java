package com.greenyetilab.utils.tests;

import com.greenyetilab.utils.GylMathUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;


/**
 * Tests for the GylMathUtils class
 */
@RunWith(JUnit4.class)
public class GylMathUtilsTests {
    @Test
    public void testArrayLerp() {
        class ArrayLerpData {
            float[] array;
            float k;
            float expected;
            ArrayLerpData(float[] array, float k, float expected) {
                this.array = array;
                this.k = k;
                this.expected = expected;
            }
        }
        ArrayLerpData[] dataSet = new ArrayLerpData[]{
                new ArrayLerpData(new float[]{0, 1, 4}, 0, 0),
                new ArrayLerpData(new float[]{0, 1, 4}, 0.25f, 0.5f),
                new ArrayLerpData(new float[]{0, 1, 4}, 0.5f, 1),
                new ArrayLerpData(new float[]{0, 1, 4}, 0.75f, 2.5f),
                new ArrayLerpData(new float[]{0, 1, 4}, 1, 4),
        };
        for (ArrayLerpData data : dataSet) {
            float actual = GylMathUtils.arrayLerp(data.array, data.k);
            assertEquals(data.expected, actual, 0.001f);
        }
    }
}
