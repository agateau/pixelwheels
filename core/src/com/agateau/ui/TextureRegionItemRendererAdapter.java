package com.agateau.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Generic implementation of GridMenuItem.ItemRenderer for a TextureRegion
 */
public abstract class TextureRegionItemRendererAdapter<T> implements GridMenuItem.ItemRenderer<T> {
    private final Rectangle mRectangle = new Rectangle();

    @Override
    public Rectangle getItemRectangle(float width, float height, T item) {
        TextureRegion region = getItemRegion(item);
        updateRectangle(width, height, region);
        return mRectangle;
    }

    @Override
    public void render(Batch batch, float x, float y, float width, float height, T item) {
        TextureRegion region = getItemRegion(item);
        updateRectangle(width, height, region);
        batch.draw(region, x + mRectangle.x, y + mRectangle.y, mRectangle.width, mRectangle.height);
    }

    protected abstract TextureRegion getItemRegion(T item);

    private void updateRectangle(float width, float height, TextureRegion region) {
        float rWidth = region.getRegionWidth();
        float rHeight = region.getRegionHeight();
        float xScale = width / rWidth;
        float yScale = height / rHeight;
        float scale = Math.min(Math.min(xScale, yScale), 1);
        mRectangle.width = rWidth * scale;
        mRectangle.height = rHeight * scale;
        mRectangle.x = (width - mRectangle.width) / 2;
        mRectangle.y = (height - mRectangle.height) / 2;
    }
}
