/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.agateau.utils.Introspector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class IntrospectorTests {
    public static class Payload {
        public int value = 0;
    }

    @Test
    public void testNotModified() {
        Payload object = new Payload();
        object.value = 12;

        Payload reference = new Payload();
        reference.value = 12;

        Introspector introspector = new Introspector(object, reference, null);
        assertThat(introspector.hasBeenModified(), is(false));
    }

    @Test
    public void testModified() {
        Payload object = new Payload();
        object.value = 6;

        Payload reference = new Payload();
        reference.value = 12;

        Introspector introspector = new Introspector(object, reference, null);
        assertThat(introspector.hasBeenModified(), is(true));
    }
}
