/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Utilities to draw shapes
 */
public class DrawUtils {
    public static void drawCross(ShapeRenderer renderer, Vector2 pos, float radius) {
        drawCross(renderer, pos.x, pos.y, radius);
    }

    public static void drawCross(ShapeRenderer renderer, float x, float y, float radius) {
        renderer.line(x - radius, y, x + radius, y);
        renderer.line(x, y - radius, x, y + radius);
    }
}
