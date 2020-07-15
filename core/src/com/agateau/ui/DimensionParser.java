/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.utils.log.NLog;

/**
 * Helper class to parse a dimension string.
 *
 * <p>The dimension can be expressed in pixels: 2px or in grid units: 4g
 *
 * <p>The grid size is set through the gridSize public attribute
 */
public class DimensionParser {
    public float gridSize = 1;

    public enum Unit {
        GRID,
        PIXEL
    }

    public float parse(String txt) {
        return parse(txt, Unit.PIXEL);
    }

    public float parse(String txt, Unit defaultUnit) {
        if (txt.equals("0")) {
            return 0;
        }
        if (txt.endsWith("px")) {
            return Float.parseFloat(txt.substring(0, txt.length() - 2));
        } else if (txt.endsWith("g")) {
            return Float.parseFloat(txt.substring(0, txt.length() - 1)) * this.gridSize;
        } else {
            float k = defaultUnit == Unit.PIXEL ? 1 : this.gridSize;
            try {
                return Float.parseFloat(txt) * k;
            } catch (NumberFormatException exc) {
                NLog.e("Invalid dimension text: " + txt);
                return 12;
            }
        }
    }
}
