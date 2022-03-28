/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.utils;

import com.agateau.pixelwheels.Constants;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/** Helper class to draw a TextureRegion for a Box2D Body */
public class BodyRegionDrawer {
    public static final float SHADOW_ALPHA = 0.35f;
    public static final float SHADOW_OFFSET_PX = 6;
    private static final int Z_MAX_SHADOW_OFFSET_PX = 30;
    private static final int SCALE_MAX_SHADOW_OFFSET_PX = 30;
    private Batch mBatch;
    private float mZ = 0;
    private float mScale = 1;
    private float mOffsetX = 0;
    private float mOffsetY = 0;
    private float mShadowAlpha = SHADOW_ALPHA;

    public void setBatch(Batch batch) {
        mBatch = batch;
    }

    public void setShadowAlpha(float shadowAlpha) {
        mShadowAlpha = shadowAlpha;
    }

    /**
     * Defines the default Z value for a body.
     *
     * <p>0 for a ground object, 1 for flying object.
     *
     * <p>Only affects the offset of the shadow.
     */
    public void setZ(float z) {
        mZ = z;
    }

    /**
     * Defines the scale of the body.
     *
     * <p>1 by default, bigger for bigger objects.
     *
     * <p>Affects the size of the region, and the size and offset of its shadow
     */
    public void setScale(float scale) {
        mScale = scale;
    }

    public void setOffset(float x, float y) {
        mOffsetX = x;
        mOffsetY = y;
    }

    public void draw(Body body, TextureRegion region) {
        Vector2 center = body.getPosition();
        float angle = body.getAngle();
        float x = center.x + mOffsetX * MathUtils.cos(angle) - mOffsetY * MathUtils.sin(angle);
        float y = center.y + mOffsetX * MathUtils.sin(angle) + mOffsetY * MathUtils.cos(angle);
        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        mBatch.draw(
                region,
                x - w / 2,
                y - h / 2, // pos
                w / 2,
                h / 2, // origin
                w,
                h, // size
                mScale,
                mScale,
                angle * MathUtils.radDeg);
    }

    public void drawShadow(Body body, TextureRegion region) {
        Vector2 center = body.getPosition();
        float angle = body.getAngle() * MathUtils.radiansToDegrees;
        float offset =
                (SHADOW_OFFSET_PX
                                + mZ * Z_MAX_SHADOW_OFFSET_PX
                                + (mScale - 1) * SCALE_MAX_SHADOW_OFFSET_PX)
                        * Constants.UNIT_FOR_PIXEL;
        float x = center.x + offset;
        float y = center.y - offset;
        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        float old = mBatch.getPackedColor();
        mBatch.setColor(0, 0, 0, mShadowAlpha);
        mBatch.draw(
                region, x - w / 2, y - h / 2, // pos
                w / 2, h / 2, // origin
                w, h, // size
                1, 1, // scale
                angle);
        mBatch.setPackedColor(old);
    }
}
