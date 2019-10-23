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
package com.agateau.ui;

import com.agateau.ui.menu.GridMenuItem;
import com.agateau.utils.Assert;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/** Generic implementation of GridMenuItem.ItemRenderer for a TextureRegion */
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
        batch.draw(
                region,
                x + (width - rWidth) / 2,
                y + (height - rHeight) / 2, // pos
                rWidth / 2,
                rHeight / 2, // origin
                rWidth,
                rHeight, // width
                mScale,
                mScale, // scale
                mAngle // rotation
                );
    }

    @Override
    public boolean isItemEnabled(T item) {
        return true;
    }

    protected abstract TextureRegion getItemRegion(T item);

    protected void updateRenderInfo(float width, float height, TextureRegion region) {
        float rWidth = region.getRegionWidth();
        float rHeight = region.getRegionHeight();
        if (mAngle % 180 > 0) {
            // Swap width and height if necessary
            float tmp = rHeight;
            //noinspection SuspiciousNameCombination
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

    public float getScale() {
        return mScale;
    }

    public float getAngle() {
        return mAngle;
    }
}
