/*
 * Copyright 2024 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.utils;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.badlogic.gdx.files.FileHandle;
import java.util.Objects;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class IntrospectorTests {
    @Rule public TemporaryFolder mTemporaryFolder = new TemporaryFolder();

    static class POJO {
        public int intField;
        public float floatField;
        public boolean boolField;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            POJO that = (POJO) obj;
            return intField == that.intField
                    && Float.compare(that.floatField, floatField) == 0
                    && boolField == that.boolField;
        }

        @Override
        public int hashCode() {
            return Objects.hash(intField, floatField, boolField);
        }
    }

    @Test
    public void testRoundTrip() {
        // GIVEN a POJO
        POJO pojo = new POJO();
        pojo.intField = 12;
        pojo.floatField = 12.34f;
        pojo.boolField = true;

        // AND a file handle to serialize it
        FileHandle handle = new FileHandle(mTemporaryFolder.getRoot() + "/pojo.xml");

        // AND an introspector for it
        Introspector introspector = Introspector.fromInstance(pojo, handle);

        // WHEN it is serialized
        introspector.save();

        // THEN a file is created
        assertThat(handle.exists(), is(true));

        // WHEN it is deserialized
        POJO pojo2 = new POJO();
        Introspector introspector2 = Introspector.fromInstance(pojo2, handle);
        introspector2.load();

        // THEN the new instance has the same values as the existing instance
        assertThat(pojo2, equalTo(pojo));
    }
}
