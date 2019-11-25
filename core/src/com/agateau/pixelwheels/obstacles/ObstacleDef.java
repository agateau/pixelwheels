/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.obstacles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Shape2D;

/**
 * Definition of an obstacle on a track
 *
 * <p>shape is in pixel coordinates
 */
public class ObstacleDef {
    public String id;
    public TextureRegion region;
    public Shape2D shape;
    public float density;

    public static ObstacleDef createCircle(String id, TextureRegion region, float density) {
        ObstacleDef def = new ObstacleDef();
        def.id = id;
        def.region = region;
        def.density = density;
        def.shape = new Circle(0, 0, region.getRegionWidth() / 2);
        return def;
    }
}
