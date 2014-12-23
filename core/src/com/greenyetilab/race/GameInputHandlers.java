package com.greenyetilab.race;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.log.NLog;

/**
 * Provides input handlers
 */
public class GameInputHandlers {
    private static Array<GameInputHandler> mHandlers;

    public static Array<GameInputHandler> getAvailableHandlers() {
        init();
        return mHandlers;
    }

    public static GameInputHandler getHandlerByName(String name) {
        init();
        for (GameInputHandler handler : mHandlers) {
            if (handler.toString().equals(name)) {
                return handler;
            }
        }
        NLog.e("Could not find an input handler named '%s'", name);
        return mHandlers.first();
    }

    private static void init() {
        if (mHandlers != null) {
            return;
        }
        mHandlers = new Array<GameInputHandler>();
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            mHandlers.add(new KeyboardInputHandler());
        }
        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
            mHandlers.add(new TouchInputHandler());
        }
        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            mHandlers.add(new AccelerometerInputHandler());
        }
    }
}
