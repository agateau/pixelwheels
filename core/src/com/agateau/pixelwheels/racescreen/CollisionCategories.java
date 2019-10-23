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
package com.agateau.pixelwheels.racescreen;

/** Collision categories for Box2D fixtures */
public class CollisionCategories {
    public static final int WALL = 1;
    public static final int RACER = 2;
    public static final int RACER_BULLET = 4;

    // Objects which do not stop bullets
    public static final int EXPLOSABLE = 8;

    // Masks for all bodies vehicles cannot go through
    public static final int SOLID_BODIES = WALL | RACER | RACER_BULLET | EXPLOSABLE;
}
