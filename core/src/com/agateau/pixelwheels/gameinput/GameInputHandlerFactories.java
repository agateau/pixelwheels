/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.pixelwheels.GamePlay;
import com.agateau.utils.PlatformUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides input handlers
 */
public class GameInputHandlerFactories {
    private static Array<GameInputHandlerFactory> mFactories;

    public static Array<GameInputHandlerFactory> getAvailableFactories() {
        init();
        return mFactories;
    }

    public static Map<String, Array<GameInputHandler>> getInputHandlersByIds() {
        init();
        Map<String, Array<GameInputHandler>> map = new HashMap<String, Array<GameInputHandler>>();
        for (GameInputHandlerFactory factory : mFactories) {
            map.put(factory.getId(), new Array<GameInputHandler>(factory.getAllHandlers()));
        }
        return map;
    }

    public static GameInputHandlerFactory getFactoryById(String id) {
        init();
        if ("".equals(id)) {
            return mFactories.first();
        }
        for (GameInputHandlerFactory factory : mFactories) {
            if (factory.getId().equals(id)) {
                return factory;
            }
        }
        NLog.e("Could not find an input handler factory with id '%s'", id);
        return mFactories.first();
    }

    public static GameInputHandler getDefaultInputHandler() {
        GameInputHandlerFactory factory = mFactories.first();
        return factory.getAllHandlers().get(0);
    }

    public static boolean hasMultitouch() {
        return Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen) || GamePlay.instance.alwaysShowTouchInput;
    }

    private static void init() {
        if (mFactories != null) {
            return;
        }
        mFactories = new Array<GameInputHandlerFactory>();
        if (PlatformUtils.isDesktop()) {
            mFactories.add(new KeyboardInputHandler.Factory());
        }
        if (hasMultitouch()) {
            mFactories.add(new TouchInputHandler.Factory());
        }
        mFactories.add(new GamepadInputHandler.Factory());
    }
}
