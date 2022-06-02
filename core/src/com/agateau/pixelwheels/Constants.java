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
package com.agateau.pixelwheels;

import static java.lang.System.getenv;

import com.badlogic.gdx.graphics.Color;

/** Global game constants */
public class Constants {
    public static final float UNIT_FOR_PIXEL = 1f / 20f;
    public static final float CAMERA_ADVANCE_PERCENT = 0.25f;

    public static final int MAX_PLAYERS = 2;

    public static final String DEBUG_SCREEN;

    public static final Color HALF_IMMERSED_COLOR = new Color(0.5f, 0.75f, 1, 0.4f);
    public static final Color FULLY_IMMERSED_COLOR = new Color(0, 0.5f, 1, 0.2f);

    public static final String LOG_FILENAME = "pixelwheels.log";
    // 1 mega-byte max size
    public static final long LOG_MAX_SIZE = 1024 * 1024;

    public enum Store {
        ITCHIO,
        GPLAY
    }

    // Not final because it can be updated by the StoreConfigurator instance
    public static Store STORE = Store.ITCHIO;

    static {
        String value = getenv("PW_DEBUG_SCREEN");
        DEBUG_SCREEN = value == null ? "" : value;
    }
}
