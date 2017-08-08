package com.agateau.ui;

import com.agateau.utils.Assert;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

/**
 * A MenuItem to display a grid of custom elements
 */

public class GridMenuItem<T> extends Widget implements MenuItem {
    private final Menu mMenu;
    private final Rectangle mFocusRectangle = new Rectangle();
    private GridMenuItemStyle mStyle;
    private Array<T> mItems;
    private int mSelectedIndex = -1;
    private int mCurrentIndex = -1;
    private ItemRenderer<T> mRenderer;

    private int mColumnCount = 3;
    private float mItemWidth = 0;
    private float mItemHeight = 0;

    public interface ItemRenderer<T> {
        /**
         * Returns a rectangle relative to the bottom-left corner of the grid
         */
        Rectangle getItemRectangle(float width, float height, T item);

        void render(Batch batch, float x, float y, float width, float height, T item);
    }

    public static class GridMenuItemStyle {
        public Drawable selected;
    }

    public GridMenuItem(Menu menu) {
        mMenu = menu;
        mStyle = mMenu.getSkin().get(GridMenuItemStyle.class);
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == 0 && button != 0) {
                    return false;
                }
                GridMenuItem.this.touchDown(x, y);
                return true;
            }
        });
    }

    public void setSelected(T item) {
        if (item == null) {
            setSelectedIndex(-1);
            return;
        }
        int index = mItems.indexOf(item, true);
        if (index < 0) {
            NLog.e("Item is not in the item list");
            return;
        }
        setSelectedIndex(index);
    }

    public void setCurrent(T item) {
        if (item == null) {
            mCurrentIndex = -1;
            return;
        }
        int index = mItems.indexOf(item, true);
        if (index < 0) {
            NLog.e("Item is not in the item list");
            return;
        }
        mCurrentIndex = index;
    }

    private void setSelectedIndex(int index) {
        if (index < 0) {
            mSelectedIndex = -1;
            return;
        }
        Assert.check(index < mItems.size, "Invalid index value");
        mSelectedIndex = index;
        mCurrentIndex = index;
    }

    public T getSelected() {
        return mSelectedIndex >= 0 ? mItems.get(mSelectedIndex) : null;
    }

    public void setItems(Array<T> items) {
        mItems = items;
        mCurrentIndex = items.size > 0 ? 0 : -1;
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
        invalidateHierarchy();
    }

    /// Scene2d API
    @Override
    public float getPrefWidth() {
        return mItemWidth * mColumnCount;
    }

    @Override
    public float getPrefHeight() {
        int rowCount = MathUtils.ceil(mItems.size / (float)mColumnCount);
        return mItemHeight * rowCount;
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

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        final float gutterWidth = (getWidth() - mItemWidth * mColumnCount) / 2;
        float x = gutterWidth;
        float y = getHeight() - mItemHeight;

        for (int idx = 0; idx < mItems.size; idx++) {
            T item = mItems.get(idx);
            if (idx == mSelectedIndex) {
                int padding = mMenu.getMenuStyle().focusPadding;
                Rectangle rect = mRenderer.getItemRectangle(mItemWidth, mItemHeight, item);
                mStyle.selected.draw(batch, getX() + x + rect.x - padding, getY() + y + rect.y - padding,
                        rect.width + 2 * padding, rect.height + 2 * padding);
            }
            mRenderer.render(batch, getX() + x, getY() + y, mItemWidth, mItemHeight, item);
            x += mItemWidth;
            if (x + mItemWidth > getWidth()) {
                x = gutterWidth;
                y -= mItemHeight;
            }
        }
    }

    /// MenuItem API
    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void trigger() {
        mSelectedIndex = mCurrentIndex;
        ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
        fire(changeEvent);
        Pools.free(changeEvent);
    }

    @Override
    public boolean goUp() {
        if (mCurrentIndex - mColumnCount >= 0) {
            mCurrentIndex -= mColumnCount;
            mMenu.animateFocusIndicator();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean goDown() {
        if (mCurrentIndex + mColumnCount < mItems.size) {
            mCurrentIndex += mColumnCount;
            mMenu.animateFocusIndicator();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void goLeft() {
        if (mCurrentIndex > 0) {
            mCurrentIndex--;
            mMenu.animateFocusIndicator();
        }
    }

    @Override
    public void goRight() {
        if (mCurrentIndex < mItems.size - 1) {
            mCurrentIndex++;
            mMenu.animateFocusIndicator();
        }
    }

    @Override
    public Rectangle getFocusRectangle() {
        updateFocusRectangle();
        return mFocusRectangle;
    }

    /// Private
    private void updateFocusRectangle() {
        if (mCurrentIndex == -1) {
            mFocusRectangle.set(0, 0, -1, -1);
            return;
        }
        T item = mItems.get(mCurrentIndex);
        float x = getX() + (mCurrentIndex % mColumnCount) * mItemWidth;
        float y = getY() + getHeight() - (mCurrentIndex / mColumnCount + 1) * mItemHeight;
        Rectangle rect = mRenderer.getItemRectangle(mItemWidth, mItemHeight, item);
        mFocusRectangle.set(x + rect.x, y + rect.y, rect.width, rect.height);
    }

    private void touchDown(float touchX, float touchY) {
        if (mItems.size == 0) {
            return;
        }
        if (mItemWidth <= 0 || mItemHeight <= 0) {
            NLog.e("Invalid item size");
            return;
        }
        final int columnCount = Math.min(MathUtils.floor(getWidth() / mItemWidth), mItems.size);
        final float gutterWidth = (getWidth() - mItemWidth * columnCount) / 2;
        int row = MathUtils.floor((getHeight() - touchY) / mItemHeight);
        int column = MathUtils.floor((touchX - gutterWidth) / mItemWidth);
        int idx = row * columnCount + column;
        if (idx >= 0 && idx < mItems.size) {
            mCurrentIndex = idx;
            mMenu.setCurrentItem(this);
            trigger();
        }
    }
}
