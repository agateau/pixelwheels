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
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.utils.Array;

/** Definition of a vehicle */
public class VehicleDef {
    public String id;
    public String name;
    public float speed;
    public Array<AxleDef> axles = new Array<>();
    public Array<Shape2D> shapes = new Array<>();

    public TextureRegion getImage(TextureRegionProvider provider) {
        return provider.findRegion("vehicles/" + mainImage);
    }

    public String toString() {
        return "vehicleDef(" + id + ")";
    }

    String mainImage;
}
