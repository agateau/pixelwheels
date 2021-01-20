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
import com.agateau.ui.MouseCursorManager;
import com.agateau.ui.VirtualKey;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

/**
 * A keyboard and game controller friendly menu system
 *
 * <p>Sends ChangeEvent when the current item changes.
 */
public class Menu extends WidgetGroup {
    private static final float LABEL_COLUMN_WIDTH = 120;
    private final MenuInputHandler mMenuInputHandler = new MenuInputHandler();
    private final MenuItemGroup mGroup;
    private final Skin mSkin;
    private final MenuStyle mStyle;

    private float mLabelColumnWidth;

    /** A listener to set the item current if the mouse cursor is visible and over it */
    static class MouseMovedListener extends ClickListener {
        private final Menu mMenu;
        private final MenuItem mMenuItem;

        public MouseMovedListener(Menu menu, MenuItem menuItem) {
            mMenu = menu;
            mMenuItem = menuItem;
        }

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            if (MouseCursorManager.getInstance().isVisible()) {
                mMenu.setCurrentItem(mMenuItem);
            }
            return true;
        }
    }

    public static class MenuStyle {
        public Drawable focus;
        public int spacing;
        public int focusPadding;

        public MenuStyle() {}
    }

    public Menu(Skin skin) {
        this(skin, "default");
    }

    public Menu(Skin skin, String styleName) {
        mSkin = skin;
        mStyle = skin.get(styleName, MenuStyle.class);

        mGroup = new MenuItemGroup(this);
        setLabelColumnWidth(LABEL_COLUMN_WIDTH);

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

    public float getLabelColumnWidth() {
        return mLabelColumnWidth;
    }

    public void setLabelColumnWidth(float labelColumnWidth) {
        mLabelColumnWidth = labelColumnWidth;
    }

    public MenuItem addButton(String text) {
        return mGroup.addButton(text);
    }

    /**
     * Add a plain label in the menu
     *
     * @return The created label
     */
    @SuppressWarnings("UnusedReturnValue")
    public LabelMenuItem addLabel(String text) {
        return mGroup.addLabel(text);
    }

    /**
     * Add a "title" label in the menu (uses the "menuTitle" label style)
     *
     * @return The created label
     */
    @SuppressWarnings("UnusedReturnValue")
    public LabelMenuItem addTitleLabel(String text) {
        return mGroup.addTitleLabel(text);
    }

    /** Add a full-width item */
    @SuppressWarnings("UnusedReturnValue")
    public MenuItem addItem(MenuItem item) {
        return mGroup.addItem(item);
    }

    /** Add a [label - item] row */
    public MenuItem addItemWithLabel(String labelText, MenuItem item) {
        return mGroup.addItemWithLabel(labelText, item);
    }

    private boolean mFirstLayout = true;

    @Override
    public void layout() {
        super.layout();

        updateGroupBounds();

        if (mFirstLayout) {
            mFirstLayout = false;
            onFirstLayout();
        }
    }

    private void updateGroupBounds() {
        Actor actor = mGroup.getActor();
        actor.setWidth(getWidth() - 2 * mStyle.focusPadding);
        ((Layout) actor).invalidate();
        ((Layout) actor).validate();
        actor.setPosition(mStyle.focusPadding, mStyle.focusPadding);
    }

    private void onFirstLayout() {
        if (mGroup.getCurrentItem() == null) {
            mGroup.focusFirstItem();
        } else {
            mGroup.updateFocusIndicatorBounds();
        }
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
            MenuItem item = getCurrentItem();
            if (item != null) {
                item.goLeft();
            }
        } else if (mMenuInputHandler.isPressed(VirtualKey.RIGHT)) {
            MenuItem item = getCurrentItem();
            if (item != null) {
                item.goRight();
            }
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

    void onGroupBoundariesChanged() {
        Actor actor = mGroup.getActor();
        actor.setPosition(mStyle.focusPadding, mStyle.focusPadding);
        setSize(getWidth(), actor.getHeight() + 2 * mStyle.focusPadding);
        invalidateHierarchy();
    }
}
