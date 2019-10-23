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
import com.badlogic.gdx.controllers.PovDirection;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/** An implementation of InputMapper for gamepads */
public class GamepadInputMapper extends ControllerAdapter implements InputMapper {
    private static final String TRIGGER_BUTTON_PREF = "trigger";
    private static final String BACK_BUTTON_PREF = "back";

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

    public enum GamepadButton {
        TRIGGER,
        BACK
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

    private final HashMap<GamepadButton, Integer> mButtonCodes = new HashMap<>();

    private WeakReference<Listener> mListenerRef;

    public static GamepadInputMapper[] getInstances() {
        return GamepadInputMappers.getInstance().getMappers();
    }

    public static GamepadInputMapper getInstance(int idx) {
        return GamepadInputMappers.getInstance().getMappers()[idx];
    }

    GamepadInputMapper(Controller controller) {
        mButtonCodes.put(GamepadButton.TRIGGER, 1);
        mButtonCodes.put(GamepadButton.BACK, 2);
        setController(controller);
    }

    Controller getController() {
        return mController;
    }

    void setController(Controller controller) {
        mController = controller;
        if (controller != null) {
            mController.addListener(this);
        }
    }

    public int getButtonCode(GamepadButton button) {
        return mButtonCodes.get(button);
    }

    public void setButtonCode(GamepadButton button, int code) {
        mButtonCodes.put(button, code);
    }

    public void setListener(Listener listener) {
        mListenerRef = new WeakReference<>(listener);
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

    @Override
    public void loadConfig(Preferences preferences, String prefix) {
        mButtonCodes.put(
                GamepadButton.TRIGGER, preferences.getInteger(prefix + TRIGGER_BUTTON_PREF, 1));
        mButtonCodes.put(GamepadButton.BACK, preferences.getInteger(prefix + BACK_BUTTON_PREF, 2));
    }

    @Override
    public void saveConfig(Preferences preferences, String prefix) {
        preferences.putInteger(
                prefix + TRIGGER_BUTTON_PREF, mButtonCodes.get(GamepadButton.TRIGGER));
        preferences.putInteger(prefix + BACK_BUTTON_PREF, mButtonCodes.get(GamepadButton.BACK));
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
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        boolean up = false;
        boolean down = false;
        boolean left = false;
        boolean right = false;
        switch (value) {
            case center:
                break;
            case north:
                up = true;
                break;
            case south:
                down = true;
                break;
            case east:
                right = true;
                break;
            case west:
                left = true;
                break;
            case northEast:
                up = true;
                right = true;
                break;
            case southEast:
                down = true;
                right = true;
                break;
            case northWest:
                up = true;
                left = true;
                break;
            case southWest:
                down = true;
                left = true;
                break;
        }
        setKeyJustPressed(VirtualKey.UP, up);
        setKeyJustPressed(VirtualKey.DOWN, down);
        setKeyJustPressed(VirtualKey.LEFT, left);
        setKeyJustPressed(VirtualKey.RIGHT, right);
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
        Listener listener = mListenerRef != null ? mListenerRef.get() : null;
        if (listener != null) {
            if (listener.onButtonPressed(buttonCode, pressed)) {
                return;
            }
        }
        if (buttonCode == mButtonCodes.get(GamepadButton.TRIGGER)) {
            setKeyJustPressed(VirtualKey.TRIGGER, pressed);
        } else if (buttonCode == mButtonCodes.get(GamepadButton.BACK)) {
            setKeyJustPressed(VirtualKey.BACK, pressed);
        }
    }
}
