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
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;

class FocusIndicator {
    private static final float IN_ANIMATION_DURATION = 0.1f;
    private static final float OUT_ANIMATION_DURATION = 0.4f;
    private final Menu.MenuStyle mMenuStyle;

    private boolean mFocused = false;
    private float mAlpha = 0;

    FocusIndicator(Menu menu) {
        mMenuStyle = menu.getMenuStyle();
    }

    public void act(float delta) {
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
        int padding = mMenuStyle.focusPadding;
        float oldA = DrawUtils.setBatchAlpha(batch, batch.getColor().a * mAlpha);
        x = MathUtils.floor(x);
        y = MathUtils.floor(y);
        width = MathUtils.ceil(width);
        height = MathUtils.ceil(height);
        mMenuStyle.focus.draw(
                batch, x - padding, y - padding, width + 2 * padding, height + 2 * padding);
        DrawUtils.setBatchAlpha(batch, oldA);
    }

    public void setFocused(boolean focused) {
        mFocused = focused;
    }
}
