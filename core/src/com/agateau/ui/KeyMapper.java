package com.agateau.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.HashMap;

/**
 * Provide mapping between real and virtual keys
 */
public class KeyMapper {
    private final HashMap<VirtualKey, Integer> mKeyForVirtualKey = new HashMap<VirtualKey, Integer>();

    public KeyMapper() {
        put(VirtualKey.LEFT, Input.Keys.LEFT);
        put(VirtualKey.RIGHT, Input.Keys.RIGHT);
        put(VirtualKey.UP, Input.Keys.UP);
        put(VirtualKey.DOWN, Input.Keys.DOWN);
        put(VirtualKey.TRIGGER, Input.Keys.SPACE);
        put(VirtualKey.BACK, Input.Keys.ESCAPE);
    }

    public void put(VirtualKey vkey, Integer key) {
        mKeyForVirtualKey.put(vkey, key);
    }

    public boolean isKeyPressed(VirtualKey vkey) {
        return Gdx.input.isKeyPressed(mKeyForVirtualKey.get(vkey));
    }

    public boolean isKeyJustPressed(VirtualKey vkey) {
        return Gdx.input.isKeyJustPressed(mKeyForVirtualKey.get(vkey));
    }
}
