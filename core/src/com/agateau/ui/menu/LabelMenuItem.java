package com.agateau.ui.menu;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LabelMenuItem implements MenuItem {
    private final Label mLabel;

    public LabelMenuItem(String text, Skin skin) {
        this(text, skin, "default");
    }

    public LabelMenuItem(String text, Skin skin, String style) {
        mLabel = new Label(text, skin, style);
    }

    public Label getLabel() {
        return mLabel;
    }

    @Override
    public Actor getActor() {
        return mLabel;
    }

    @Override
    public boolean addListener(EventListener eventListener) {
        return false;
    }

    @Override
    public boolean isFocusable() {
        return false;
    }

    @Override
    public void setFocused(boolean focused) {}

    @Override
    public void trigger() {}

    @Override
    public boolean goUp() {
        return false;
    }

    @Override
    public boolean goDown() {
        return false;
    }

    @Override
    public void goLeft() {}

    @Override
    public void goRight() {}

    @Override
    public Rectangle getFocusRectangle() {
        return null;
    }

    @Override
    public float getParentWidthRatio() {
        return 0;
    }
}
