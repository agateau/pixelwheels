/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
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
package com.agateau.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pools;

/**
 * A clickable menu item
 */
public class ButtonMenuItem extends TextButton implements MenuItem {
    private final Menu mMenu;
    private final Rectangle mRect = new Rectangle();

    public ButtonMenuItem(Menu menu, String text, Skin skin) {
        super(text, skin);
        mMenu = menu;

        addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                mMenu.setCurrentItem(ButtonMenuItem.this);
                trigger();
            }
        });
    }

    @Override
    public float getPrefWidth() {
        if (mMenu == null) {
            return super.getPrefWidth();
        }
        return mMenu.getDefaultItemWidth();
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void trigger() {
        ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
        fire(changeEvent);
        Pools.free(changeEvent);
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
    public void goLeft() {

    }

    @Override
    public void goRight() {

    }

    @Override
    public Rectangle getFocusRectangle() {
        mRect.x = getX();
        mRect.y = getY();
        mRect.width = getWidth();
        mRect.height = getHeight();
        return mRect;
    }
}
