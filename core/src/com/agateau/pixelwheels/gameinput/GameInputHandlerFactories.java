/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.pixelwheels.debug.Debug;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import java.util.Map;

/** Provides input handlers */
public class GameInputHandlerFactories {
    private static Array<GameInputHandlerFactory> mFactories;

    public static Array<GameInputHandlerFactory> getAvailableFactories() {
        init();
        return mFactories;
    }

    public static Map<String, Array<GameInputHandler>> getInputHandlersByIds() {
        init();
        Map<String, Array<GameInputHandler>> map = new HashMap<>();
        for (GameInputHandlerFactory factory : mFactories) {
            map.put(factory.getId(), new Array<>(factory.getAllHandlers()));
        }
        return map;
    }

    public static GameInputHandlerFactory getFactoryById(String id) {
        init();
        if ("".equals(id)) {
            GameInputHandlerFactory factory = mFactories.first();
            NLog.i("No input handler selected, using '%s'", factory.getId());
            return factory;
        }
        for (GameInputHandlerFactory factory : mFactories) {
            if (factory.getId().equals(id)) {
                return factory;
            }
        }
        NLog.e("Could not find an input handler factory with id '%s'", id);
        return mFactories.first();
    }

    public static boolean hasMultitouch() {
        return Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)
                || Debug.instance.alwaysShowTouchInput;
    }

    private static void init() {
        if (mFactories != null) {
            return;
        }
        mFactories = new Array<>();

        if (hasMultitouch()) {
            mFactories.add(new PieTouchInputHandler.Factory());
            mFactories.add(new SidesTouchInputHandler.Factory());
        }
        // We used to only add the keyboard input handler if this returned true:
        //
        //   Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard)
        //
        // but it always returned false on Android (at least with libgdx 1.9.8).
        // Since it does not hurt to have it always there, add it unconditionally
        // so that playing with the keyboard works on Android.
        mFactories.add(new KeyboardInputHandler.Factory());
        mFactories.add(new GamepadInputHandler.Factory());
    }
}
