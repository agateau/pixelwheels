package com.agateau.ui;

/**
 * Turn events of an input device into virtual keys
 */
public interface InputMapper {
    boolean isKeyPressed(VirtualKey vkey);

    boolean isKeyJustPressed(VirtualKey vkey);
}
