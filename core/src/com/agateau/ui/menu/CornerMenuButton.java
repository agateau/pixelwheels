/*
 * Copyright 2023 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.ui.Scene2dUtils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

public class CornerMenuButton extends ImageButton implements CornerMenuItem {
    private final Rectangle mRect = new Rectangle();
    private final FocusIndicator mFocusIndicator;
    private final Corner mCorner;

    public CornerMenuButton(Menu menu, Corner corner, ImageButtonStyle style) {
        super(style);
        mFocusIndicator = new FocusIndicator(menu);
        mCorner = corner;
    }

    @Override
    public Corner getCorner() {
        return mCorner;
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public boolean isFocusable() {
        return !isDisabled();
    }

    @Override
    public void trigger() {
        Scene2dUtils.simulateClick(this);
    }

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
        mRect.x = 0;
        mRect.y = 0;
        mRect.width = getWidth();
        mRect.height = getHeight();
        return mRect;
    }

    @Override
    public float getParentWidthRatio() {
        return 0;
    }

    @Override
    public void setFocused(boolean focused) {
        mFocusIndicator.setFocused(focused);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        mFocusIndicator.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        mFocusIndicator.draw(batch, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }
}
