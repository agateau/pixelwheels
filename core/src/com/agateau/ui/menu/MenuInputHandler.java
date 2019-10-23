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
package com.agateau.ui.menu;

import com.agateau.ui.InputMapper;
import com.agateau.ui.UiInputMapper;
import com.agateau.ui.VirtualKey;

/**
 * Monitor input events for the menu
 *
 * <p>Provides an API similar to Gdx.input but works with {@link VirtualKey}. Handles key repeat.
 */
public class MenuInputHandler {
    private static final float REPEAT_DELAY = 0.6f;
    private static final float REPEAT_RATE = 0.025f;

    private enum State {
        STARTING,
        NORMAL,
        KEY_DOWN,
        REPEATING
    }

    private InputMapper mInputMapper = UiInputMapper.getInstance();
    private State mState = State.STARTING;

    private VirtualKey mPressedVirtualKey = null;
    private VirtualKey mJustPressedVirtualKey = null;
    private float mRepeatDelay = 0;

    /**
     * Returns true if the key is being pressed. If the key is held down, this will return true at
     * regular intervals, like an auto-repeat keyboard
     *
     * @param vkey the key to check
     * @return True if the key is being pressed
     */
    public boolean isPressed(VirtualKey vkey) {
        return mPressedVirtualKey == vkey && mRepeatDelay < 0;
    }

    /**
     * Returns true if the key has been pressed, then released
     *
     * @param vkey the key to check
     * @return True if the key has been pressed, then released
     */
    public boolean isJustPressed(VirtualKey vkey) {
        return mJustPressedVirtualKey == vkey;
    }

    public void act(float delta) {
        if (mState == State.STARTING) {
            // If a key is already down at startup, ignore it. If no key is down, go to NORMAL state
            if (findPressedKey() == null) {
                mState = State.NORMAL;
            }
        } else if (mState == State.NORMAL) {
            // Not repeating yet
            mJustPressedVirtualKey = null;
            VirtualKey virtualKey = findPressedKey();
            if (virtualKey != null) {
                mPressedVirtualKey = virtualKey;
                // Set delay to -1 so that next call to isPressed() returns true
                mRepeatDelay = -1;
                mState = State.KEY_DOWN;
            }
        } else {
            // Repeating
            if (mInputMapper.isKeyPressed(mPressedVirtualKey)) {
                if (mRepeatDelay > 0) {
                    mRepeatDelay -= delta;
                } else {
                    if (mState == State.KEY_DOWN) {
                        mRepeatDelay = REPEAT_DELAY;
                        mState = State.REPEATING;
                    } else {
                        mRepeatDelay = REPEAT_RATE;
                    }
                }
            } else {
                // Key has been released, not repeating anymore
                mState = State.NORMAL;
                mJustPressedVirtualKey = mPressedVirtualKey;
            }
        }
    }

    public InputMapper getInputMapper() {
        return mInputMapper;
    }

    public void setInputMapper(InputMapper inputMapper) {
        mInputMapper = inputMapper;
    }

    private VirtualKey findPressedKey() {
        for (VirtualKey virtualKey : VirtualKey.values()) {
            if (mInputMapper.isKeyPressed(virtualKey)) {
                return virtualKey;
            }
        }
        return null;
    }
}
