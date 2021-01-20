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

import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/** An item to create tabbed content in a menu */
public class TabMenuItem extends Actor implements MenuItem {
    private static final float HANDLE_SPEED = 5;
    private final FocusIndicator mFocusIndicator;
    private final Menu mMenu;
    private final GlyphLayout mGlyphLayout = new GlyphLayout();

    private static class Page {
        final String name;
        final MenuItemGroup group;
        final float tabWidth;

        private Page(String name, MenuItemGroup group, float tabWidth) {
            this.name = name;
            this.group = group;
            this.tabWidth = tabWidth;
        }
    }

    private final Array<Page> mPages = new Array<>();
    private final Rectangle mFocusRectangle = new Rectangle();

    private final BitmapFont mFont;
    private final TabMenuItemStyle mStyle;

    private int mPreviousTab = -1;
    private int mCurrentTab = 0;
    private float mHandleAnimProgress = 0;

    public static class TabMenuItemStyle {
        Drawable frame;
        float framePadding; // space between tab borders and outer frame
        float tabPadding; // horizontal space between tab borders and text
        Drawable handle;
        Drawable leftTabBorder;
        Drawable rightTabBorder;
    }

    public TabMenuItem(Menu menu) {
        mMenu = menu;
        mFocusIndicator = new FocusIndicator(menu);
        mFont = menu.getSkin().get("default-font", BitmapFont.class);
        mStyle = menu.getSkin().get(TabMenuItemStyle.class);

        setTouchable(Touchable.enabled);

        addListener(new Menu.MouseMovedListener(menu, this));
        addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        float tabRight = mStyle.framePadding;
                        for (int idx = 0; idx < mPages.size; ++idx) {
                            tabRight += mPages.get(idx).tabWidth;
                            if (x < tabRight) {
                                setCurrentTab(idx);
                                return;
                            }
                        }
                    }
                });
    }

    public MenuItemGroup addPage(String name) {
        mGlyphLayout.setText(mFont, name);
        float tabWidth = mGlyphLayout.width + mStyle.tabPadding * 2;

        MenuItemGroup group = new MenuItemGroup(mMenu);
        mMenu.addItem(group);

        mPages.add(new Page(name, group, tabWidth));
        if (mPages.size > 1) {
            mMenu.setItemVisible(group, false);
        }

        float width = 0;
        for (Page page : mPages) {
            width += page.tabWidth;
        }
        setSize(width + mStyle.framePadding * 2, mStyle.frame.getMinHeight());
        return group;
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public void setFocused(boolean focused) {
        mFocusIndicator.setFocused(focused);
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
    public void goLeft() {
        setCurrentTab(mCurrentTab - 1);
    }

    @Override
    public void goRight() {
        setCurrentTab(mCurrentTab + 1);
    }

    @Override
    public Rectangle getFocusRectangle() {
        mFocusRectangle.x = 0;
        mFocusRectangle.y = 0;
        mFocusRectangle.width = getWidth();
        mFocusRectangle.height = getHeight();
        float focusPadding = mMenu.getMenuStyle().focusPadding;
        AgcMathUtils.adjustRectangle(mFocusRectangle, -2 * focusPadding);
        return mFocusRectangle;
    }

    @Override
    public float getParentWidthRatio() {
        return 0;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        mFocusIndicator.act(delta);

        if (mPreviousTab != mCurrentTab) {
            if (mPreviousTab == -1) {
                // Start up
                mPreviousTab = mCurrentTab;
            } else {
                mHandleAnimProgress += delta * HANDLE_SPEED;
                if (mHandleAnimProgress > 1) {
                    // Animation is done
                    mHandleAnimProgress = 0;
                    mPreviousTab = mCurrentTab;
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (mPages.size == 0) {
            return;
        }

        mFocusIndicator.draw(batch, getX(), getY(), getWidth(), getHeight());
        drawFrame(batch);
        drawHandle(batch);
        drawText(batch);
    }

    private void drawFrame(Batch batch) {
        float distance = getDistanceToLeftEdge();
        drawFrameBorder(batch, mStyle.leftTabBorder, getX() - distance, distance);
        drawFrameBorder(
                batch, mStyle.rightTabBorder, getRight(), getStage().getWidth() - getRight());
        mStyle.frame.draw(batch, getX(), getY(), getWidth(), getHeight());
    }

    private float getDistanceToLeftEdge() {
        float x = 0;
        for (Actor actor = this; actor != null; actor = actor.getParent()) {
            x += actor.getX();
        }
        return x;
    }

    private void drawFrameBorder(Batch batch, Drawable drawable, float x, float width) {
        drawable.draw(batch, x, getY(), width, drawable.getMinHeight());
    }

    private float getTabX(int tab) {
        float x = 0;
        for (int idx = 0; idx < tab; ++idx) {
            x += mPages.get(idx).tabWidth;
        }
        return x;
    }

    private void drawHandle(Batch batch) {
        float framePadding = mStyle.framePadding;
        float x;
        float width;
        if (mCurrentTab == mPreviousTab) {
            x = getTabX(mCurrentTab);
            width = mPages.get(mCurrentTab).tabWidth;
        } else {
            float srcX = getTabX(mPreviousTab);
            float dstX = getTabX(mCurrentTab);
            float srcWidth = mPages.get(mPreviousTab).tabWidth;
            float dstWidth = mPages.get(mCurrentTab).tabWidth;
            x = MathUtils.lerp(srcX, dstX, mHandleAnimProgress);
            width = MathUtils.lerp(srcWidth, dstWidth, mHandleAnimProgress);
        }
        mStyle.handle.draw(
                batch,
                getX() + framePadding + x,
                getY() + framePadding,
                width,
                getHeight() - 2 * framePadding);
    }

    private void drawText(Batch batch) {
        float x = mStyle.framePadding;
        float y = getY() + (mFont.getCapHeight() + getHeight()) / 2;
        for (int idx = 0; idx < mPages.size; ++idx) {
            String name = mPages.get(idx).name;
            float tabWidth = mPages.get(idx).tabWidth;
            mFont.draw(batch, name, getX() + x, y, tabWidth, Align.center, /* wrap= */ false);
            x += tabWidth;
        }
    }

    private void setCurrentTab(int currentTab) {
        mCurrentTab = MathUtils.clamp(currentTab, 0, mPages.size - 1);
        for (int idx = 0; idx < mPages.size; ++idx) {
            MenuItemGroup page = mPages.get(idx).group;
            mMenu.setItemVisible(page, idx == mCurrentTab);
        }
    }
}
