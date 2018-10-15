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
    private final DelayedRemovalArray<Listener> mListeners = new DelayedRemovalArray<Listener>(0);

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

        Controllers.addListener(new ControllerAdapter() {
            @Override
            public void connected(Controller controller) {
                for(GamepadInputMapper mapper : mMappers) {
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
                for(GamepadInputMapper mapper : mMappers) {
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
