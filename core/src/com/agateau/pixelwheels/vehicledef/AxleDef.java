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
package com.agateau.pixelwheels.vehicledef;

import com.agateau.pixelwheels.TextureRegionProvider;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** Definition of a vehicle axle */
public class AxleDef {
    public enum TireSize {
        NORMAL,
        LARGE
    }

    public float width;
    public float y;
    public float steer;
    public float drive;
    public boolean drift;
    public TireSize tireSize;

    public TextureRegion getTexture(TextureRegionProvider provider) {
        return provider.findRegion("tires/" + tireSize.name());
    }
}
