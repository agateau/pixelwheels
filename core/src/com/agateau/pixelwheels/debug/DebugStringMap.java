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
package com.agateau.pixelwheels.debug;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.StringBuilder;
import java.util.HashMap;

/** Helper class to show debug info on hud */
@SuppressWarnings("unused")
public class DebugStringMap {
    private static final String[] PERCENT_VALUES = new String[10];

    static {
        StringBuilder builder = new StringBuilder();
        builder.setLength(10);
        for (int row = 0; row < PERCENT_VALUES.length; ++row) {
            for (int idx = 0; idx < builder.length; ++idx) {
                builder.setCharAt(idx, idx < row ? '=' : '_');
            }
            PERCENT_VALUES[row] = builder.toString();
        }
    }

    private static final HashMap<String, String> sMap = new HashMap<>();

    public static HashMap<String, String> getMap() {
        return sMap;
    }

    public static void put(String key, Object message) {
        sMap.put(key, message.toString());
    }

    public static void putPercent(String key, float percent) {
        int idx =
                MathUtils.clamp(
                        (int) (percent * PERCENT_VALUES.length), 0, PERCENT_VALUES.length - 1);
        sMap.put(key, PERCENT_VALUES[idx]);
    }
}
