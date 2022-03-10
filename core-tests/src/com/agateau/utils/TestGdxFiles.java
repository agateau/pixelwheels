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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

/**
 * A test implementation of Gdx.files
 *
 * <p>Assumes the build system has defined a system property called "agc.assetsDir", pointing to the
 * game assets directory.
 */
public class TestGdxFiles implements Files {
    private final String mAssetsDir;

    public TestGdxFiles() {
        mAssetsDir = System.getProperty("agc.assetsDir");
    }

    @Override
    public FileHandle getFileHandle(String path, FileType type) {
        if (type == FileType.Internal) {
            return new FileHandle(mAssetsDir + "/" + path);
        } else {
            return new FileHandle(path);
        }
    }

    @Override
    public FileHandle classpath(String path) {
        return getFileHandle(path, FileType.Classpath);
    }

    @Override
    public FileHandle internal(String path) {
        return getFileHandle(path, FileType.Internal);
    }

    @Override
    public FileHandle external(String path) {
        return getFileHandle(path, FileType.External);
    }

    @Override
    public FileHandle absolute(String path) {
        return getFileHandle(path, FileType.Absolute);
    }

    @Override
    public FileHandle local(String path) {
        return getFileHandle(path, FileType.Local);
    }

    @Override
    public String getExternalStoragePath() {
        return null;
    }

    @Override
    public boolean isExternalStorageAvailable() {
        return false;
    }

    @Override
    public String getLocalStoragePath() {
        return null;
    }

    @Override
    public boolean isLocalStorageAvailable() {
        return false;
    }
}
