/*
 * Copyright 2023 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.agateau.utils.TestGdxPreferences;
import com.badlogic.gdx.Preferences;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LogExporterUtilsTest {

    @Test
    public void testDumpPreferences() {
        Preferences prefs =
                new TestGdxPreferences() {
                    public Map<String, ?> get() {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("hello", "world");
                        map.put("twelve", 12);
                        map.put("aBoolean", true);
                        return map;
                    }
                };

        String out = LogExporterUtils.dumpPreferences(prefs);
        assertThat(out, is("{\"aBoolean\":\"true\",\"twelve\":\"12\",\"hello\":\"world\"}"));
    }
}
