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

/**
 * Customization of the gameplay
 *
 * <p>Any change made to the values of this class at runtime will cause the stats to not be saved.
 */
public class GamePlay {
    public int racerCount = 6;
    public int maxDrivingForce = 50;
    public int lowSpeed = 20;
    public int maxSpeed = 270;
    public int maxLateralImpulse = 2;

    public int stoppedMaxSteer = 80;
    public int lowSpeedMaxSteer = 14;
    public int highSpeedMaxSteer = 4;
    public float steeringStep = 0.1f;

    public int vehicleDensity = 3;
    public int tireBaseDensity = 15;
    public int vehicleRestitution = 1;
    public int groundDragFactor = 8;
    public int borderRestitution = 1;
    public float simplifiedCollisionMaxDeltaV = 0.4f;
    public float simplifiedCollisionKFactor = 4;

    public int viewportWidth = 60;

    public int turboStrength = 200;
    public float turboDuration = 0.5f;

    // When an AI is better ranked than a player, set its max speed to this percent of the best max
    // speed
    public float aiSpeedLimiter = 0.8f;

    public float extremeSpeedLimiter = 0.5f;

    public boolean oneLapOnly = false;
    public boolean freeCamera = false;

    public static final GamePlay instance = new GamePlay();
}
