/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
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

/**
 * Global game constants
 */
public class Constants {
    public static final float UNIT_FOR_PIXEL = 1f / 20f;
    public static final int Z_GROUND = 0;
    public static final int Z_VEHICLES = 1;
    public static final int Z_SHADOWS = 2;
    public static final int Z_OBSTACLES = 3;
    public static final int Z_FLYING = 4;

    public static final int Z_COUNT = Z_FLYING + 1;

    public static final float CAMERA_ADVANCE_PERCENT = 0.25f;

    // In degrees per second
    public static final float MAX_CAMERA_ROTATION_SPEED = 90;

    // When the camera must rotate by less than this angle, its maximum rotation speed is linearly
    // interpolated between 1 and MAX_CAMERA_ROTATION_SPEED. When it must rotate by more than this
    // angle its maximum rotation speed is MAX_CAMERA_ROTATION_SPEED.
    public static final float MIN_ANGLE_FOR_MAX_CAMERA_ROTATION_SPEED = 15;
}
