/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.ui;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.utils.Array;

/**
 * An implementation of InputMapper for gamepads
 */
public class GamepadInputMapper extends ControllerAdapter implements InputMapper {
    private static final int MAX_GAMEPAD_COUNT = 4;

    private enum AxisValue {
        LESS,
        ZERO,
        MORE
    }

    private boolean mActive;

    private AxisValue[] mAxisValues = new AxisValue[2];
    private boolean mButtonDown = false;

    private static final GamepadInputMapper[] sInstances = new GamepadInputMapper[MAX_GAMEPAD_COUNT];

    public static GamepadInputMapper[] getInstances() {
        if (sInstances[0] == null) {
            createInstances();
        }
        return sInstances;
    }

    public static GamepadInputMapper getInstance(int idx) {
        return getInstances()[idx];
    }

    private static void createInstances() {
        for (int idx = 0; idx < sInstances.length; ++idx) {
            sInstances[idx] = new GamepadInputMapper(idx);
        }
    }

    private GamepadInputMapper(int idx) {
        mAxisValues[0] = AxisValue.ZERO;
        mAxisValues[1] = AxisValue.ZERO;
        Array<Controller> controllers = Controllers.getControllers();
        if (idx < controllers.size) {
            controllers.get(idx).addListener(this);
            mActive = true;
        } else {
            mActive = false;
        }
    }

    public boolean isActive() {
        return mActive;
    }

    @Override
    public boolean isKeyPressed(VirtualKey key) {
        switch (key) {
        case UP:
            return mAxisValues[1] == AxisValue.LESS;
        case DOWN:
            return mAxisValues[1] == AxisValue.MORE;
        case LEFT:
            return mAxisValues[0] == AxisValue.LESS;
        case RIGHT:
            return mAxisValues[0] == AxisValue.MORE;
        case TRIGGER:
            return mButtonDown;
        case BACK:
            return false;
        }
        NLog.e("Unknown key %s", key);
        return false;
    }

    @Override
    public boolean isKeyJustPressed(VirtualKey vkey) {
        return isKeyPressed(vkey);
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        NLog.i("buttonCode=%d", buttonCode);
        mButtonDown = true;
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        NLog.i("buttonCode=%d", buttonCode);
        mButtonDown = false;
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        NLog.i("povCode=%d value=%s", povCode, value);
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (axisCode < mAxisValues.length) {
            mAxisValues[axisCode] = normalizeAxisValue(value);
        }
        return false;
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
}
