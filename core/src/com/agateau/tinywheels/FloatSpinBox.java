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
package com.agateau.tinywheels;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Locale;

/**
 * A SpinBox for floats
 */
public class FloatSpinBox extends SpinBox<Float> {
    public FloatSpinBox(float min, float max, Skin skin) {
        super(min, max, skin);
    }

    @Override
    protected Float tFromFloat(float f) {
        return f;
    }

    @Override
    protected float floatFromT(Float f) {
        return f;
    }

    @Override
    protected String stringFromT(Float f) {
        return String.format(Locale.getDefault(), "%.2f", f);
    }
}
