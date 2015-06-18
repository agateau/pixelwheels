package com.greenyetilab.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.log.NLog;

/**
 * An actor to select items in a grid
 */
public class GridSelector<T> extends Widget {
    private Array<T> mItems;
    private T mSelectedItem = null;
    private ItemRenderer<T> mRenderer;
    private float mItemWidth = 0;
    private float mItemHeight = 0;

    public interface ItemRenderer<T> {
        void render(Batch batch, float x, float y, float width, float height, T item, boolean selected);
    }

    public GridSelector() {
        addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == 0 && button != 0) {
                    return false;
                }
                GridSelector.this.touchDown(x, y);
                return true;
            }
        });
    }

    public void setSelected(T item) {
        if (mItems.indexOf(item, true) < 0) {
            NLog.e("Item is not in the item list");
            return;
        }
        mSelectedItem = item;
    }

    public T getSelected() {
        return mSelectedItem;
    }

    public void setItems(Array<T> items) {
        mItems = items;
        mSelectedItem = items.size > 0 ? items.first() : null;
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

        final int columnCount = Math.min(MathUtils.floor(getWidth() / mItemWidth), mItems.size);
        final float gutterWidth = (getWidth() - mItemWidth * columnCount) / 2;
        float x = gutterWidth;
        float y = getHeight() - mItemHeight;
        for (T item : mItems) {
            boolean selected = item == mSelectedItem;
            mRenderer.render(batch, getX() + x, getY() + y, mItemWidth, mItemHeight, item, selected);
            x += mItemWidth;
            if (x + mItemWidth > getWidth()) {
                x = gutterWidth;
                y -= mItemHeight;
            }
        }
    }

    void touchDown(float touchX, float touchY) {
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
        if (idx >=0 && idx < mItems.size) {
            mSelectedItem = mItems.get(idx);
        }
    }
}
