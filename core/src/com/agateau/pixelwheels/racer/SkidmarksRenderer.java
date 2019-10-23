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
package com.agateau.pixelwheels.racer;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.utils.AgcMathUtils;
import com.agateau.utils.CircularArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/** Render a circular array of skidmarks */
public class SkidmarksRenderer {
    private static final float SKIDMARK_WIDTH = 7 * Constants.UNIT_FOR_PIXEL;
    private static final float SKIDMARK_ALPHA_INC = 0.05f;
    private static final float SKIDMARK_ALPHA_MIN = 0.1f;
    private static final float SKIDMARK_ALPHA_MAX = 0.4f;

    private final Assets mAssets;

    private boolean mValidThickness = false;
    private float mThickX1;
    private float mThickY1;
    private float mThickX2;
    private float mThickY2;

    public SkidmarksRenderer(Assets assets) {
        mAssets = assets;
    }

    public void draw(Batch batch, CircularArray<Wheel.Skidmark> skidmarks) {
        int idx1 = skidmarks.getBeginIndex();
        if (idx1 == skidmarks.getEndIndex()) {
            return;
        }
        int idx2 = skidmarks.getNextIndex(idx1);
        float alpha = SKIDMARK_ALPHA_MIN;

        for (; idx2 != skidmarks.getEndIndex(); idx1 = idx2, idx2 = skidmarks.getNextIndex(idx2)) {
            Wheel.Skidmark mark1 = skidmarks.get(idx1);
            Wheel.Skidmark mark2 = skidmarks.get(idx2);

            Vector2 pos1 = mark1.getPos();
            Vector2 pos2 = mark2.getPos();

            if (!mValidThickness) {
                mValidThickness = true;
                Vector2 thickness = AgcMathUtils.computeWidthVector(pos1, pos2, SKIDMARK_WIDTH / 2);
                mThickX2 = thickness.x;
                mThickY2 = thickness.y;
            }

            if (!mark1.isEnd() && !mark2.isEnd()) {
                mThickX1 = mThickX2;
                mThickY1 = mThickY2;
                Vector2 thickness = AgcMathUtils.computeWidthVector(pos1, pos2, SKIDMARK_WIDTH / 2);
                mThickX2 = thickness.x;
                mThickY2 = thickness.y;
                drawSkidmark(
                        batch,
                        pos1,
                        pos2,
                        alpha * mark1.getOpacity(),
                        (alpha + SKIDMARK_ALPHA_INC) * mark2.getOpacity());
                alpha = Math.min(SKIDMARK_ALPHA_MAX, alpha + SKIDMARK_ALPHA_INC);
            } else {
                mValidThickness = false;
                alpha = SKIDMARK_ALPHA_MIN;
            }
        }
    }

    private final float[] mVertices = new float[4 * 5];

    private void drawSkidmark(Batch batch, Vector2 pos1, Vector2 pos2, float alpha1, float alpha2) {
        TextureRegion region = mAssets.skidmark;
        float c = Color.toFloatBits(1, 1, 1, alpha1);
        float c2 = Color.toFloatBits(1, 1, 1, alpha2);
        float u = region.getU();
        float v = region.getV();
        float u2 = region.getU2();
        float v2 = region.getV2();

        /*
            0            3
             x----------x        ^
             |          |        |
        pos1 x          x pos2   | thickness
             |          |
             x----------x
            1            2
         */
        float x0 = pos1.x + mThickX1;
        float y0 = pos1.y + mThickY1;

        float x1 = pos1.x - mThickX1;
        float y1 = pos1.y - mThickY1;

        float x2 = pos2.x - mThickX2;
        float y2 = pos2.y - mThickY2;

        float x3 = pos2.x + mThickX2;
        float y3 = pos2.y + mThickY2;

        initVertex(0, x0, y0, c, u, v);
        initVertex(1, x1, y1, c, u2, v);
        initVertex(2, x2, y2, c2, u2, v2);
        initVertex(3, x3, y3, c2, u, v2);

        batch.draw(region.getTexture(), mVertices, 0, mVertices.length);
    }

    private void initVertex(int index, float x, float y, float c, float u, float v) {
        int idx = index * 5;
        mVertices[idx++] = x;
        mVertices[idx++] = y;
        mVertices[idx++] = c;
        mVertices[idx++] = u;
        mVertices[idx] = v;
    }
}
