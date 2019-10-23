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

public class FileUtils {
    public static String appName = "unnamed";

    public static FileHandle getUserWritableFile(String name) {
        FileHandle handle;
        if (PlatformUtils.isDesktop()) {
            handle = Gdx.files.external(".local/share/" + appName + "/" + name);
        } else {
            handle = Gdx.files.local(name);
        }
        return handle;
    }

    public static FileHandle getCacheDir() {
        FileHandle handle;
        if (PlatformUtils.isDesktop()) {
            handle = Gdx.files.external(".cache/" + appName);
        } else {
            if (!Gdx.files.isExternalStorageAvailable()) {
                return null;
            }
            handle = Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "/" + appName);
        }
        handle.mkdirs();
        return handle;
    }

    public static FileHandle assets(String path) {
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
}
