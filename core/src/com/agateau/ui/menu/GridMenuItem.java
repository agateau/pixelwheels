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

import com.agateau.pixelwheels.utils.DrawUtils;
import com.agateau.ui.InputMapper;
import com.agateau.ui.MouseCursorManager;
import com.agateau.ui.VirtualKey;
import com.agateau.utils.Assert;
import com.agateau.utils.PlatformUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;

/** A MenuItem to display a grid of custom elements */
public class GridMenuItem<T> extends Widget implements MenuItem {
    public static final int INVALID_INDEX = -1;
    private final Menu mMenu;
    private Array<T> mItems;
    private ItemRenderer<T> mRenderer;

    private int mColumnCount = 3;
    private float mItemWidth = 0;
    private float mItemHeight = 0;
    private TouchUiConfirmMode mTouchUiConfirmMode = TouchUiConfirmMode.DOUBLE_TOUCH;
    private ItemDirection mItemDirection = ItemDirection.LeftToRight;

    /**
     * Represents a cursor: the selection in the grid.
     *
     * <p>This is in a separate class because it is possible to have multiple cursors on a single
     * grid, each controlled by a separate input
     */
    private class Cursor {
        private static final int CURSOR_EXTRA_PADDING = 4;
        public final Rectangle mFocusRectangle = new Rectangle();
        public final Array<FocusIndicator> mFocusIndicators = new Array<>();
        private final int mRank;
        private Menu.MenuStyle mMenuStyle;
        public int mSelectedIndex = INVALID_INDEX;
        public int mCurrentIndex = 0;
        public SelectionListener<T> mSelectionListener;
        public MenuItemListener mMenuItemListener;
        private MenuInputHandler mInputHandler;

        public Cursor(int rank) {
            mMenuStyle = GridMenuItem.this.mMenu.getMenuStyle();
            mRank = rank;
        }

        public void setInputMapper(InputMapper inputMapper) {
            if (inputMapper == null) {
                mInputHandler = null;
            } else {
                mInputHandler = new MenuInputHandler();
                mInputHandler.setInputMapper(inputMapper);
            }
        }

        public void setMenuStyle(Menu.MenuStyle menuStyle) {
            mMenuStyle = menuStyle;
            mFocusIndicators.clear();
            createFocusIndicators();
        }

        private void createFocusIndicators() {
            if (mItems == null) {
                return;
            }
            while (mFocusIndicators.size < mItems.size) {
                FocusIndicator indicator =
                        new FocusIndicator(mMenuStyle, mRank * CURSOR_EXTRA_PADDING);
                mFocusIndicators.add(indicator);
            }
        }

        public void act(float delta) {
            for (FocusIndicator focusIndicator : mFocusIndicators) {
                focusIndicator.act(delta);
            }
            if (mInputHandler == null) {
                return;
            }
            mInputHandler.act(delta);
            if (mInputHandler.isPressed(VirtualKey.LEFT)) {
                goLeft();
            }
            if (mInputHandler.isPressed(VirtualKey.RIGHT)) {
                goRight();
            }
            if (mInputHandler.isPressed(VirtualKey.UP)) {
                goUp();
            }
            if (mInputHandler.isPressed(VirtualKey.DOWN)) {
                goDown();
            }
            if (mInputHandler.isPressed(VirtualKey.TRIGGER)) {
                trigger();
            }
        }

        public void goLeft() {
            if (mItemDirection == ItemDirection.LeftToRight) {
                if (mCurrentIndex > 0) {
                    setCurrentIndex(mCurrentIndex - 1);
                }
            } else {
                int rowCount = getRowCount();
                if (mCurrentIndex - rowCount >= 0) {
                    setCurrentIndex(mCurrentIndex - rowCount);
                }
            }
        }

        public void goRight() {
            if (mItemDirection == ItemDirection.LeftToRight) {
                if (mCurrentIndex < mItems.size - 1) {
                    setCurrentIndex(mCurrentIndex + 1);
                }
            } else {
                int rowCount = getRowCount();
                if (mCurrentIndex + rowCount < mItems.size) {
                    setCurrentIndex(mCurrentIndex + rowCount);
                }
            }
        }

