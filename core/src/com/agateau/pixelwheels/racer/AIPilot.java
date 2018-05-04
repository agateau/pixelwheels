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
package com.agateau.pixelwheels.racer;

import com.agateau.pixelwheels.bonus.Bonus;
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.map.WaypointStore;
import com.agateau.utils.AgcMathUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * An AI pilot
 */
public class AIPilot implements Pilot {
    private static final float MIN_NORMAL_SPEED = 1;
    private static final float MAX_BLOCKED_DURATION = 1;
    private static final float MAX_REVERSE_DURATION = 0.5f;
    private final GameWorld mGameWorld;
    private final Track mTrack;
    private final Racer mRacer;

    private enum State {
        NORMAL,
        BLOCKED,
    }

    private State mState = State.NORMAL;
    private float mBlockedDuration = 0;
    private float mReverseDuration = 0;

    public AIPilot(GameWorld gameWorld, Track track, Racer racer) {
        mGameWorld = gameWorld;
        mTrack = track;
        mRacer = racer;
    }

    private final Vector2 mTargetVector = new Vector2();
    @Override
    public void act(float dt) {
        handleBonus(dt);
        switch (mState) {
        case NORMAL:
            actNormal(dt);
            break;
        case BLOCKED:
            actBlocked(dt);
            break;
        }
    }

    private void actNormal(float dt) {
        updateAcceleration();
        updateDirection();
        float speed = mRacer.getVehicle().getSpeed();
        if (mGameWorld.getState() == GameWorld.State.RUNNING && speed < MIN_NORMAL_SPEED) {
            mBlockedDuration += dt;
            if (mBlockedDuration > MAX_BLOCKED_DURATION) {
                NLog.i("Racer %s blocked", mRacer);
                mState = State.BLOCKED;
                mReverseDuration = 0;
            }
        } else {
            mBlockedDuration = 0;
        }
    }

    private void actBlocked(float dt) {
        Vehicle vehicle = mRacer.getVehicle();
        vehicle.setAccelerating(false);
        vehicle.setBraking(true);
        vehicle.setDirection(0);
        mReverseDuration += dt;
        if (mReverseDuration > MAX_REVERSE_DURATION) {
            mState = State.NORMAL;
            mBlockedDuration = 0;
        }
    }

    private void updateAcceleration() {
        Vehicle vehicle = mRacer.getVehicle();
        vehicle.setAccelerating(true);
        vehicle.setBraking(false);

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
        float targetAngle = AgcMathUtils.normalizeAngle(mTargetVector.angle());
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
        WaypointStore store = mTrack.getWaypointStore();
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
