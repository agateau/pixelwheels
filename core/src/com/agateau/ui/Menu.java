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

import com.agateau.utils.Assert;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

/**
 * A keyboard and game controller friendly menu system
 */

public class Menu extends ScrollPane {
    private static final float SELECTION_ANIMATION_DURATION = 0.2f;
    private final MenuInputHandler mMenuInputHandler = new MenuInputHandler();
    private final Image mFocusIndicator;
    private final Group mPaneWidget;
    private final VerticalGroup mContainer;
    private final Skin mSkin;
    private MenuStyle mStyle;
    private float mDefaultItemWidth = 300;

    private int mCurrentIndex = -1;

    private final Array<MenuItem> mItems = new Array<MenuItem>();

    public static class MenuStyle {
        public Drawable focus;
        public int focusPadding;

        public MenuStyle() {
        }
    }

    public Menu(Skin skin) {
        super(null);
        mSkin = skin;
        mStyle = skin.get(MenuStyle.class);

        mFocusIndicator = new Image(mStyle.focus);
        mFocusIndicator.setTouchable(Touchable.disabled);

        mPaneWidget = new Group();

        mContainer = new VerticalGroup() {
            @Override
            public void layout() {
                super.layout();
                updateFocusIndicatorBounds();
            }
        };
        mContainer.pad(mStyle.focusPadding);
        mContainer.space(mStyle.focusPadding * 2);

        mPaneWidget.addActor(mContainer);
        mPaneWidget.addActor(mFocusIndicator);

        setWidget(mPaneWidget);
    }

    public Skin getSkin() {
        return mSkin;
    }

    public MenuStyle getMenuStyle() {
        return mStyle;
    }

    public void setKeyMapper(KeyMapper keyMapper) {
        mMenuInputHandler.setKeyMapper(keyMapper);
    }

    public float getDefaultItemWidth() {
        return mDefaultItemWidth;
    }

    public void setDefaultItemWidth(float defaultItemWidth) {
        mDefaultItemWidth = defaultItemWidth;
    }

    public Actor addButton(String text) {
        return addItem(new ButtonMenuItem(this, text, mSkin));
    }

    public Actor addItem(MenuItem item) {
        mItems.add(item);
        if (mCurrentIndex == -1) {
            mCurrentIndex = mItems.size - 1;
        }

        mContainer.addActor(item.getActor());

        float width = mContainer.getPrefWidth();
        float height = mContainer.getPrefHeight();

        mContainer.setSize(width, height);
        mPaneWidget.setSize(width, height);
        setSize(width, height);

        return item.getActor();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        mMenuInputHandler.act(delta);
        if (mMenuInputHandler.isPressed(VirtualKey.DOWN)) {
            if (!getCurrentItem().goDown()) {
                adjustIndex(1);
            }
        } else if (mMenuInputHandler.isPressed(VirtualKey.UP)) {
            if (!getCurrentItem().goUp()) {
                adjustIndex(-1);
            }
        } else if (mMenuInputHandler.isPressed(VirtualKey.LEFT)) {
            getCurrentItem().goLeft();
        } else if (mMenuInputHandler.isPressed(VirtualKey.RIGHT)) {
            getCurrentItem().goRight();
        } else if (mMenuInputHandler.isJustPressed(VirtualKey.TRIGGER)) {
            triggerCurrentItem();
        }
    }

    public void setCurrentItem(MenuItem item) {
        if (item == null) {
            setCurrentIndex(-1);
            return;
        }
        int index = mItems.indexOf(item, /* identity= */ true);
        Assert.check(index != -1, "Invalid item");
        setCurrentIndex(index);
    }

    public MenuItem getCurrentItem() {
        return mCurrentIndex >= 0 ? mItems.get(mCurrentIndex) : null;
    }

    private void adjustIndex(int delta) {
        setCurrentIndex(MathUtils.clamp(mCurrentIndex + delta, 0, mItems.size - 1));
    }

    private void triggerCurrentItem() {
        if (mCurrentIndex < 0) {
            return;
        }
        mItems.get(mCurrentIndex).trigger();
    }

    private void setCurrentIndex(int index) {
        int old = mCurrentIndex;
        mCurrentIndex = index;
        if (old >= 0 && mCurrentIndex == -1) {
            mFocusIndicator.addAction(Actions.fadeOut(SELECTION_ANIMATION_DURATION));
        } else if (old == -1) {
            updateFocusIndicatorBounds();
            mFocusIndicator.addAction(Actions.fadeIn(SELECTION_ANIMATION_DURATION));
        } else {
            animateFocusIndicator();
        }
    }

    void animateFocusIndicator() {
        MenuItem item = getCurrentItem();
        if (item == null) {
            return;
        }
        Rectangle rect = item.getFocusRectangle();
        mFocusIndicator.addAction(Actions.moveTo(rect.x - mStyle.focusPadding, rect.y - mStyle.focusPadding, SELECTION_ANIMATION_DURATION, Interpolation.pow2Out));
        mFocusIndicator.addAction(Actions.sizeTo(rect.width + 2 * mStyle.focusPadding, rect.height + 2 * mStyle.focusPadding, SELECTION_ANIMATION_DURATION, Interpolation.pow2Out));
        ensureItemVisible();
    }

    private void updateFocusIndicatorBounds() {
        MenuItem item = getCurrentItem();
        if (item == null) {
            return;
        }
        Rectangle rect = item.getFocusRectangle();
        mFocusIndicator.setBounds(rect.x - mStyle.focusPadding, rect.y - mStyle.focusPadding, rect.width + 2 * mStyle.focusPadding, rect.height + 2 * mStyle.focusPadding);
        ensureItemVisible();
    }

    private void ensureItemVisible() {
        MenuItem item = getCurrentItem();
        Rectangle rect = item.getFocusRectangle();
        scrollTo(rect.x - mStyle.focusPadding, rect.y - mStyle.focusPadding, rect.width + 2 * mStyle.focusPadding, rect.height + 2 * mStyle.focusPadding);
    }
}
