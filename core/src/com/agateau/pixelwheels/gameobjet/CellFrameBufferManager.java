/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.gameobjet;

import com.agateau.pixelwheels.Constants;
import com.agateau.utils.Assert;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

/**
 * Manages cells in a frame buffer. A cell is a rectangle of the frame buffer.
 *
 * <p>This class is useful for objects which are made of multiple textures, and need to be drawn
 * with some amount of transparency. If one draws the textures of such an object directly on the
 * screen, when alpha is less than 1 then the textures below are visible through the top textures.
 *
 * <p>We can avoid this problem by drawing the textures to a CellFrameBufferManager cell, at full
 * opacity, then drawing the content of the cell, at the required opacity, to the screen.
 */
public class CellFrameBufferManager {
    private static final int SIZE = 1024;
    private final FrameBuffer mFrameBuffer;

    private final Vector2 mNextCellOrigin = new Vector2();
    private float mNextRowY = 0;
    private final Vector2 mTmp = new Vector2();

    private Batch mBatch;

    private final Matrix4 mOldProjectionMatrix = new Matrix4();
    private final Matrix4 mProjectionMatrix = new Matrix4();

    public CellFrameBufferManager() {
        mFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, SIZE, SIZE, false /* hasDepth */);
        mFrameBuffer
                .getColorBufferTexture()
                .setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        mProjectionMatrix.setToOrtho2D(0, 0, SIZE, SIZE);
    }

    /** Returns a Vector2 pointing to the left-bottom corner of the cell */
    public Vector2 reserveCell(int width, int height) {
        if (mNextCellOrigin.x + width >= SIZE) {
            // Does not fit current row
            mNextCellOrigin.set(0, mNextRowY);
        }
        mTmp.set(mNextCellOrigin);

        mNextCellOrigin.x += width;
        float top = mNextCellOrigin.y + height;
        Assert.check(top < SIZE, "Not enough space to fit cells");
        mNextRowY = Math.max(mNextRowY, top);
        return mTmp;
    }

    /** Begins drawing to the manager texture. Must be called before calling drawToCell() */
    public void begin(Batch batch) {
        mBatch = batch;
        mOldProjectionMatrix.set(mBatch.getProjectionMatrix());

        mFrameBuffer.begin();
        mBatch.begin();
        mBatch.setProjectionMatrix(mProjectionMatrix);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void end() {
        mBatch.end();
        mFrameBuffer.end();

        mBatch.setProjectionMatrix(mOldProjectionMatrix);
    }

    public void drawCell(Batch batch, Vector2 dst, Vector2 cellOrigin, int cellSize) {
        drawCell(batch, dst.x, dst.y, cellOrigin, cellSize);
    }

    public void drawCell(Batch batch, float dstX, float dstY, Vector2 cellOrigin, int cellSize) {
        drawScaledCell(batch, dstX, dstY, cellOrigin, cellSize, 1f);
    }

    public void drawScaledCell(
            Batch batch, Vector2 dst, Vector2 cellOrigin, int cellSize, float scale) {
        drawScaledCell(batch, dst.x, dst.y, cellOrigin, cellSize, scale);
    }

    public void drawScaledCell(
            Batch batch, float dstX, float dstY, Vector2 cellOrigin, int cellSize, float scale) {
        float w = Constants.UNIT_FOR_PIXEL * cellSize * scale;
        float h = Constants.UNIT_FOR_PIXEL * cellSize * scale;

        float textureSize = CellFrameBufferManager.SIZE;
        float u = cellOrigin.x / textureSize;
        float v = cellOrigin.y / textureSize;
        float v2 = (cellOrigin.y + cellSize) / textureSize;
        float u2 = (cellOrigin.x + cellSize) / textureSize;

        batch.draw(
                mFrameBuffer.getColorBufferTexture(),
                // dst
                dstX - w / 2f,
                dstY - h / 2f,
                w,
                h,
                // src
                u,
                v,
                u2,
                v2);
    }
}
