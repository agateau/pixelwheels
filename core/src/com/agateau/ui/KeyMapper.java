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
package com.agateau.ui;

import static com.agateau.utils.CollectionUtils.addToIntegerArray;

import com.agateau.utils.PlatformUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import java.util.HashMap;

/** Implementation of InputMapper for keyboards */
public class KeyMapper implements InputMapper {
    // The UI instance can map multiple keycodes to the same VirtualKey, so the value type of this
    // map is an array of int.
    private final HashMap<VirtualKey, Integer[]> mKeyForVirtualKey = new HashMap<>();

    /** Create a KeyMapper to use when navigating UIs */
    public static KeyMapper createUiInstance() {
        KeyMapper mapper = new KeyMapper();
        mapper.setKey(VirtualKey.LEFT, Input.Keys.LEFT);
        mapper.setKey(VirtualKey.RIGHT, Input.Keys.RIGHT);
        mapper.setKey(VirtualKey.UP, Input.Keys.UP);
        mapper.setKey(VirtualKey.DOWN, Input.Keys.DOWN);
        mapper.setKey(VirtualKey.TRIGGER, Input.Keys.SPACE);
        mapper.setKey(VirtualKey.BACK, Input.Keys.ESCAPE);

        mapper.addKey(VirtualKey.TRIGGER, Input.Keys.ENTER);
        if (!PlatformUtils.isDesktop()) {
            // Do not use CENTER or BACK on Desktop, it causes invalid enum value errors with lwjgl3
            mapper.addKey(VirtualKey.TRIGGER, Input.Keys.CENTER);
            mapper.addKey(VirtualKey.BACK, Input.Keys.BACK);
        }
        return mapper;
    }

    /**
     * Create a KeyMapper used by a player during actual play, not to navigate UIs (except when
     * picking 2nd-player specific settings)
     */
    public static KeyMapper createGameInstance(int playerIdx) {
        KeyMapper mapper = new KeyMapper();
        for (VirtualKey vkey : VirtualKey.values()) {
            mapper.mKeyForVirtualKey.put(vkey, DefaultKeys.getDefaultKeys(playerIdx, vkey));
        }
        return mapper;
    }

    private KeyMapper() {}

    public void setKey(VirtualKey vkey, int key) {
        mKeyForVirtualKey.put(vkey, new Integer[] {key});
    }

    public void addKey(VirtualKey vkey, int key) {
        Integer[] keys = mKeyForVirtualKey.get(vkey);
        if (keys == null) {
            keys = new Integer[] {key};
        } else {
            keys = addToIntegerArray(keys, key);
        }
        mKeyForVirtualKey.put(vkey, keys);
    }

    public int getKey(VirtualKey virtualKey) {
        return mKeyForVirtualKey.get(virtualKey)[0];
    }

    @Override
    public boolean isKeyPressed(VirtualKey vkey) {
        Integer[] keys = mKeyForVirtualKey.get(vkey);
        for (Integer key : keys) {
            if (Gdx.input.isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isKeyJustPressed(VirtualKey vkey) {
        Integer[] keys = mKeyForVirtualKey.get(vkey);
        for (Integer key : keys) {
            if (Gdx.input.isKeyJustPressed(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void loadConfig(Preferences preferences, String prefix, int playerIdx) {
        for (VirtualKey vkey : VirtualKey.values()) {
            String preferenceKey = prefix + vkey.toString().toLowerCase();
            int defaultValue = DefaultKeys.getDefaultKeys(playerIdx, vkey)[0];
            int key = preferences.getInteger(preferenceKey, defaultValue);
            mKeyForVirtualKey.put(vkey, new Integer[] {key});
        }
    }

    @Override
    public void saveConfig(Preferences preferences, String prefix) {
        for (VirtualKey vkey : VirtualKey.values()) {
            String preferenceKey = prefix + vkey.toString().toLowerCase();
            int value = getKey(vkey);
            preferences.putInteger(preferenceKey, value);
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
