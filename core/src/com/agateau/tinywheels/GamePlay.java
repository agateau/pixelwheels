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
 * Customization of the gameplay
 */
public class GamePlay {
    public int racerCount = 6;
    public int maxDrivingForce = 60;
    public int lowSpeed = 20;
    public int maxSpeed = 180;
    public int maxLateralImpulse = 3;
    public int maxSkidmarks = 60;

    public int lowSpeedMaxSteer = 20;
    public int highSpeedMaxSteer = 5;
    public int vehicleDensity = 14;
    public int vehicleRestitution = 1;
    public int groundDragFactor = 8;
    public int borderRestitution = 1;

    public int viewportWidth = 70;

    public int turboStrength = 500;
    public float turboDuration = 0.6f;

    public boolean alwaysShowTouchInput = false;

    public boolean showTestTrack = false;
    public boolean createSpeedReport = false;

    public static final GamePlay instance = new GamePlay();
}
