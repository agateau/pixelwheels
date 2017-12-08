/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
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

import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * A viewport which scales to fit the screen, but sets units per pixel so that pixels are always
 * zoomed by a round value
 */
public class IntegerFitViewport extends ScreenViewport {
    final int mNativeWidth;
    final int mNativeHeight;

    public IntegerFitViewport(int nativeWidth, int nativeHeight) {
        mNativeWidth = nativeWidth;
        mNativeHeight = nativeHeight;
    }

    @Override
    public void update(int width, int height, boolean centerCamera) {
        int ppu = Math.min(width / mNativeWidth, height / mNativeHeight);
        if (ppu == 0) {
            ppu = 1;
        }
        setUnitsPerPixel(1f / ppu);
        super.update(width, height, true);
    }
}
