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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.agateau.utils.GylMathUtils;

/**
 * An AI pilot
 */
public class AIPilot implements Pilot {
    private final MapInfo mMapInfo;
    private final Racer mRacer;

    public AIPilot(MapInfo mapInfo, Racer racer) {
        mMapInfo = mapInfo;
        mRacer = racer;
    }

    private final Vector2 mTargetVector = new Vector2();
    @Override
    public void act(float dt) {
        Vehicle vehicle = mRacer.getVehicle();
        vehicle.setAccelerating(true);

        float lapDistance = mRacer.getLapPositionComponent().getLapDistance();
        WaypointStore store = mMapInfo.getWaypointStore();
        int index = store.getWaypointIndex(lapDistance);
        Vector2 waypoint = store.getWaypoint(store.getNextIndex(index));
        mTargetVector.set(waypoint.x - mRacer.getX(), waypoint.y - mRacer.getY());

        Bonus bonus = mRacer.getBonus();
        if (bonus != null) {
            bonus.aiAct(dt);
        }

        float targetAngle = GylMathUtils.normalizeAngle(mTargetVector.angle());
        float vehicleAngle = GylMathUtils.normalizeAngle(vehicle.getAngle());
        float deltaAngle = targetAngle - vehicleAngle;
        if (deltaAngle > 180) {
            deltaAngle -= 360;
        } else if (deltaAngle < -180) {
            deltaAngle += 360;
        }
        float direction = MathUtils.clamp(deltaAngle / GamePlay.instance.lowSpeedMaxSteer, -1, 1);
        vehicle.setDirection(direction);
    }
}
