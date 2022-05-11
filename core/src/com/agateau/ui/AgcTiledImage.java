/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.pixelwheels.utils.DrawUtils;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/** A TiledImage that does not draw gaps between tiles when zoomed in */
public class AgcTiledImage extends Actor {
    private TiledDrawable mDrawable;
    private FrameBuffer mFrameBuffer;

    public void setRegion(TextureRegion region) {
        mDrawable = new TiledDrawable(region);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        // Regenerate the framebuffer because it depends on the size. Do not do this inside act()
        // or draw() because it causes the SpriteBatch matrix to change so the UI is no longer
        // drawn aligned to their expected geometries, causing bugs like
        // https://github.com/agateau/pixelwheels/issues/119
        ensureFrameBufferOK();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
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
                0,
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
                && mFrameBuffer.getWidth() >= width
                && mFrameBuffer.getHeight() >= height) {
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
