package com.agateau.ui;

import com.badlogic.gdx.Preferences;

/**
 * Turn events of an input device into virtual keys
 */
public interface InputMapper {
    boolean isKeyPressed(VirtualKey vkey);

    boolean isKeyJustPressed(VirtualKey vkey);

    void loadConfig(Preferences preferences, String prefix);

    void saveConfig(Preferences preferences, String prefix);
}
