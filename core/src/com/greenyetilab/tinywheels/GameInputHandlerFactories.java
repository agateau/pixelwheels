package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.log.NLog;

/**
 * Provides input handlers
 */
public class GameInputHandlerFactories {
    private static Array<GameInputHandlerFactory> mFactories;

    public static Array<GameInputHandlerFactory> getAvailableFactories() {
        init();
        return mFactories;
    }

    public static GameInputHandlerFactory getFactoryById(String id) {
        init();
        for (GameInputHandlerFactory factory : mFactories) {
            if (factory.getId().equals(id)) {
                return factory;
            }
        }
        NLog.e("Could not find an input handler factory with id '%s'", id);
        return mFactories.first();
    }

    public static boolean hasMultitouch() {
        return Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen) || GamePlay.instance.alwaysShowTouchInput;
    }

    private static void init() {
        if (mFactories != null) {
            return;
        }
        mFactories = new Array<GameInputHandlerFactory>();
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            mFactories.add(new KeyboardInputHandler.Factory());
        }
        if (hasMultitouch()) {
            mFactories.add(new TouchInputHandler.Factory());
        }
    }
}
