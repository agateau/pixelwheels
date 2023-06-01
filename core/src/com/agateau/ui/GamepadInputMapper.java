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
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.utils.IntMap;
import java.util.HashMap;

/** An implementation of InputMapper for gamepads */
public class GamepadInputMapper extends ControllerAdapter implements InputMapper {
    private enum AxisValue {
        LESS,
        ZERO,
        MORE
    }

    private enum KeyState {
        RELEASED,
        PRESSED,
        JUST_PRESSED
    }

    public interface Listener {
        /**
         * Returns true if the event has been handled. In this case the internal state of the input
         * mapper won't be updated
         */
        boolean onButtonPressed(int buttonCode, boolean pressed);
    }

    private Controller mController;

    private final HashMap<VirtualKey, KeyState> mPressedKeys = new HashMap<>();

    private final HashMap<VirtualKey, Integer> mButtonCodes = new HashMap<>();

    // Maps well-known gamepad buttons to our virtual keys
    private final IntMap<VirtualKey> mVirtualKeyForButton = new IntMap<>();

    private Listener mListener;

    GamepadInputMapper(Controller controller) {
        mButtonCodes.put(VirtualKey.TRIGGER, 1);
        mButtonCodes.put(VirtualKey.BACK, 2);
        setController(controller);
    }

    public Controller getController() {
        return mController;
    }

    void setController(Controller controller) {
        mController = controller;
        if (controller != null) {
            mController.addListener(this);
            updateVirtualKeyForButton();
        }
    }

    public int getButtonCodeForVirtualKey(VirtualKey key) {
        return mButtonCodes.get(key);
    }

    public void setButtonCodeForVirtualKey(VirtualKey key, int code) {
        mButtonCodes.put(key, code);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public boolean isKeyPressed(VirtualKey key) {
        KeyState state = mPressedKeys.get(key);
        return state != null && state != KeyState.RELEASED;
    }

    @Override
    public boolean isKeyJustPressed(VirtualKey key) {
        KeyState state = mPressedKeys.get(key);
        if (state == KeyState.JUST_PRESSED) {
            mPressedKeys.put(key, KeyState.PRESSED);
            return true;
        } else {
            return false;
        }
    }

    private void loadButtonFromPreferences(
            Preferences preferences, String prefix, VirtualKey virtualKey, int defaultValue) {
        String preferenceKey = prefix + virtualKey.toString().toLowerCase();
        int button = preferences.getInteger(preferenceKey, defaultValue);
        mButtonCodes.put(virtualKey, button);
    }

    private void saveButtonToPreferences(
            Preferences preferences, String prefix, VirtualKey virtualKey) {
        String preferenceKey = prefix + virtualKey.toString().toLowerCase();
        preferences.putInteger(preferenceKey, mButtonCodes.get(virtualKey));
    }

    @Override
    public void loadConfig(Preferences preferences, String prefix, int playerIdx) {
        loadButtonFromPreferences(preferences, prefix, VirtualKey.TRIGGER, 1);
        loadButtonFromPreferences(preferences, prefix, VirtualKey.BACK, 2);
    }

    @Override
    public void saveConfig(Preferences preferences, String prefix) {
        saveButtonToPreferences(preferences, prefix, VirtualKey.TRIGGER);
        saveButtonToPreferences(preferences, prefix, VirtualKey.BACK);
    }

    @Override
    public boolean isAvailable() {
        return mController != null;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        onButtonPressed(buttonCode, true);
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        onButtonPressed(buttonCode, false);
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float fvalue) {
        AxisValue value = normalizeAxisValue(fvalue);
        if ((axisCode & 1) == 0) {
            setKeyJustPressed(VirtualKey.LEFT, value == AxisValue.LESS);
            setKeyJustPressed(VirtualKey.RIGHT, value == AxisValue.MORE);
        } else {
            setKeyJustPressed(VirtualKey.UP, value == AxisValue.LESS);
            setKeyJustPressed(VirtualKey.DOWN, value == AxisValue.MORE);
        }
        return false;
    }

    private void setKeyJustPressed(VirtualKey key, boolean justPressed) {
        mPressedKeys.put(key, justPressed ? KeyState.JUST_PRESSED : KeyState.RELEASED);
    }

    private static AxisValue normalizeAxisValue(float value) {
        if (value < -0.5f) {
            return AxisValue.LESS;
        } else if (value > 0.5f) {
            return AxisValue.MORE;
        } else {
            return AxisValue.ZERO;
        }
    }

    private void onButtonPressed(int buttonCode, boolean pressed) {
        if (mListener != null) {
            if (mListener.onButtonPressed(buttonCode, pressed)) {
                return;
            }
        }
        if (buttonCode == mButtonCodes.get(VirtualKey.TRIGGER)) {
            setKeyJustPressed(VirtualKey.TRIGGER, pressed);
        } else if (buttonCode == mButtonCodes.get(VirtualKey.BACK)) {
            setKeyJustPressed(VirtualKey.BACK, pressed);
        } else {
            VirtualKey key = mVirtualKeyForButton.get(buttonCode);
            if (key != null) {
                setKeyJustPressed(key, pressed);
            }
        }
    }

    private void updateVirtualKeyForButton() {
        mVirtualKeyForButton.clear();
        ControllerMapping mapping = mController.getMapping();
        mVirtualKeyForButton.put(mapping.buttonDpadDown, VirtualKey.DOWN);
        mVirtualKeyForButton.put(mapping.buttonDpadUp, VirtualKey.UP);
        mVirtualKeyForButton.put(mapping.buttonDpadLeft, VirtualKey.LEFT);
        mVirtualKeyForButton.put(mapping.buttonDpadRight, VirtualKey.RIGHT);
        mVirtualKeyForButton.put(mapping.buttonA, VirtualKey.TRIGGER);
        mVirtualKeyForButton.put(mapping.buttonStart, VirtualKey.TRIGGER);
        mVirtualKeyForButton.put(mapping.buttonB, VirtualKey.BACK);
        mVirtualKeyForButton.put(mapping.buttonBack, VirtualKey.BACK);
    }
}
