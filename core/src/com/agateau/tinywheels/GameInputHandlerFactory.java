package com.agateau.tinywheels;

/**
 * A factory to create GameInputHandlers and provide information about them
 */
public interface GameInputHandlerFactory {
    String getId();
    String getName();
    String getDescription();
    GameInputHandler create();
}
