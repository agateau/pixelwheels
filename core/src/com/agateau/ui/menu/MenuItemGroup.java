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

import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.Assert;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class MenuItemGroup implements MenuItem {
    private final Menu mMenu;
    private final VerticalGroup mGroup = new VerticalGroup() {
        @Override
        public void layout() {
            super.layout();
            updateBounds();
            mMenu.updateFocusIndicatorBounds(Menu.FocusIndicatorMovement.IMMEDIATE);
        }
    };
    private final Array<MenuItem> mItems = new Array<MenuItem>();
    private final HashMap<MenuItem, Actor> mActorForItem = new HashMap<MenuItem, Actor>();

    private int mCurrentIndex = -1;

    private enum SetCurrentHint {
        NONE,
        FROM_TOP,
        FROM_BOTTOM
    }

    public MenuItemGroup(Menu menu) {
        mMenu = menu;
        mGroup.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                MenuItem item = getItemAt(x, y);
                if (item != null) {
                    setCurrentItem(item);
                }
                return false;
            }
        });
        Menu.MenuStyle style = mMenu.getMenuStyle();
        mGroup.space(style.focusPadding * 2 + style.spacing);
        mGroup.fill();
    }

    @Override
    public Actor getActor() {
        return mGroup;
    }

    @Override
    public boolean addListener(EventListener eventListener) {
        return mGroup.addListener(eventListener);
    }

    @Override
    public boolean isFocusable() {
        // TODO: return false if there are only non focusable items
        return true;
    }

    @Override
    public void trigger() {
        MenuItem item = getCurrentItem();
        if (item == null) {
            return;
        }
        Assert.check(isItemVisible(item), "Cannot trigger an invisible item");
        item.trigger();
    }

    @Override
    public boolean goDown() {
        if (getCurrentItem().goDown()) {
            return true;
        }
        return adjustIndex(mCurrentIndex, 1);
    }

    @Override
    public boolean goUp() {
        if (getCurrentItem().goUp()) {
            return true;
        }
        return adjustIndex(mCurrentIndex, -1);
    }

    @Override
    public void goLeft() {
        MenuItem item = getCurrentItem();
        if (item != null) {
            item.goLeft();
        }
    }

    @Override
    public void goRight() {
        MenuItem item = getCurrentItem();
        if (item != null) {
            item.goRight();
        }
    }

    private final Rectangle mFocusRect = new Rectangle();
    @Override
    public Rectangle getFocusRectangle() {
        MenuItem item = getCurrentItem();
        Assert.check(item != null, "Cannot get focus rectangle of an invalid item");
        Assert.check(item.isFocusable(), "Item " + item + " is not focusable");
        mFocusRect.set(item.getFocusRectangle());
        Actor actor = mActorForItem.get(item);
        mFocusRect.x += actor.getX();
        mFocusRect.y += actor.getY();
        if (actor != item.getActor()) {
            // Item has a label
            mFocusRect.x += item.getActor().getX();
        }
        return mFocusRect;
    }

    @Override
    public void setDefaultItemWidth(float width) {
        for (MenuItem item : mItems) {
            item.setDefaultItemWidth(width);
        }
    }

    public MenuItem getCurrentItem() {
        return mCurrentIndex >= 0 ? mItems.get(mCurrentIndex) : null;
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

    public boolean isItemVisible(MenuItem item) {
        Actor actor = mActorForItem.get(item);
        Assert.check(actor != null, "No actor for item");
        return actor.getParent() == mGroup;
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
            mGroup.addActorAfter(previous, actor);
        } else {
            mGroup.removeActor(actor);
        }
        updateBounds();
    }

    public MenuItem addButton(String text) {
        return addItem(new ButtonMenuItem(mMenu, text, mMenu.getSkin()));
    }

    public LabelMenuItem addLabel(String text) {
        LabelMenuItem labelMenuItem = new LabelMenuItem(text, mMenu.getSkin());
        addItem(labelMenuItem);
        return labelMenuItem;
    }

    public LabelMenuItem addTitleLabel(String text) {
        LabelMenuItem labelMenuItem = new LabelMenuItem(text, mMenu.getSkin(), "menuTitle");
        addItem(labelMenuItem);
        return labelMenuItem;
    }

    public MenuItem addItem(MenuItem item) {
        item.setDefaultItemWidth(mMenu.getDefaultItemWidth());
        addItemInternal(item, item.getActor());
        return item;
    }

    public MenuItem addItemWithLabel(String labelText, MenuItem item) {
        Actor actor = item.getActor();
        float height = actor.getHeight();

        float labelWidth = mMenu.getLabelColumnWidth();
        float itemWidth = mMenu.getDefaultItemWidth();

        Label label = new Label(labelText, mMenu.getSkin());
        label.setSize(labelWidth, height);

        item.setDefaultItemWidth(itemWidth - labelWidth);

        AnchorGroup group = new AnchorGroup();
        group.setSize(itemWidth, height);

        group.addPositionRule(label, Anchor.TOP_LEFT, group, Anchor.TOP_LEFT);
        group.addPositionRule(actor, Anchor.TOP_LEFT, label, Anchor.TOP_RIGHT);

        addItemInternal(item, group);
        return item;
    }

    private boolean adjustIndex(int startIndex, int delta) {
        int size = mItems.size;
        for (int idx = startIndex + delta; idx >= 0 && idx < size; idx += delta) {
            MenuItem item = mItems.get(idx);
            if (item.isFocusable() && isItemVisible(item)) {
                setCurrentIndex(idx, delta > 0 ? SetCurrentHint.FROM_TOP : SetCurrentHint.FROM_BOTTOM);
                return true;
            }
        }
        return false;
    }

    private void updateBounds() {
        float width = Math.max(mMenu.getWidth() - mMenu.getMenuStyle().focusPadding * 2,
                mGroup.getPrefWidth());
        float height = mGroup.getPrefHeight();

        mGroup.setSize(width, height);
        mMenu.onGroupBoundariesChanged();
    }

    private void addItemInternal(MenuItem item, Actor actor) {
        mItems.add(item);
        mActorForItem.put(item, actor);
        if (mCurrentIndex == -1 && item.isFocusable()) {
            mCurrentIndex = mItems.size - 1;
        }
        mGroup.addActor(actor);
        updateBounds();
    }

    private void setCurrentIndex(int index) {
        setCurrentIndex(index, SetCurrentHint.NONE);
    }

    private void setCurrentIndex(int index, SetCurrentHint hint) {
        int old = mCurrentIndex;
        mCurrentIndex = index;
        if (mCurrentIndex != -1) {
            MenuItem item = getCurrentItem();
            Assert.check(isItemVisible(item), "Cannot set an invisible item current");
            Assert.check(item.isFocusable(), "Item " + item + " is not focusable");

            if (item instanceof MenuItemGroup) {
                MenuItemGroup group = (MenuItemGroup)item;
                switch (hint) {
                    case NONE:
                        break;
                    case FROM_TOP:
                        group.adjustIndex(-1, 1);
                        break;
                    case FROM_BOTTOM:
                        group.adjustIndex(group.mItems.size, -1);
                        break;
                }
            }
        }
        if (old >= 0 && mCurrentIndex == -1) {
            mMenu.hideFocusIndicator();
        } else if (old == -1) {
            mMenu.updateFocusIndicatorBounds(Menu.FocusIndicatorMovement.IMMEDIATE);
        } else {
            mMenu.updateFocusIndicatorBounds(Menu.FocusIndicatorMovement.ANIMATED);
        }
    }

    /**
     * Returns the item at x, y (relative to mGroup), if any
     */
    private Rectangle mActorRectangle = new Rectangle();
    private MenuItem getItemAt(float x, float y) {
        for (MenuItem item : mItems) {
            if (!isItemVisible(item)) {
                continue;
            }
            Actor actor = item.getActor();
            // We do not use the item focus rect because it might only represent a part of the item
            // For example the focus rect of a GridMenuItem is the currently selected cell of the grid
            mActorRectangle.set(0, 0, actor.getWidth(), actor.getHeight());
            for (; actor != mGroup && actor != null; actor = actor.getParent()) {
                mActorRectangle.x += actor.getX();
                mActorRectangle.y += actor.getY();
            }
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
