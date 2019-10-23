/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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

import com.badlogic.gdx.Preferences;

/** Responds to input from keyboard or gamepad */
public class UiInputMapper implements InputMapper {
    private final KeyMapper mKeyMapper = KeyMapper.getDefaultInstance();
    private final GamepadInputMapper mGamepadInputMapper = GamepadInputMapper.getInstance(0);

    private static UiInputMapper sInstance;

    private UiInputMapper() {}

    public static UiInputMapper getInstance() {
        if (sInstance == null) {
            sInstance = new UiInputMapper();
        }
        return sInstance;
    }

    @Override
    public boolean isKeyPressed(VirtualKey key) {
        return mKeyMapper.isKeyPressed(key) || mGamepadInputMapper.isKeyPressed(key);
    }

    @Override
    public boolean isKeyJustPressed(VirtualKey key) {
        return mKeyMapper.isKeyJustPressed(key) || mGamepadInputMapper.isKeyJustPressed(key);
    }

    @Override
    public void loadConfig(Preferences preferences, String prefix) {}

    @Override
    public void saveConfig(Preferences preferences, String prefix) {}

    @Override
    public boolean isAvailable() {
        return true;
    }
}
