/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
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

/** A POJO class to store font names and sizes used by UiAssets */
public class FontSet {
    public String defaultFontName;
    public String defaultBoldFontName;
    public int defaultFontSize;

    public String titleFontName;
    public int titleFontSize;

    public String hudFontName;

    public static FontSet createTestInstance() {
        FontSet fontSet = new FontSet();
        fontSet.defaultFontName = "Xolonium-Regular.ttf";
        fontSet.defaultBoldFontName = "Xolonium-Bold.ttf";
        fontSet.defaultFontSize = 24;

        fontSet.titleFontName = "Kwajong-Italic.otf";
        fontSet.titleFontSize = 36;

        fontSet.hudFontName = fontSet.defaultFontName;

        return fontSet;
    }
}
