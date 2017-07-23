package com.agateau.utils;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;

/**
 * An actor to select items in a grid
 */
public class GridSelector<T> extends Widget {
    private static final float ANIMATION_DURATION = 0.2f;
    private Array<T> mItems;
    private T mSelectedItem = null;
    private ItemRenderer<T> mRenderer;
    private float mItemWidth = 0;
    private float mItemHeight = 0;

    private FloatAction mXAction = new FloatAction();
    private FloatAction mYAction = new FloatAction();

    public interface ItemRenderer<T> {
        void renderSelectionIndicator(Batch batch, float x, float y, float width, float height, T item);
        void render(Batch batch, float x, float y, float width, float height, T item);
    }

    public GridSelector() {
        mXAction.setDuration(ANIMATION_DURATION);
        mYAction.setDuration(ANIMATION_DURATION);
        mXAction.setInterpolation(Interpolation.pow2Out);
        mYAction.setInterpolation(Interpolation.pow2Out);
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == 0 && button != 0) {
                    return false;
                }
                GridSelector.this.touchDown(x, y);
                return true;
            }
        });
    }

    public void setSelected(T item) {
        if (item == null) {
            setSelectedIndex(mItems.size > 0 ? 0 : -1);
            return;
        }
        int index = mItems.indexOf(item, true);
        if (index < 0) {
            NLog.e("Item is not in the item list");
            return;
        }
        setSelectedIndex(index);
    }

    private void setSelectedIndex(int index) {
        if (index < 0) {
            mSelectedItem = null;
            return;
        }
        assert(index < mItems.size);
        mSelectedItem = mItems.get(index);

        final int columnCount = Math.min(MathUtils.floor(getWidth() / mItemWidth), mItems.size);
        int column = index % columnCount;
        int row = index / columnCount;
        updateActionEnds(column, row);
        mXAction.finish();
        mYAction.finish();

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
    public void act(float delta) {
        mXAction.act(delta);
        mYAction.act(delta);
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
            mRenderer.render(batch, getX() + x, getY() + y, mItemWidth, mItemHeight, item);
            x += mItemWidth;
            if (x + mItemWidth > getWidth()) {
                x = gutterWidth;
                y -= mItemHeight;
            }
        }

        if (mSelectedItem != null) {
            mRenderer.renderSelectionIndicator(batch, getX() + gutterWidth + mXAction.getValue(), getY() + mYAction.getValue(), mItemWidth, mItemHeight, mSelectedItem);
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
            mXAction.setStart(mXAction.getValue());
            mYAction.setStart(mYAction.getValue());
            updateActionEnds(column, row);
            mXAction.restart();
            mYAction.restart();
        }
    }

    private void updateActionEnds(int column, int row) {
        mXAction.setEnd(column * mItemWidth);
        mYAction.setEnd(getHeight() - (row + 1) * mItemHeight);
    }
}
