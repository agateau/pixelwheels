/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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
import com.agateau.ui.GamepadInputMappers;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.utils.Array;

public class EnoughGamepadsChecker {

    public interface Listener {
        void onNotEnoughInputs();

        void onEnoughInputs();
    }

    private final GameConfig mGameConfig;
    private final Listener mListener;
    private final Array<String> mInputNames = new Array<>();
    private int mMissingInputCount = 0;
    private int mInputCount = 0;

    public EnoughGamepadsChecker(GameConfig gameConfig, Listener listener) {
        mGameConfig = gameConfig;
        mListener = listener;
        GamepadInputMappers.getInstance()
                .addListener(
                        new GamepadInputMappers.Listener() {
                            @Override
                            public void onGamepadConnected() {
                                EnoughGamepadsChecker.this.onGamepadConnected();
                            }

                            @Override
                            public void onGamepadDisconnected() {
                                EnoughGamepadsChecker.this.onGamepadDisconnected();
                            }
                        });
    }

    public int getInputCount() {
        return mInputCount;
    }

    /** Returns a list of inputs for each player. Entry is null (not empty) if it's missing */
    public Array<String> getInputNames() {
        return mInputNames;
    }

    public void setInputCount(int inputCount) {
        mInputCount = inputCount;
        if (mInputCount == 0) {
            return;
        }
        update();
        if (!hasEnoughInputs()) {
            mListener.onNotEnoughInputs();
        }
    }

    private void update() {
        mInputNames.clear();
        mMissingInputCount = 0;
        for (int idx = 0; idx < mInputCount; ++idx) {
            GameInputHandler handler = mGameConfig.getPlayerInputHandler(idx);
            if (handler == null || !handler.isAvailable()) {
                NLog.e("Controller for player %d is not available (handler=%s)", idx + 1, handler);
                mMissingInputCount++;
                mInputNames.add(null);
            } else {
                String name = handler.getName();
                if (name.equals("")) {
                    name = handler.getTypeName();
                }
                mInputNames.add(name);
            }
        }
    }

    private void onGamepadConnected() {
        if (mInputCount == 0 | hasEnoughInputs()) {
            return;
        }
        update();
        if (hasEnoughInputs()) {
            mListener.onEnoughInputs();
        } else {
            mListener.onNotEnoughInputs();
        }
    }

    private void onGamepadDisconnected() {
        if (mInputCount == 0) {
            return;
        }
        update();
        if (!hasEnoughInputs()) {
            mListener.onNotEnoughInputs();
        }
    }

    private boolean hasEnoughInputs() {
        return mMissingInputCount == 0;
    }
}
