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
package com.agateau.utils;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static String appName;
    private static FileHandle sExtraAssetsHandle;

    // Do not use sCachedDesktopDirs directly, use getDesktopDirs() instead: getDesktopDirs()
    // creates a DesktopDirs instance the first time it's called and cache it there.
    private static DesktopDirs sCachedDesktopDirs;

    public static FileHandle getUserWritableFile(String name) {
        FileHandle handle;
        if (PlatformUtils.isDesktop()) {
            String path = getDesktopDataDir() + File.separator + name;
            handle = Gdx.files.absolute(path);
        } else {
            handle = Gdx.files.local(name);
        }
        return handle;
    }

    public static String getDesktopConfigDir() {
        return getDesktopDirs().getConfigDir();
    }

    public static String getDesktopLegacyConfigDir() {
        return System.getProperty("user.home")
                + File.separator
                + ".config"
                + File.separator
                + "agateau.com";
    }

    public static String getDesktopCacheDir() {
        return getDesktopDirs().getCacheDir();
    }

    public static String getDesktopDataDir() {
        return getDesktopDirs().getDataDir();
    }

    public static void setExtraAssetsDir(String dir) {
        FileHandle handle = Gdx.files.absolute(dir);
        if (!handle.isDirectory()) {
            NLog.e("'%s' is not a directory", dir);
            return;
        }
        NLog.i("Set '%s' as the extra asset directory", handle.path());
        sExtraAssetsHandle = handle;
    }

    public static FileHandle assets(String path) {
        if (sExtraAssetsHandle != null) {
            FileHandle handle = sExtraAssetsHandle.child(path);
            if (handle.exists()) {
                return handle;
            }
        }
        return Gdx.files.internal(path);
    }

    public static XmlReader.Element parseXml(FileHandle handle) {
        XmlReader reader = new XmlReader();
        XmlReader.Element root = reader.parse(handle);
        if (root == null) {
            NLog.e("Failed to parse xml file from %s. No root element.", handle.path());
            return null;
        }
        return root;
    }

    public static String readUtf8(final InputStream in) throws IOException {
        InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        while (true) {
            int length = reader.read(buffer);
            if (length == -1) {
                break;
            }
            sb.append(buffer, 0, length);
        }
        return sb.toString();
    }

    private static DesktopDirs getDesktopDirs() {
        if (sCachedDesktopDirs == null) {
            Assert.check(appName != null, "appName has not been set");
            sCachedDesktopDirs = new DesktopDirs(appName, System.getenv());
        }
        return sCachedDesktopDirs;
    }
}
