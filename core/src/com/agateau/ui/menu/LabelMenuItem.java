/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.ui.menu;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * A MenuItem to show a static text
 *
 * <p>To wrap long texts, call setWrap(true)
 */
public class LabelMenuItem implements MenuItem {
    private final Label mLabel;

    public LabelMenuItem(String text, Skin skin) {
        this(text, skin, "default");
    }

    public LabelMenuItem(String text, Skin skin, String style) {
        mLabel = new Label(text, skin, style);
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
        return mLabel.getWrap() ? 1 : 0;
    }

    public void setWrap(boolean wrap) {
        mLabel.setWrap(wrap);
    }

    public void setText(CharSequence text) {
        mLabel.setText(text);
        mLabel.pack();
    }
}
