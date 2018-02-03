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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.agateau.utils.GylMathUtils;

/**
 * An AI pilot
 */
public class AIPilot implements Pilot {
    private final GameWorld mGameWorld;
    private final MapInfo mMapInfo;
    private final Racer mRacer;

    public AIPilot(GameWorld gameWorld, MapInfo mapInfo, Racer racer) {
        mGameWorld = gameWorld;
        mMapInfo = mapInfo;
        mRacer = racer;
    }

    private final Vector2 mTargetVector = new Vector2();
    @Override
    public void act(float dt) {
        handleBonus(dt);
        updateAcceleration();
        updateDirection();
    }

    private void updateAcceleration() {
        Vehicle vehicle = mRacer.getVehicle();
        vehicle.setAccelerating(true);

        // If we are better ranked than a player, slow down a bit
        float rank = mGameWorld.getRacerRank(mRacer);
        boolean needLimit = false;
        for (Racer racer : mGameWorld.getPlayerRacers()) {
            if (mGameWorld.getRacerRank(racer) > rank) {
                needLimit = true;
                break;
            }
        }
        float limit = needLimit ? GamePlay.instance.aiSpeedLimiter : 1f;
        vehicle.setSpeedLimiter(limit);
    }

    private void updateDirection() {
        updateTargetVector();

        Vehicle vehicle = mRacer.getVehicle();
        float targetAngle = GylMathUtils.normalizeAngle(mTargetVector.angle());
        float vehicleAngle = vehicle.getAngle();
        float deltaAngle = targetAngle - vehicleAngle;
        if (deltaAngle > 180) {
            deltaAngle -= 360;
        } else if (deltaAngle < -180) {
            deltaAngle += 360;
        }
        float direction = MathUtils.clamp(deltaAngle / GamePlay.instance.lowSpeedMaxSteer, -1, 1);
        vehicle.setDirection(direction);
    }

    private void updateTargetVector() {
        float lapDistance = mRacer.getLapPositionComponent().getLapDistance();
        WaypointStore store = mMapInfo.getWaypointStore();
        int index = store.getWaypointIndex(lapDistance);
        Vector2 waypoint = store.getWaypoint(store.getNextIndex(index));
        mTargetVector.set(waypoint.x - mRacer.getX(), waypoint.y - mRacer.getY());
    }

    private void handleBonus(float dt) {
        Bonus bonus = mRacer.getBonus();
        if (bonus != null) {
            bonus.aiAct(dt);
        }
    }
}
