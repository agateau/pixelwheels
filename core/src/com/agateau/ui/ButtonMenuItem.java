package com.agateau.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * A clickable menu item
 */
public class ButtonMenuItem extends TextButton implements MenuItem {
    public ButtonMenuItem(String text, Skin skin) {
        super(text, skin);
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void trigger() {
        toggle();
    }
}