        public boolean goUp() {
            if (mItemDirection == ItemDirection.LeftToRight) {
                if (mCurrentIndex - mColumnCount >= 0) {
                    setCurrentIndex(mCurrentIndex - mColumnCount);
                    return true;
                } else {
                    return false;
                }
            } else {
                if (mCurrentIndex > 0) {
                    setCurrentIndex(mCurrentIndex - 1);
                    return true;
                } else {
                    return false;
                }
            }
        }

        public boolean goDown() {
            if (mItemDirection == ItemDirection.LeftToRight) {
                if (mCurrentIndex + mColumnCount < mItems.size) {
                    setCurrentIndex(mCurrentIndex + mColumnCount);
                    return true;
                } else {
                    return false;
                }
            } else {
                if (mCurrentIndex < mItems.size - 1) {
                    setCurrentIndex(mCurrentIndex + 1);
                    return true;
                } else {
                    return false;
                }
            }
        }

        public void setCurrentIndex(int currentIndex) {
            if (mCurrentIndex != INVALID_INDEX) {
                mFocusIndicators.get(mCurrentIndex).setFocused(false);
            }
            mCurrentIndex = currentIndex;
            if (mCurrentIndex != INVALID_INDEX) {
                mFocusIndicators.get(mCurrentIndex).setFocused(true);
            }
            if (mSelectionListener != null) {
                T item = currentIndex >= 0 ? mItems.get(currentIndex) : null;
                mSelectionListener.currentChanged(item, currentIndex);
            }
        }

        public void trigger() {
            setSelectedIndex(mCurrentIndex);
            if (mMenuItemListener == null) {
                MenuItemTriggerEvent.fire(GridMenuItem.this);
            } else {
                mMenuItemListener.triggered();
            }
        }

        /**
         * Selects @p index
         *
         * <p>On non touch screen UI, selecting an index confirms the selection.
         *
         * <p>On touch screen UI, user must select the same index to confirm the selection (because
         * there is no mouse-over, so there is no way to make an item current without clicking on
         * it).
         */
        public void setSelectedIndex(int index) {
            if (index < 0) {
                mSelectedIndex = INVALID_INDEX;
                return;
            }
            Assert.check(index < mItems.size, "Invalid index value");
            T item = mItems.get(index);
            if (!mRenderer.isItemEnabled(item)) {
                mSelectedIndex = INVALID_INDEX;
                return;
            }
            int oldIndex = mSelectedIndex;
            mSelectedIndex = index;
            if (mSelectionListener != null) {
                if (PlatformUtils.isTouchUi()) {
                    if (mTouchUiConfirmMode == TouchUiConfirmMode.SINGLE_TOUCH
                            || oldIndex == mSelectedIndex) {
                        mSelectionListener.selectionConfirmed();
                    }
                } else {
                    mSelectionListener.selectionConfirmed();
                }
            }
            setCurrentIndex(index);
        }
    }

    private final Array<Cursor> mCursors = new Array<>();

    public enum ItemDirection {
        LeftToRight,
        TopToBottom
    }

    public enum TouchUiConfirmMode {
        SINGLE_TOUCH,
        DOUBLE_TOUCH
    }

    public interface ItemRenderer<T> {
        /** Returns a rectangle relative to the bottom-left corner of the grid cell */
        Rectangle getItemRectangle(float width, float height, T item);

        void render(Batch batch, float x, float y, float width, float height, T item);

        boolean isItemEnabled(T item);
    }

    public interface SelectionListener<T> {
        void currentChanged(T item, int index);

        void selectionConfirmed();
    }

    public GridMenuItem(Menu menu) {
        mMenu = menu;
        mCursors.add(new Cursor(0));
        addListener(
                new InputListener() {
                    public boolean touchDown(
                            InputEvent event, float x, float y, int pointer, int button) {
                        if (pointer == 0 && button != 0) {
                            return false;
                        }
                        int idx = getIndexAt(x, y);
                        if (idx >= 0) {
                            mCursors.first().setCurrentIndex(idx);
                            trigger();
                        }
                        return true;
                    }

                    @Override
                    public boolean mouseMoved(InputEvent event, float x, float y) {
                        if (!MouseCursorManager.getInstance().isVisible()) {
                            return true;
                        }
                        int idx = getIndexAt(x, y);
                        if (idx >= 0) {
                            mCursors.first().setCurrentIndex(idx);
                        }
                        return true;
                    }
                });
    }

