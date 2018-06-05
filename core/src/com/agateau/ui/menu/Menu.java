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
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.AgcMathUtils;
import com.agateau.utils.Assert;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * A keyboard and game controller friendly menu system
 *
 * Sends ChangeEvent when the current item changes.
 */
public class Menu extends Group {
    private static final float SELECTION_ANIMATION_DURATION = 0.2f;
    private final MenuInputHandler mMenuInputHandler = new MenuInputHandler();
    private final Image mFocusIndicator;
    private final VerticalGroup mContainer;
    private final Skin mSkin;
    private MenuStyle mStyle;

    private float mLabelColumnWidth = 120;
    private float mDefaultItemWidth = 300;

    private int mCurrentIndex = -1;

    private final Array<MenuItem> mItems = new Array<MenuItem>();

    private final HashMap<MenuItem, Actor> mActorForItem = new HashMap<MenuItem, Actor>();

    private Vector2 mTmp = new Vector2();

    private enum FocusIndicatorMovement {
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

        mContainer = new VerticalGroup() {
            @Override
            public void layout() {
                super.layout();
                updateBounds();
                updateFocusIndicatorBounds(FocusIndicatorMovement.IMMEDIATE);
            }
        };
        mContainer.pad(mStyle.focusPadding);
        mContainer.space(mStyle.focusPadding * 2 + mStyle.spacing);
        mContainer.fill();
        mContainer.addCaptureListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                MenuItem item = getItemAt(x, y);
                if (item != null) {
                    setCurrentItem(item);
                }
                return false;
            }
        });

        addActor(mFocusIndicator);
        addActor(mContainer);
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
        return mDefaultItemWidth;
    }

    public void setDefaultItemWidth(float defaultItemWidth) {
        mDefaultItemWidth = defaultItemWidth;
    }

    @SuppressWarnings("unused")
    public float getLabelColumnWidth() {
        return mLabelColumnWidth;
    }

    public void setLabelColumnWidth(float labelColumnWidth) {
        mLabelColumnWidth = labelColumnWidth;
    }

    public MenuItem addButton(String text) {
        return addItem(new ButtonMenuItem(this, text, mSkin));
    }

    /**
     * Add a plain label in the menu
     * @return The created label
     */
    public LabelMenuItem addLabel(String text) {
        LabelMenuItem labelMenuItem = new LabelMenuItem(text, mSkin);
        addItem(labelMenuItem);
        return labelMenuItem;
    }

    /**
     * Add a "title" label in the menu (uses the "menuTitle" label style)
     * @return The created label
     */
    @SuppressWarnings("UnusedReturnValue")
    public LabelMenuItem addTitleLabel(String text) {
        LabelMenuItem labelMenuItem = new LabelMenuItem(text, mSkin, "menuTitle");
        addItem(labelMenuItem);
        return labelMenuItem;
    }

    /**
     * Add a full-width item
     */
    public MenuItem addItem(MenuItem item) {
        item.setDefaultColumnWidth(mDefaultItemWidth);
        addItemInternal(item, item.getActor());
        return item;
    }

    /**
     * Add a [label - item] row
     */
    public MenuItem addItemWithLabel(String labelText, MenuItem item) {
        Actor actor = item.getActor();
        float height = actor.getHeight();

        Label label = new Label(labelText, mSkin);
        label.setSize(mLabelColumnWidth, height);

        item.setDefaultColumnWidth(mDefaultItemWidth - mLabelColumnWidth);

        AnchorGroup group = new AnchorGroup();
        group.setSize(mDefaultItemWidth, height);

        group.addPositionRule(label, Anchor.TOP_LEFT, group, Anchor.TOP_LEFT);
        group.addPositionRule(actor, Anchor.TOP_LEFT, label, Anchor.TOP_RIGHT);

        addItemInternal(item, group);
        return item;
    }

    private void addItemInternal(MenuItem item, Actor actor) {
        mItems.add(item);
        mActorForItem.put(item, actor);
        if (mCurrentIndex == -1) {
            mCurrentIndex = mItems.size - 1;
        }
        mContainer.addActor(actor);
        updateBounds();
    }

    private void updateBounds() {
        float width = Math.max(getWidth(), mContainer.getPrefWidth());
        float height = mContainer.getPrefHeight();

        mContainer.setSize(width, height);
        setBounds(getX(), getTop() - height, width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        mMenuInputHandler.act(delta);
        if (mMenuInputHandler.isPressed(VirtualKey.DOWN)) {
            goDown();
        } else if (mMenuInputHandler.isPressed(VirtualKey.UP)) {
            goUp();
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
        int index = getItemIndex(item);
        Assert.check(index != -1, "Invalid item");
        setCurrentIndex(index);
    }

    public MenuItem getCurrentItem() {
        return mCurrentIndex >= 0 ? mItems.get(mCurrentIndex) : null;
    }

    public boolean isItemVisible(MenuItem item) {
        Actor actor = mActorForItem.get(item);
        return actor.getParent() == mContainer;
    }

    public void setItemVisible(MenuItem item, boolean visible) {
        if (isItemVisible(item) == visible) {
            return;
        }
        int itemIndex = getItemIndex(item);
        Actor actor = mActorForItem.get(item);
        Assert.check(actor != null, "No actor for item");
        if (visible) {
            Actor previous = null;
            for (int idx = itemIndex - 1; idx >= 0; --idx) {
                MenuItem previousItem = mItems.get(idx);
                if (isItemVisible(previousItem)) {
                    previous = mActorForItem.get(previousItem);
                    break;
                }
            }
            mContainer.addActorAfter(previous, actor);
        } else {
            mContainer.removeActor(actor);
        }
        updateBounds();
    }

    public boolean goDown() {
        if (getCurrentItem().goDown()) {
            return true;
        }
        return adjustIndex(1);
    }

    public boolean goUp() {
        if (getCurrentItem().goUp()) {
            return true;
        }
        return adjustIndex(-1);
    }

    private boolean adjustIndex(int delta) {
        int size = mItems.size;
        for (int idx = getItemIndex(getCurrentItem()) + delta; idx >= 0 && idx < size; idx += delta) {
            MenuItem item = mItems.get(idx);
            if (item.isFocusable() && isItemVisible(item)) {
                setCurrentIndex(idx);
                return true;
            }
        }
        return false;
    }

    private void triggerCurrentItem() {
        MenuItem item = getCurrentItem();
        if (item == null) {
            return;
        }
        Assert.check(isItemVisible(item), "Cannot trigger an invisible item");
        item.trigger();
    }

    private void setCurrentIndex(int index) {
        int old = mCurrentIndex;
        mCurrentIndex = index;
        if (mCurrentIndex >= 0) {
            Assert.check(isItemVisible(getCurrentItem()), "Cannot set an invisible item current");
        }
        if (old >= 0 && mCurrentIndex == -1) {
            mFocusIndicator.addAction(Actions.fadeOut(SELECTION_ANIMATION_DURATION));
        } else if (old == -1) {
            updateFocusIndicatorBounds(FocusIndicatorMovement.IMMEDIATE);
        } else {
            updateFocusIndicatorBounds(FocusIndicatorMovement.ANIMATED);
        }
    }

    void animateFocusIndicator() {
        updateFocusIndicatorBounds(FocusIndicatorMovement.ANIMATED);
    }

    private void updateFocusIndicatorBounds(FocusIndicatorMovement movement) {
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

    private void mapDescendantRectangle(Actor actor, Rectangle rect) {
        mTmp.set(rect.x, rect.y);
        mTmp = actor.localToAscendantCoordinates(mContainer, mTmp);
        rect.x = mTmp.x;
        rect.y = mTmp.y;
    }

    /**
     * Returns the item at x, y (relative to mContainer), if any
     */
    private Rectangle mActorRectangle = new Rectangle();
    private MenuItem getItemAt(float x, float y) {
        for (MenuItem item : mItems) {
            if (!isItemVisible(item)) {
                continue;
            }
            Actor actor = item.getActor();
            // We do not use the item focus rect because it might be only represent a part of the item
            // For example the focus rect of a GridMenuItem is the currently selected cell of the grid
            mActorRectangle.set(0, 0, actor.getWidth(), actor.getHeight());
            mapDescendantRectangle(actor, mActorRectangle);
            if (mActorRectangle.contains(x, y)) {
                return item;
            }
        }
        return null;
    }

    private int getItemIndex(MenuItem item) {
        return mItems.indexOf(item, /* identity= */ true);
    }
}
