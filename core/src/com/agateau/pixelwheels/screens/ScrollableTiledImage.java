/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.utils.DrawUtils;
import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/** An actor to draw tiled images, without gaps between tiles when zoomed */
class ScrollableTiledImage extends Actor {
    private final float mPixelsPerSecond;
    private final TiledDrawable mDrawable;
    private FrameBuffer mFrameBuffer;

    private float mOffset = 0;

    ScrollableTiledImage(TextureRegion region, float pixelsPerSecond) {
        mDrawable = new TiledDrawable(region);
        mPixelsPerSecond = pixelsPerSecond;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float tileHeight = mDrawable.getMinHeight();
        mOffset = AgcMathUtils.modulo(mOffset + delta * mPixelsPerSecond, tileHeight);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ensureFrameBufferOK();
        DrawUtils.multiplyBatchAlphaBy(batch, parentAlpha);
        batch.draw(
                mFrameBuffer.getColorBufferTexture(),
                // dst
                getX(),
                getY(),
                // origin
                0,
                0,
                // dst size
                getWidth(),
                getHeight(),
                // scale
                1,
                1,
                // rotation
                0,
                // src
                0,
                (int) mOffset,
                (int) getWidth(),
                (int) getHeight(),
                // flips
                false,
                true);
    }

    private void ensureFrameBufferOK() {
        int width = (int) (getWidth() + mDrawable.getMinWidth());
        int height = (int) (getHeight() + mDrawable.getMinHeight());
        if (mFrameBuffer != null
                && mFrameBuffer.getWidth() == width
                && mFrameBuffer.getHeight() == height) {
            return;
        }
        mFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false /* hasDepth */);

        SpriteBatch batch = new SpriteBatch();
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));

        mFrameBuffer.begin();
        batch.begin();
        mDrawable.draw(batch, 0, 0, width, height);
        batch.end();
        mFrameBuffer.end();
        batch.dispose();
    }
}