    public void addCursor() {
        mCursors.add(new Cursor(mCursors.size));
    }

    public void setListener(int idx, MenuItemListener listener) {
        mCursors.get(idx).mMenuItemListener = listener;
    }

    public void setInputMapper(int idx, InputMapper inputMapper) {
        mCursors.get(idx).setInputMapper(inputMapper);
    }

    public void setMenuStyle(int idx, Menu.MenuStyle menuStyle) {
        mCursors.get(idx).setMenuStyle(menuStyle);
    }

    public TouchUiConfirmMode getTouchUiConfirmMode() {
        return mTouchUiConfirmMode;
    }

    public void setTouchUiConfirmMode(TouchUiConfirmMode touchUiConfirmMode) {
        mTouchUiConfirmMode = touchUiConfirmMode;
    }

    public void setItemDirection(ItemDirection itemDirection) {
        mItemDirection = itemDirection;
    }

    public void setSelectionListener(SelectionListener<T> selectionListener) {
        setSelectionListener(0, selectionListener);
    }

    public void setSelectionListener(int idx, SelectionListener<T> selectionListener) {
        mCursors.get(idx).mSelectionListener = selectionListener;
    }

    public void setCurrent(int cursorIdx, T item) {
        Cursor cursor = mCursors.get(cursorIdx);
        if (item == null) {
            cursor.setCurrentIndex(0);
            return;
        }
        int index = mItems.indexOf(item, true);
        if (index < 0) {
            NLog.e("Item is not in the item list");
            return;
        }
        cursor.setCurrentIndex(index);
        if (PlatformUtils.isTouchUi()) {
            cursor.setSelectedIndex(index);
        }
    }

