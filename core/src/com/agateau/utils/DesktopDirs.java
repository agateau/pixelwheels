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

import java.io.File;
import java.util.Map;

/**
 * Provides paths to standard directories, following the XDG Base Directory Spec.
 *
 * @see <a href="https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html">XDG
 *     Base Directory Spec</a>
 */
public class DesktopDirs {
    private final String mAppName;
    private final Map<String, String> mEnvironment;

    public DesktopDirs(String appName, Map<String, String> environment) {
        mAppName = appName;
        mEnvironment = environment;
    }

    public String getConfigDir() {
        return getXdgDir("XDG_CONFIG_HOME", ".config") + File.separator + mAppName;
    }

    public String getCacheDir() {
        return getXdgDir("XDG_CACHE_HOME", ".cache") + File.separator + mAppName;
    }

    public String getDataDir() {
        return getXdgDir("XDG_DATA_HOME", ".local" + File.separator + "share")
                + File.separator
                + mAppName;
    }

    private String getXdgDir(String variableName, String defaultSubDir) {
        String dir = mEnvironment.get(variableName);
        if (dir != null) {
            return dir;
        }
        String homeDir = System.getProperty("user.home");
        if (homeDir == null) {
            System.err.println("Can't find user home dir, using current dir");
            homeDir = System.getProperty("user.dir");
            if (homeDir == null) {
                throw new RuntimeException("Can't find current dir, aborting");
            }
        }
        return homeDir + File.separator + defaultSubDir;
    }
}
