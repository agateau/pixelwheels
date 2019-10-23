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
import com.badlogic.gdx.utils.IntArray;

public class GamepadInputWatcher {
    public interface Listener {
        void onNotEnoughGamepads();

        void onEnoughGamepads();
    }

    private final GameConfig mGameConfig;
    private final Listener mListener;
    private final IntArray mMissingGamepads = new IntArray(GamepadInputMappers.MAX_GAMEPAD_COUNT);
    private int mInputCount = 0;

    public GamepadInputWatcher(GameConfig gameConfig, Listener listener) {
        mGameConfig = gameConfig;
        mListener = listener;
        GamepadInputMappers.getInstance()
                .addListener(
                        new GamepadInputMappers.Listener() {
                            @Override
                            public void onGamepadConnected() {
                                GamepadInputWatcher.this.onGamepadConnected();
                            }

                            @Override
                            public void onGamepadDisconnected() {
                                GamepadInputWatcher.this.onGamepadDisconnected();
                            }
                        });
    }

    public int getInputCount() {
        return mInputCount;
    }

    public IntArray getMissingGamepads() {
        return mMissingGamepads;
    }

    public void setInputCount(int inputCount) {
        mInputCount = inputCount;
        if (mInputCount == 0) {
            return;
        }
        updateMissingGamepads();
        if (!hasEnoughGamepads()) {
            mListener.onNotEnoughGamepads();
        }
    }

    private void updateMissingGamepads() {
        mMissingGamepads.clear();
        for (int idx = 0; idx < mInputCount; ++idx) {
            GameInputHandler handler = mGameConfig.getPlayerInputHandler(idx);
            if (handler == null || !handler.isAvailable()) {
                NLog.e("Controller for player %d is not available (handler=%s)", idx + 1, handler);
                mMissingGamepads.add(idx);
            }
        }
    }

    private void onGamepadConnected() {
        if (mInputCount == 0 | hasEnoughGamepads()) {
            return;
        }
        updateMissingGamepads();
        if (hasEnoughGamepads()) {
            mListener.onEnoughGamepads();
        } else {
            mListener.onNotEnoughGamepads();
        }
    }

    private void onGamepadDisconnected() {
        if (mInputCount == 0) {
            return;
        }
        updateMissingGamepads();
        if (!hasEnoughGamepads()) {
            mListener.onNotEnoughGamepads();
        }
    }

    private boolean hasEnoughGamepads() {
        return mMissingGamepads.size == 0;
    }
}
