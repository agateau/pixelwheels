/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.ui.menu;

import com.agateau.pixelwheels.utils.DrawUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;

class FocusIndicator {
    private static final float IN_ANIMATION_DURATION = 0.1f;
    private static final float OUT_ANIMATION_DURATION = 0.4f;

    private static final float BLINK_DEPTH = 0.3f;
    private static final float BLINK_DURATION = 1.5f;

    private final Color mOldBatchColor = new Color();
    private final Menu.MenuStyle mMenuStyle;
    private final int mExtraPadding;

    private boolean mFocused = false;
    private float mAlpha = 0;
    private float mBlinkT = 0;

    FocusIndicator(Menu menu) {
        this(menu.getMenuStyle());
    }

    FocusIndicator(Menu.MenuStyle menuStyle) {
        this(menuStyle, 0);
    }

    FocusIndicator(Menu.MenuStyle menuStyle, int extraPadding) {
        mMenuStyle = menuStyle;
        mExtraPadding = extraPadding;
    }

    public void act(float delta) {
        if (mFocused) {
            mBlinkT += delta;
            if (mBlinkT > BLINK_DURATION) {
                mBlinkT -= BLINK_DURATION;
            }
        }
        if (mFocused && mAlpha < 1) {
            mAlpha += delta / IN_ANIMATION_DURATION;
        } else if (!mFocused && mAlpha > 0) {
            mAlpha -= delta / OUT_ANIMATION_DURATION;
        }
        mAlpha = MathUtils.clamp(mAlpha, 0, 1);
    }

    public void draw(Batch batch, float x, float y, float width, float height) {
        if (mAlpha == 0) {
            return;
        }

        // `k` varies from -1 to 1 in BLINK_DURATION seconds
        float k = MathUtils.sin(mBlinkT * MathUtils.PI2 / BLINK_DURATION);

        float alpha = mAlpha * (1 - BLINK_DEPTH / 2 + BLINK_DEPTH / 2 * k);

        mOldBatchColor.set(batch.getColor());
        batch.setColor(alpha, alpha, alpha, alpha);

        int padding = mMenuStyle.focusPadding + mExtraPadding;
        DrawUtils.drawPixelAligned(batch, mMenuStyle.focus, x, y, width, height, padding);

        batch.setColor(mOldBatchColor);
    }

    public void setFocused(boolean focused) {
        mFocused = focused;
        if (mFocused) {
            mBlinkT = 0;
        }
    }
}
