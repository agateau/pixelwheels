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

import com.agateau.utils.Assert;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;

public class MenuItemGroup implements MenuItem {
    private final Menu mMenu;
    private final WidgetGroup mGroup =
            new WidgetGroup() {
                @Override
                public void layout() {
                    layoutItems();
                }
            };

    private static class ItemInfo {
        Label label = null;
        boolean visible = true;
    }

    private final Array<MenuItem> mItems = new Array<>();
    private final HashMap<Actor, MenuItem> mItemForActor = new HashMap<>();
    private final HashMap<MenuItem, ItemInfo> mInfoForItem = new HashMap<>();

    private int mCurrentIndex = -1;
    private float mWidth = -1;

    private enum SetCurrentHint {
        NONE,
        FROM_TOP,
        FROM_BOTTOM
    }

    public MenuItemGroup(Menu menu) {
        mMenu = menu;
        mGroup.addListener(
                new InputListener() {
                    public boolean touchDown(
                            InputEvent event, float x, float y, int pointer, int button) {
                        MenuItem item = getItemAt(x, y);
                        if (item != null && item.isFocusable()) {
                            setCurrentItem(item);
                        }
                        return false;
                    }

                    @Override
                    public void enter(
                            InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        menu.setCurrentItem(MenuItemGroup.this);
                    }
                });
    }

    public void setWidth(float width) {
        mWidth = width;
        mGroup.setWidth(width);
    }

    public void focusFirstItem() {
        for (MenuItem item : mItems) {
            if (item.isFocusable()) {
                setCurrentItem(item);
                return;
            }
        }
    }

