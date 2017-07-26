package com.agateau.ui;

/**
 * Monitor input events for the menu
 */
public class MenuInputHandler {
    KeyMapper mKeyMapper = new KeyMapper();

    public boolean isPressed(VirtualKey vkey) {
        return mKeyMapper.isKeyJustPressed(vkey);
    }

    public void act(float delta) {

    }

    public void setKeyMapper(KeyMapper keyMapper) {
        mKeyMapper = keyMapper;
    }
}
