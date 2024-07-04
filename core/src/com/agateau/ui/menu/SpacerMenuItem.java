/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
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

public class SpacerMenuItem implements MenuItem {
    private final Actor mActor;

    public SpacerMenuItem(int height) {
        mActor = new Actor();
        mActor.setHeight(height);
    }

    @Override
    public Actor getActor() {
        return mActor;
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

    @Override
    public void setFocused(boolean focused) {}
}
