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

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public class GamepadInputMappers {
    public static final int MAX_GAMEPAD_COUNT = 4;

    public interface Listener {
        void onGamepadConnected();

        void onGamepadDisconnected();
    }

    private final GamepadInputMapper[] mMappers = new GamepadInputMapper[MAX_GAMEPAD_COUNT];
    private final DelayedRemovalArray<Listener> mListeners = new DelayedRemovalArray<>(0);

    private static GamepadInputMappers sInstance;

    public static GamepadInputMappers getInstance() {
        if (sInstance == null) {
            sInstance = new GamepadInputMappers();
        }

        return sInstance;
    }

    public GamepadInputMapper[] getMappers() {
        return mMappers;
    }

    public void addListener(Listener listener) {
        if (!mListeners.contains(listener, true)) {
            mListeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        mListeners.removeValue(listener, true);
    }

    private GamepadInputMappers() {
        Array<Controller> controllers = Controllers.getControllers();
        for (int idx = 0; idx < mMappers.length; ++idx) {
            Controller controller = idx < controllers.size ? controllers.get(idx) : null;
            mMappers[idx] = new GamepadInputMapper(controller);
        }

        Controllers.addListener(
                new ControllerAdapter() {
                    @Override
                    public void connected(Controller controller) {
                        for (GamepadInputMapper mapper : mMappers) {
                            if (mapper.getController() == null) {
                                mapper.setController(controller);
                                break;
                            }
                        }
                        mListeners.begin();
                        for (Listener listener : mListeners) {
                            listener.onGamepadConnected();
                        }
                        mListeners.end();
                    }

                    @Override
                    public void disconnected(Controller controller) {
                        for (GamepadInputMapper mapper : mMappers) {
                            if (mapper.getController() == controller) {
                                mapper.setController(null);
                                break;
                            }
                        }
                        mListeners.begin();
                        for (Listener listener : mListeners) {
                            listener.onGamepadDisconnected();
                        }
                        mListeners.end();
                    }
                });
    }
}
