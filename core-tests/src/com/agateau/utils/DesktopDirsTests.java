/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DesktopDirsTests {
    private static String sHomeDir = System.getProperty("user.home");

    @Test
    public void testGetConfigDir() {
        String appName = "testapp";
        DesktopDirs dirs = new DesktopDirs(appName, new HashMap<>());
        String expectedDir = sHomeDir + File.separator + ".config" + File.separator + appName;
        assertThat(dirs.getConfigDir(), is(expectedDir));
    }

    @Test
    public void testGetConfigDirWithEnvVar() {
        String appName = "testapp";
        HashMap<String, String> env = new HashMap<>();
        env.put("XDG_CONFIG_HOME", File.separator + "custom-config");
        DesktopDirs dirs = new DesktopDirs(appName, env);
        String expectedDir = File.separator + "custom-config" + File.separator + appName;
        assertThat(dirs.getConfigDir(), is(expectedDir));
    }

    @Test
    public void testGetDataDir() {
        String appName = "testapp";
        DesktopDirs dirs = new DesktopDirs(appName, new HashMap<>());
        String expectedDir =
                sHomeDir
                        + File.separator
                        + ".local"
                        + File.separator
                        + "share"
                        + File.separator
                        + appName;
        assertThat(dirs.getDataDir(), is(expectedDir));
    }

    @Test
    public void testGetDataDirWithEnv() {
        String appName = "testapp";
        HashMap<String, String> env = new HashMap<>();
        env.put("XDG_DATA_HOME", File.separator + "custom-data");
        DesktopDirs dirs = new DesktopDirs(appName, env);
        String expectedDir = File.separator + "custom-data" + File.separator + appName;
        assertThat(dirs.getDataDir(), is(expectedDir));
    }
}
