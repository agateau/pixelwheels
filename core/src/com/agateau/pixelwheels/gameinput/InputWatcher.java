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
package com.agateau.pixelwheels.gameinput;

import com.agateau.pixelwheels.GameConfig;
import com.agateau.utils.Assert;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class InputWatcher {
    public interface Listener {
        void onNotEnoughControllers();
        void onEnoughControllers();
    }

    private final GameConfig mGameConfig;
    private final Listener mListener;
    private int mInputCount = 0;
    private boolean mEnoughInput = true;

    private final ControllerListener mControllerListener = new ControllerListener() {
        @Override
        public void connected(Controller controller) {
            NLog.d("controller=%s %s", controller, controller.getName());
            onControllerConnected();
        }

        @Override
        public void disconnected(Controller controller) {
            NLog.d("controller=%s %s", controller, controller.getName());
            onControllerDisconnected();
        }

        @Override
        public boolean buttonDown(Controller controller, int buttonCode) {
            return false;
        }

        @Override
        public boolean buttonUp(Controller controller, int buttonCode) {
            return false;
        }

        @Override
        public boolean axisMoved(Controller controller, int axisCode, float value) {
            return false;
        }

        @Override
        public boolean povMoved(Controller controller, int povCode, PovDirection value) {
            return false;
        }

        @Override
        public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
            return false;
        }

        @Override
        public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
            return false;
        }

        @Override
        public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
            return false;
        }
    };

    public InputWatcher(GameConfig gameConfig, Listener listener) {
        mGameConfig = gameConfig;
        mListener = listener;
        Controllers.addListener(mControllerListener);
    }

    public void setInputCount(int inputCount) {
        mInputCount = inputCount;
        if (mInputCount == 0) {
            return;
        }
        mEnoughInput = checkEnoughInputHandlers();
        if (!mEnoughInput) {
            mListener.onNotEnoughControllers();
        }
    }

    private boolean checkEnoughInputHandlers() {
        NLog.d("");
        for (int idx = 0; idx < mInputCount; ++idx) {
            GameInputHandler handler = mGameConfig.getPlayerInputHandler(idx);
            Assert.check(handler != null, "Missing GameInputHandler");
            if (!handler.isAvailable()) {
                NLog.e("Controller for player %d is not available", idx + 1);
                return false;
            }
        }
        return true;
    }

    private void onControllerConnected() {
        NLog.d("");
        if (mInputCount == 0 | mEnoughInput) {
            return;
        }
        if (checkEnoughInputHandlers()) {
            mEnoughInput = true;
            mListener.onEnoughControllers();
        }
    }

    private void onControllerDisconnected() {
        NLog.d("mInputCount=%d mEnoughInput=%b", mInputCount, mEnoughInput);
        if (mInputCount == 0 | !mEnoughInput) {
            return;
        }
        if (!checkEnoughInputHandlers()) {
            mEnoughInput = false;
            mListener.onNotEnoughControllers();
        }
    }
}
