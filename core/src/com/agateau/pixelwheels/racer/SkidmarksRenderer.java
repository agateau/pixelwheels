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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** Render a circular array of skidmarks */
public class SkidmarksRenderer {
    private static final float SKIDMARK_WIDTH = 7 * Constants.UNIT_FOR_PIXEL;
    private static final float SKIDMARK_ALPHA_INC = 0.05f;
    private static final float SKIDMARK_ALPHA_MIN = 0.1f;
    private static final float SKIDMARK_ALPHA_MAX = 0.4f;

    private final Assets mAssets;

    public SkidmarksRenderer(Assets assets) {
        mAssets = assets;
    }

    public void draw(Batch batch, CircularArray<Wheel.Skidmark> skidmarks, Rectangle viewBounds) {
        int idx = skidmarks.getBeginIndex();
        if (idx == skidmarks.getEndIndex()) {
            return;
        }
        Wheel.Skidmark mark2 = skidmarks.get(idx);
        idx = skidmarks.getNextIndex(idx);
        float alpha = SKIDMARK_ALPHA_MIN;

        for (; idx != skidmarks.getEndIndex(); idx = skidmarks.getNextIndex(idx)) {
            Wheel.Skidmark mark1 = mark2;
            mark2 = skidmarks.get(idx);

            if (mark1.isEndIndicator() || mark2.isEndIndicator()) {
                alpha = SKIDMARK_ALPHA_MIN;
                continue;
            }

            if (mark1.isFinished() && mark2.isFinished()) {
                continue;
            }

            if (!mark2.hasThickness()) {
                Vector2 pos1 = mark1.getPos();
                Vector2 pos2 = mark2.getPos();
                if (!viewBounds.contains(pos1) && !viewBounds.contains(pos2)) {
                    continue;
                }
                Vector2 thickness = AgcMathUtils.computeWidthVector(pos1, pos2, SKIDMARK_WIDTH / 2);
                mark2.setThickness(thickness);
                if (!mark1.hasThickness()) {
                    mark1.setThickness(thickness);
                }
            }

            drawSkidmark(
                    batch,
                    mark1,
                    mark2,
                    alpha * mark1.getOpacity(),
                    (alpha + SKIDMARK_ALPHA_INC) * mark2.getOpacity());
            alpha = Math.min(SKIDMARK_ALPHA_MAX, alpha + SKIDMARK_ALPHA_INC);
        }
    }

    private final float[] mVertices = new float[4 * 5];

    private void drawSkidmark(
            Batch batch, Wheel.Skidmark mark1, Wheel.Skidmark mark2, float alpha1, float alpha2) {
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
        Vector2 pos1 = mark1.getPos();
        Vector2 pos2 = mark2.getPos();

        float x0 = pos1.x + mark1.getThickness().x;
        float y0 = pos1.y + mark1.getThickness().y;

        float x1 = pos1.x - mark1.getThickness().x;
        float y1 = pos1.y - mark1.getThickness().y;

        float x2 = pos2.x - mark2.getThickness().x;
        float y2 = pos2.y - mark2.getThickness().y;

        float x3 = pos2.x + mark2.getThickness().x;
        float y3 = pos2.y + mark2.getThickness().y;

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
