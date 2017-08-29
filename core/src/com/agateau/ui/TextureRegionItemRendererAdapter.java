package com.agateau.ui;

import com.agateau.utils.Assert;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Generic implementation of GridMenuItem.ItemRenderer for a TextureRegion
 */
public abstract class TextureRegionItemRendererAdapter<T> implements GridMenuItem.ItemRenderer<T> {
    private final Rectangle mRectangle = new Rectangle();
    private float mScale = 1;
    private float mAngle = 0;

    public void setAngle(float angle) {
        Assert.check(angle % 90 == 0, "Angle must be a multiple of 90");
        mAngle = angle;
    }

    @Override
    public Rectangle getItemRectangle(float width, float height, T item) {
        TextureRegion region = getItemRegion(item);
        updateRenderInfo(width, height, region);
        return mRectangle;
    }

    @Override
    public void render(Batch batch, float x, float y, float width, float height, T item) {
        TextureRegion region = getItemRegion(item);
        updateRenderInfo(width, height, region);
        float rWidth = region.getRegionWidth();
        float rHeight = region.getRegionHeight();
        batch.draw(region,
                x + (width - rWidth) / 2, y + (height - rHeight) / 2, // pos
                rWidth / 2, rHeight / 2, // origin
                rWidth, rHeight, // width
                mScale, mScale, // scale
                mAngle // rotation
        );
    }

    protected abstract TextureRegion getItemRegion(T item);

    private void updateRenderInfo(float width, float height, TextureRegion region) {
        float rWidth = region.getRegionWidth();
        float rHeight = region.getRegionHeight();
        if (mAngle % 180 > 0) {
            // Swap width and height if necessary
            float tmp = rHeight;
            rHeight = rWidth;
            rWidth = tmp;
        }
        float xScale = width / rWidth;
        float yScale = height / rHeight;
        mScale = Math.min(Math.min(xScale, yScale), 1);
        mRectangle.width = rWidth * mScale;
        mRectangle.height = rHeight * mScale;
        mRectangle.x = (width - mRectangle.width) / 2;
        mRectangle.y = (height - mRectangle.height) / 2;
    }
}
