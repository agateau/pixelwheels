/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.ui.InputMapper;
import com.agateau.ui.Scene2dUtils;
import com.agateau.ui.VirtualKey;
import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * A keyboard and game controller friendly menu system
 *
 * Sends ChangeEvent when the current item changes.
 */
public class Menu extends Group {
    private static final float SELECTION_ANIMATION_DURATION = 0.2f;
    private final MenuInputHandler mMenuInputHandler = new MenuInputHandler();
    private final Image mFocusIndicator;
    private final MenuItemGroup mGroup;
    private final Skin mSkin;
    private MenuStyle mStyle;

    private Vector2 mTmp = new Vector2();

    enum FocusIndicatorMovement {
        IMMEDIATE,
        ANIMATED
    }

    public static class MenuStyle {
        public Drawable focus;
        public int spacing;
        public int focusPadding;

        public MenuStyle() {
        }
    }

    public Menu(Skin skin) {
        this(skin, "default");
    }

    public Menu(Skin skin, String styleName) {
        mSkin = skin;
        mStyle = skin.get(styleName, MenuStyle.class);

        mFocusIndicator = new Image(mStyle.focus);
        mFocusIndicator.setTouchable(Touchable.disabled);

        mGroup = new MenuItemGroup(this);

        addActor(mFocusIndicator);
        addActor(mGroup.getActor());
    }

    public Skin getSkin() {
        return mSkin;
    }

    public MenuStyle getMenuStyle() {
        return mStyle;
    }

    public void setInputMapper(InputMapper inputMapper) {
        mMenuInputHandler.setInputMapper(inputMapper);
    }

    @SuppressWarnings("unused")
    public float getDefaultItemWidth() {
        return mGroup.getDefaultItemWidth();
    }

    public void setDefaultItemWidth(float defaultItemWidth) {
        mGroup.setDefaultItemWidth(defaultItemWidth);
    }

    @SuppressWarnings("unused")
    public float getLabelColumnWidth() {
        return mGroup.getLabelColumnWidth();
    }

    public void setLabelColumnWidth(float labelColumnWidth) {
        mGroup.setLabelColumnWidth(labelColumnWidth);
    }

    public MenuItem addButton(String text) {
        return mGroup.addButton(text);
    }

    /**
     * Add a plain label in the menu
     * @return The created label
     */
    public LabelMenuItem addLabel(String text) {
        return mGroup.addLabel(text);
    }

    /**
     * Add a "title" label in the menu (uses the "menuTitle" label style)
     * @return The created label
     */
    @SuppressWarnings("UnusedReturnValue")
    public LabelMenuItem addTitleLabel(String text) {
        return mGroup.addTitleLabel(text);
    }

    /**
     * Add a full-width item
     */
    public MenuItem addItem(MenuItem item) {
        return mGroup.addItem(item);
    }

    /**
     * Add a [label - item] row
     */
    public MenuItem addItemWithLabel(String labelText, MenuItem item) {
        return mGroup.addItemWithLabel(labelText, item);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        mMenuInputHandler.act(delta);
        if (mMenuInputHandler.isPressed(VirtualKey.DOWN)) {
            mGroup.goDown();
        } else if (mMenuInputHandler.isPressed(VirtualKey.UP)) {
            mGroup.goUp();
        } else if (mMenuInputHandler.isPressed(VirtualKey.LEFT)) {
            getCurrentItem().goLeft();
        } else if (mMenuInputHandler.isPressed(VirtualKey.RIGHT)) {
            getCurrentItem().goRight();
        } else if (mMenuInputHandler.isJustPressed(VirtualKey.TRIGGER)) {
            mGroup.trigger();
        }
    }

    public void setCurrentItem(MenuItem item) {
        mGroup.setCurrentItem(item);
    }

    public MenuItem getCurrentItem() {
        return mGroup.getCurrentItem();
    }

    public boolean isItemVisible(MenuItem item) {
        return mGroup.isItemVisible(item);
    }

    public void setItemVisible(MenuItem item, boolean visible) {
        mGroup.setItemVisible(item, visible);
    }

    void animateFocusIndicator() {
        updateFocusIndicatorBounds(FocusIndicatorMovement.ANIMATED);
    }

    void updateFocusIndicatorBounds(FocusIndicatorMovement movement) {
        MenuItem item = getCurrentItem();
        if (item == null) {
            return;
        }
        Rectangle rect = item.getFocusRectangle();
        mapDescendantRectangle(item.getActor(), rect);
        AgcMathUtils.adjustRectangle(rect, mStyle.focusPadding);

        mFocusIndicator.clearActions();
        if (movement == FocusIndicatorMovement.IMMEDIATE) {
            mFocusIndicator.setBounds(rect.x, rect.y, rect.width, rect.height);
        } else {
            mFocusIndicator.addAction(Actions.moveTo(rect.x, rect.y, SELECTION_ANIMATION_DURATION, Interpolation.pow2Out));
            mFocusIndicator.addAction(Actions.sizeTo(rect.width, rect.height, SELECTION_ANIMATION_DURATION, Interpolation.pow2Out));
        }
        Scene2dUtils.fireChangeEvent(this);
    }

    void hideFocusIndicator() {
        mFocusIndicator.addAction(Actions.fadeOut(Menu.SELECTION_ANIMATION_DURATION));
    }

    void mapDescendantRectangle(Actor actor, Rectangle rect) {
        mTmp.set(rect.x, rect.y);
        mTmp = actor.localToAscendantCoordinates(mGroup.getActor(), mTmp);
        rect.x = mTmp.x;
        rect.y = mTmp.y;
    }
}