    public void updateFocusIndicatorBounds() {
        getCurrentItem().setFocused(true);
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
    public void setFocused(boolean focused) {
        if (focused) {
            adjustIndex(-1, 1);
        } else {
            setCurrentIndex(-1);
        }
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
        if (getCurrentItem() != null && getCurrentItem().goDown()) {
            return true;
        }
        return adjustIndex(mCurrentIndex, 1);
    }

    @Override
    public boolean goUp() {
        if (getCurrentItem() != null && getCurrentItem().goUp()) {
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
        Actor actor = item.getActor();
        mFocusRect.x += actor.getX();
        mFocusRect.y += actor.getY();
        return mFocusRect;
    }

    @Override
    public float getParentWidthRatio() {
        return mWidth == -1 ? 1 : 0;
    }

    public MenuItem getCurrentItem() {
        return mCurrentIndex >= 0 ? mItems.get(mCurrentIndex) : null;
    }

    public void setCurrentItem(MenuItem item) {
        if (item == null) {
            setCurrentIndex(-1);
            return;
        }
        if (getCurrentItem() instanceof MenuItemGroup) {
            ((MenuItemGroup) getCurrentItem()).setCurrentItem(item);
        }
        int index = getItemIndex(item);
        if (index == -1) {
            return;
        }
        setCurrentIndex(index);
    }

    public boolean isItemVisible(MenuItem item) {
        ItemInfo info = mInfoForItem.get(item);
        Assert.check(info != null, "No info for item");
        return info.visible;
    }

    public void setItemVisible(MenuItem item, boolean visible) {
        if (isItemVisible(item) == visible) {
            return;
        }
        ItemInfo info = mInfoForItem.get(item);
        info.visible = visible;
        item.getActor().setVisible(visible);
        if (info.label != null) {
            info.label.setVisible(visible);
        }
        updateHeight();
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
        addItemInternal(item, null);
        return item;
    }

    public MenuItem addItemWithLabel(String labelText, MenuItem item) {
        Actor actor = item.getActor();
        float height = actor.getHeight();

        float labelWidth = mMenu.getLabelColumnWidth();

        Label label = new Label(labelText, mMenu.getSkin());
        label.setSize(labelWidth, height);

        addItemInternal(item, label);
        return item;
    }

    private boolean adjustIndex(int startIndex, int delta) {
        int size = mItems.size;
        for (int idx = startIndex + delta; idx >= 0 && idx < size; idx += delta) {
            MenuItem item = mItems.get(idx);
            if (item.isFocusable() && isItemVisible(item)) {
                setCurrentIndex(
                        idx, delta > 0 ? SetCurrentHint.FROM_TOP : SetCurrentHint.FROM_BOTTOM);
                return true;
            }
        }
        return false;
    }

    private void layoutItems() {
        // Keep in sync with computeHeight()!
        float y = 0;
        Menu.MenuStyle style = mMenu.getMenuStyle();
        final float spacing = style.focusPadding * 2 + style.spacing;
        for (int idx = mItems.size - 1; idx >= 0; --idx) {
            MenuItem item = mItems.get(idx);
            ItemInfo info = mInfoForItem.get(item);
            if (!info.visible) {
                continue;
            }
            Actor actor = item.getActor();
            if (actor instanceof Layout) {
                ((Layout) actor).invalidate();
                ((Layout) actor).validate();
            }

            float x = 0;
            float width = mGroup.getWidth();
            if (info.label != null) {
                info.label.setPosition(0, y);
                x = mMenu.getLabelColumnWidth();
                width -= x;
            }

            float ratio = mItemForActor.get(actor).getParentWidthRatio();
            if (ratio > 0) {
                actor.setWidth(width * ratio);
            }

            if (info.label == null) {
                x += (width - actor.getWidth()) / 2;
            }
            actor.setPosition(x, y);
            y += actor.getHeight() + spacing;
        }
    }

    private float computeHeight() {
        // Keep in sync with layoutItems()!
        float y = 0;
        Menu.MenuStyle style = mMenu.getMenuStyle();
        final float spacing = style.focusPadding * 2 + style.spacing;
        for (int idx = mItems.size - 1; idx >= 0; --idx) {
            MenuItem item = mItems.get(idx);
            ItemInfo info = mInfoForItem.get(item);
            if (!info.visible) {
                continue;
            }
            Actor actor = item.getActor();
            y += actor.getHeight() + spacing;
        }
        return y - spacing;
    }

    private void addItemInternal(MenuItem item, Label label) {
        mItems.add(item);
        ItemInfo info = new ItemInfo();
        info.label = label;
        mInfoForItem.put(item, info);
        mItemForActor.put(item.getActor(), item);
        if (label != null) {
            mGroup.addActor(label);
        }
        mGroup.addActor(item.getActor());
        updateHeight();
    }

    private void updateHeight() {
        mGroup.setHeight(computeHeight());
        mMenu.onGroupBoundariesChanged();
    }

    private void setCurrentIndex(int index) {
        setCurrentIndex(index, SetCurrentHint.NONE);
    }

    private void setCurrentIndex(int index, SetCurrentHint hint) {
        if (mCurrentIndex == index) {
            return;
        }
        if (mCurrentIndex != -1) {
            MenuItem item = getCurrentItem();
            if (item.isFocusable()) {
                item.setFocused(false);
            }
        }
        mCurrentIndex = index;
        if (mCurrentIndex != -1) {
            MenuItem item = getCurrentItem();
            Assert.check(isItemVisible(item), "Cannot set an invisible item current");
            Assert.check(item.isFocusable(), "Item " + item + " is not focusable");
            item.setFocused(true);

            if (item instanceof MenuItemGroup) {
                MenuItemGroup group = (MenuItemGroup) item;
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
    }

    /** Returns the item at x, y (relative to mGroup), if any */
    private final Rectangle mActorRectangle = new Rectangle();

    private MenuItem getItemAt(float x, float y) {
        for (MenuItem item : mItems) {
            if (!isItemVisible(item)) {
                continue;
            }
            Actor actor = item.getActor();
            // We do not use the item focus rect because it might only represent a part of the item
            // For example the focus rect of a GridMenuItem is the currently selected cell of the
            // grid
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
