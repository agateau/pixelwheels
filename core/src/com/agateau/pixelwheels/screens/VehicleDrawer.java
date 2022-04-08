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

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.utils.BodyRegionDrawer;
import com.agateau.pixelwheels.utils.DrawUtils;
import com.agateau.pixelwheels.vehicledef.AxleDef;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** Helper class to draw a vehicle in the UI. This is *not* used to draw a vehicle in the game */
class VehicleDrawer {
    private static final int FB_WIDTH = 128;
    private static final int FB_HEIGHT = 128;
    private final Assets mAssets;
    private final Rectangle mRectangle = new Rectangle();
    private final FrameBuffer mFrameBuffer;
    private final Vector2 mFrameBufferCenter = new Vector2(FB_WIDTH / 2f, FB_HEIGHT / 2f);
    private final Matrix4 mFrameBufferProjectionMatrix = new Matrix4();
    private final Matrix4 mFrameBufferTransformMatrix = new Matrix4();

    public VehicleDef vehicleDef;
    public final Vector2 center = new Vector2(0, 0);
    public float angle = 0;

    VehicleDrawer(Assets assets) {
        mAssets = assets;
        mFrameBuffer =
                new FrameBuffer(Pixmap.Format.RGBA8888, FB_WIDTH, FB_HEIGHT, false /* hasDepth */);
        mFrameBufferProjectionMatrix.setToOrtho2D(0, 0, FB_WIDTH, FB_HEIGHT);
    }

    private final Vector2 sWheelPos = new Vector2();
    private static final Matrix4 sOldMatrix = new Matrix4();
    private static final Matrix4 sOldTransformMatrix = new Matrix4();

    /** Get bounding rectangle, centered on this.center */
    public Rectangle getRectangle() {
        TextureRegion region = vehicleDef.getImage(mAssets);

        float halfHeight = region.getRegionWidth() / 2f;
        float halfWidth = region.getRegionHeight() / 2f;

        float axleYOrigin = -halfHeight;
        float bottom = -halfHeight;
        float top = halfHeight;

        for (AxleDef axle : vehicleDef.axles) {
            float axleY = axleYOrigin + axle.y;
            TextureRegion wheelRegion = axle.getTexture(mAssets);
            float thickness = wheelRegion.getRegionHeight();
            float diameter = wheelRegion.getRegionWidth();

            float wheelRight = axle.width / 2 + thickness / 2;
            float wheelBottom = axleY - diameter / 2;
            float wheelTop = axleY + diameter / 2;

            halfWidth = Math.max(halfWidth, wheelRight);
            bottom = Math.min(bottom, wheelBottom);
            top = Math.max(top, wheelTop);
        }

        mRectangle.width = halfWidth * 2;
        mRectangle.height = top - bottom;
        mRectangle.x = center.x - halfWidth;
        mRectangle.y = center.y + bottom;
        return mRectangle;
    }

    public void draw(Batch batch) {
        TextureRegion region = vehicleDef.getImage(mAssets);

        batch.end();

        // Draw shadows to mFrameBuffer
        sOldMatrix.set(batch.getProjectionMatrix());
        sOldTransformMatrix.set(batch.getTransformMatrix());
        batch.setProjectionMatrix(mFrameBufferProjectionMatrix);
        batch.setTransformMatrix(mFrameBufferTransformMatrix);
        mFrameBuffer.begin();
        batch.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawInternal(batch, region, mFrameBufferCenter);

        batch.end();
        mFrameBuffer.end();

        // Draw mFrameBuffer on screen
        batch.setProjectionMatrix(sOldMatrix);
        batch.setTransformMatrix(sOldTransformMatrix);
        batch.begin();
        drawFrameBuffer(batch);

        // Draw vehicle to screen
        drawInternal(batch, region, center);
    }

    private void drawFrameBuffer(Batch batch) {
        float oldColor = batch.getPackedColor();
        batch.setColor(0, 0, 0, BodyRegionDrawer.SHADOW_ALPHA);
        batch.draw(
                mFrameBuffer.getColorBufferTexture(),
                // dst
                center.x - FB_WIDTH / 2f + BodyRegionDrawer.SHADOW_OFFSET_PX,
                center.y - FB_HEIGHT / 2f - BodyRegionDrawer.SHADOW_OFFSET_PX,
                // dst size
                FB_WIDTH,
                FB_HEIGHT,
                // src
                0,
                0,
                FB_WIDTH,
                FB_HEIGHT,
                // flips
                false,
                true);
        batch.setPackedColor(oldColor);
    }

    private void drawInternal(Batch batch, TextureRegion region, Vector2 center_) {
        float axleXOrigin = -region.getRegionWidth() / 2f;
        for (AxleDef axle : vehicleDef.axles) {
            // axleX is based on axle.y because the vehicle texture faces right, but the axle
            // definition faces top
            // See VehicleCreator for more details
            float axleX = axleXOrigin + axle.y;
            TextureRegion wheelRegion = axle.getTexture(mAssets);
            drawWheel(batch, wheelRegion, center_, axleX, -axle.width / 2);
            drawWheel(batch, wheelRegion, center_, axleX, axle.width / 2);
        }

        DrawUtils.drawCentered(batch, region, center_.x, center_.y, 1, angle);
    }

    private void drawWheel(Batch batch, TextureRegion region, Vector2 center_, float wx, float wy) {
        sWheelPos.set(wx, wy).rotateDeg(angle).add(center_);
        DrawUtils.drawCentered(batch, region, sWheelPos, 1, angle);
    }
}
