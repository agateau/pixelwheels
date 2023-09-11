/*
 * Copyright 2023 Aurélien Gâteau <mail@agateau.com>
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

import com.badlogic.gdx.Preferences;
import java.util.Map;

/**
 * A test implementation of Gdx.preferences
 *
 * <p>Returns the default value for all `get*(key, defaultValue)`.
 */
public class TestGdxPreferences implements Preferences {
    @Override
    public Preferences putBoolean(String key, boolean val) {
        return this;
    }

    @Override
    public Preferences putInteger(String key, int val) {
        return this;
    }

    @Override
    public Preferences putLong(String key, long val) {
        return this;
    }

    @Override
    public Preferences putFloat(String key, float val) {
        return this;
    }

    @Override
    public Preferences putString(String key, String val) {
        return this;
    }

    @Override
    public Preferences put(Map<String, ?> vals) {
        return this;
    }

    @Override
    public boolean getBoolean(String key) {
        return false;
    }

    @Override
    public int getInteger(String key) {
        return 0;
    }

    @Override
    public long getLong(String key) {
        return 0;
    }

    @Override
    public float getFloat(String key) {
        return 0;
    }

    @Override
    public String getString(String key) {
        return "";
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return defValue;
    }

    @Override
    public int getInteger(String key, int defValue) {
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        return defValue;
    }

    @Override
    public Map<String, ?> get() {
        return null;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public void clear() {}

    @Override
    public void remove(String key) {}

    @Override
    public void flush() {}
}