    public void setCurrent(T item) {
        setCurrent(0, item);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCurrentItemEnabled() {
        T item = getCurrent();
        if (item == null) {
            return false;
        }
        return mRenderer.isItemEnabled(item);
    }

    public T getSelected(int idx) {
        Cursor cursor = mCursors.get(idx);
        return cursor.mSelectedIndex >= 0 ? mItems.get(cursor.mSelectedIndex) : null;
    }

    public T getSelected() {
        return getSelected(0);
    }

    public T getCurrent() {
        Cursor cursor = mCursors.first();
        return cursor.mCurrentIndex >= 0 ? mItems.get(cursor.mCurrentIndex) : null;
    }

    public void setItems(Array<T> items) {
        mItems = items;
        for (Cursor cursor : mCursors) {
            cursor.createFocusIndicators();
            int currentIndex = items.size > 0 ? 0 : INVALID_INDEX;
            cursor.setCurrentIndex(currentIndex);
        }
        updateHeight();
    }

    public Array<T> getItems() {
        return mItems;
    }

    public void setItemRenderer(ItemRenderer<T> renderer) {
        mRenderer = renderer;
    }

    public void setItemSize(float width, float height) {
        mItemWidth = width;
        mItemHeight = height;
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    public void setColumnCount(int columnCount) {
        mColumnCount = columnCount;
        updateHeight();
    }

    private void updateHeight() {
        float height = getPrefHeight();
        if (MathUtils.isEqual(height, getHeight(), 1)) {
            return;
        }
        setHeight(height);
        invalidateHierarchy();
    }

    /// Scene2d API
    @Override
    public float getPrefWidth() {
        return mItemWidth * mColumnCount;
    }

    @Override
    public float getPrefHeight() {
        if (mItems == null || mColumnCount == 0) {
            return 0;
        }
        int rowCount = MathUtils.ceil(mItems.size / (float) mColumnCount);
        return mItemHeight * rowCount;
    }

    @Override
    protected void sizeChanged() {
        updateHeight();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (Cursor cursor : mCursors) {
            cursor.act(delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (mRenderer == null) {
            NLog.e("No renderer");
            return;
        }
        if (mItemWidth <= 0 || mItemHeight <= 0) {
            NLog.e("Invalid item size");
            return;
        }

        DrawUtils.multiplyBatchAlphaBy(batch, parentAlpha);

        float itemSpacing = getItemSpacing();
        float x = 0;
        float y = getHeight() - mItemHeight;

        for (int idx = 0; idx < mItems.size; idx++) {
            T item = mItems.get(idx);
            Rectangle rect = mRenderer.getItemRectangle(mItemWidth, mItemHeight, item);

            for (Cursor cursor : mCursors) {
                FocusIndicator focusIndicator = cursor.mFocusIndicators.get(idx);
                focusIndicator.draw(
                        batch, getX() + x + rect.x, getY() + y + rect.y, rect.width, rect.height);

                if (idx == cursor.mSelectedIndex) {
                    Menu.MenuStyle style = cursor.mMenuStyle;
                    DrawUtils.drawPixelAligned(
                            batch,
                            style.selected,
                            getX() + x + rect.x,
                            getY() + y + rect.y,
                            rect.width,
                            rect.height,
                            style.focusPadding);
                }
            }
            mRenderer.render(batch, getX() + x, getY() + y, mItemWidth, mItemHeight, item);

            if (mItemDirection == ItemDirection.LeftToRight) {
                if ((idx + 1) % mColumnCount == 0) {
                    // New row
                    x = 0;
                    y -= mItemHeight;
                } else {
                    x += mItemWidth + itemSpacing;
                }
            } else {
                if (y - mItemHeight < 0) {
                    // New column
                    x += mItemWidth + itemSpacing;
                    y = getHeight() - mItemHeight;
                } else {
                    y -= mItemHeight;
                }
            }
        }
    }

    /// MenuItem API
    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public void trigger() {
        Cursor cursor = mCursors.first();
        cursor.trigger();
    }

    @Override
    public boolean goUp() {
        Cursor cursor = mCursors.first();
        return cursor.goUp();
    }

    @Override
    public boolean goDown() {
        Cursor cursor = mCursors.first();
        return cursor.goDown();
    }

    @Override
    public void goLeft() {
        Cursor cursor = mCursors.first();
        cursor.goLeft();
    }

    @Override
    public void goRight() {
        Cursor cursor = mCursors.first();
        cursor.goRight();
    }

    @Override
    public Rectangle getFocusRectangle() {
        Cursor cursor = mCursors.first();
        if (cursor.mCurrentIndex == INVALID_INDEX) {
            cursor.mFocusRectangle.set(0, 0, -1, -1);
            return cursor.mFocusRectangle;
        }
        T item = mItems.get(cursor.mCurrentIndex);
        float x = (cursor.mCurrentIndex % mColumnCount) * (mItemWidth + getItemSpacing());
        float y = getHeight() - (cursor.mCurrentIndex / mColumnCount + 1) * mItemHeight;
        Rectangle rect = mRenderer.getItemRectangle(mItemWidth, mItemHeight, item);
        cursor.mFocusRectangle.set(x + rect.x, y + rect.y, rect.width, rect.height);
        return cursor.mFocusRectangle;
    }

    @Override
    public float getParentWidthRatio() {
        return 1;
    }

    @Override
    public void setFocused(boolean focused) {
        Cursor cursor = mCursors.first();
        if (cursor.mCurrentIndex == INVALID_INDEX) {
            return;
        }
        cursor.mFocusIndicators.get(cursor.mCurrentIndex).setFocused(focused);
    }

    /// Private
    /** Horizontal spacing between items */
    private float getItemSpacing() {
        return mColumnCount > 1 ? (getWidth() - mItemWidth * mColumnCount) / (mColumnCount - 1) : 0;
    }

    private int getIndexAt(float touchX, float touchY) {
        if (mItems.size == 0) {
            return INVALID_INDEX;
        }
        if (mItemWidth <= 0 || mItemHeight <= 0) {
            NLog.e("Invalid item size");
            return INVALID_INDEX;
        }
        float gridWidth = mItemWidth + getItemSpacing();
        int row = MathUtils.floor((getHeight() - touchY) / mItemHeight);
        int column = MathUtils.floor(touchX / gridWidth);

        float itemX = column * gridWidth;
        if (itemX + mItemWidth < touchX) {
            // Clicked between columns
            return INVALID_INDEX;
        }

        int idx;
        if (mItemDirection == ItemDirection.LeftToRight) {
            idx = row * mColumnCount + column;
        } else {
            idx = row + column * getRowCount();
        }

        if (idx >= 0 && idx < mItems.size) {
            return idx;
        } else {
            return INVALID_INDEX;
        }
    }

    private int getRowCount() {
        return (int) (getHeight() / mItemHeight);
    }
}
