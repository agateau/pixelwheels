/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.HashMap;

/**
 * Provide mapping between real and virtual keys
 */
public class KeyMapper {
    private final HashMap<VirtualKey, Integer> mKeyForVirtualKey = new HashMap<VirtualKey, Integer>();

    private static final KeyMapper sDefaultInstance = new KeyMapper();

    public static KeyMapper getDefaultInstance() {
        return sDefaultInstance;
    }

    public KeyMapper() {
        put(VirtualKey.LEFT, Input.Keys.LEFT);
        put(VirtualKey.RIGHT, Input.Keys.RIGHT);
        put(VirtualKey.UP, Input.Keys.UP);
        put(VirtualKey.DOWN, Input.Keys.DOWN);
        put(VirtualKey.TRIGGER, Input.Keys.SPACE);
        put(VirtualKey.BACK, Input.Keys.ESCAPE);
    }

    public void put(VirtualKey vkey, Integer key) {
        mKeyForVirtualKey.put(vkey, key);
    }

    public boolean isKeyPressed(VirtualKey vkey) {
        return Gdx.input.isKeyPressed(mKeyForVirtualKey.get(vkey));
    }

    public boolean isKeyJustPressed(VirtualKey vkey) {
        return Gdx.input.isKeyJustPressed(mKeyForVirtualKey.get(vkey));
    }
}
