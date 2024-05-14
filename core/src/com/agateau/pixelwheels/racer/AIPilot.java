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
package com.agateau.pixelwheels.racer;

import com.agateau.pixelwheels.BodyIdentifier;
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.bonus.Bonus;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Material;
import com.agateau.pixelwheels.map.MaterialChecker;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.map.WaypointStore;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.stats.TrackStats;
import com.agateau.pixelwheels.utils.ClosestBodyFinder;
import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/** An AI pilot */
public class AIPilot implements Pilot {
    private static final float MIN_NORMAL_SPEED = 2;
    private static final float MAX_BLOCKED_DURATION = 1;
    private static final float MAX_REVERSE_DURATION = 0.5f;
    private static final int MAX_FORWARD_WAYPOINTS = 2;
    // How much of the vehicle width to move the target to avoid a mine
    private static final float AVOIDANCE_FACTOR = 2;

    private enum State {
        NORMAL,
        BLOCKED,
    }

    private static class Target {
        static final float MIN_SCORE = -Float.MIN_VALUE;
        static final float NO_OBSTACLES = 1f;
        final Vector2 position = new Vector2();
        float score = MIN_SCORE;

        public void reset() {
            score = MIN_SCORE;
        }

        public void set(Vector2 position, float score) {
            this.position.set(position);
            this.score = score;
        }
    }

    private final Vector2 mHalfWidth = new Vector2();
    private final Vector2 mTmpVector1 = new Vector2();
    private final Vector2 mTmpVector2 = new Vector2();

    private final GameWorld mGameWorld;
    private final Track mTrack;
    private final Racer mRacer;

    private final ClosestBodyFinder mClosestBodyFinder;
    private final MaterialChecker mMaterialChecker;

    private State mState = State.NORMAL;
    private float mBlockedDuration = 0;
    private float mReverseDuration = 0;

    private final Target mTarget = new Target();
    private final Target mNextTarget = new Target();

    public AIPilot(GameWorld gameWorld, Track track, Racer racer) {
        mGameWorld = gameWorld;
        mTrack = track;
        mRacer = racer;
        mClosestBodyFinder =
                new ClosestBodyFinder(
                        body -> {
                            if (BodyIdentifier.isStaticObstacle(body)) {
                                return true;
                            }
                            return BodyIdentifier.isOtherVehicle(body, mRacer);
                        });

        mMaterialChecker = new MaterialChecker(track);
    }

    Vector2 getTargetPosition() {
        return mTarget.position;
    }

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

    private static final GameStats sDummyGameStats =
            new GameStats() {
                @Override
                public void setListener(Listener listener) {}

                @Override
                public TrackStats getTrackStats(Track track) {
                    return null;
                }

                @Override
                public int getBestChampionshipRank(Championship championship) {
                    return 0;
                }

                @Override
                public void onChampionshipFinished(Championship championship, int rank) {}

                @Override
                public void recordEvent(Event event) {}

                @Override
                public void recordIntEvent(Event event, int value) {}

                @Override
                public int getEventCount(Event event) {
                    return 0;
                }

                @Override
                public void save() {}
            };

    @Override
    public GameStats getGameStats() {
        return sDummyGameStats;
    }

    private void actNormal(float dt) {
        // update mHalfWidth
        Vehicle vehicle = mRacer.getVehicle();
        mHalfWidth.set(0, vehicle.getHeight() / 2).rotateDeg(vehicle.getAngle());

        updateAcceleration();
        updateDirection();
        if (mState == State.BLOCKED) {
            return;
        }
        float speed = mRacer.getVehicle().getSpeed();
        if (mGameWorld.getState() == GameWorld.State.RUNNING && speed < MIN_NORMAL_SPEED) {
            mBlockedDuration += dt;
            if (mBlockedDuration > MAX_BLOCKED_DURATION) {
                switchToBlocked();
            }
        } else {
            mBlockedDuration = 0;
        }
    }

    private void switchToBlocked() {
        mState = State.BLOCKED;
        mReverseDuration = 0;
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
    }

    private void updateDirection() {
        Target target = findBestTarget();
        if (target == null) {
            switchToBlocked();
            return;
        }
        float targetAngle = mTmpVector1.set(target.position).sub(mRacer.getPosition()).angleDeg();
        targetAngle = AgcMathUtils.normalizeAngle(targetAngle);

        Vehicle vehicle = mRacer.getVehicle();
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

    private Target findBestTarget() {
        float lapDistance = mRacer.getLapPositionComponent().getLapDistance();
        WaypointStore store = mTrack.getWaypointStore();

        // Start at the previous index, as a fallback in case the next waypoints are not visible
        int index = store.getPreviousIndex(store.getWaypointIndex(lapDistance));
        mTarget.reset();
        for (int i = -1; i < MAX_FORWARD_WAYPOINTS; ++i, index = store.getNextIndex(index)) {
            mNextTarget.position.set(store.getWaypoint(index));
            mNextTarget.score = (float) i;
            updateNextTarget();
            if (mNextTarget.score > mTarget.score) {
                mTarget.set(mNextTarget.position, mNextTarget.score);
            }
        }

        if (mTarget.score <= Target.MIN_SCORE) {
            return null;
        }
        return mTarget;
    }

    private void updateNextTarget() {
        Vector2 position = mTmpVector1;
        Vector2 adjustedTargetPos = mTmpVector2;

        // Check there is nothing on a line from the right side of the vehicle to the
        // similarly-offset right side of the target
        position.set(mRacer.getPosition()).add(mHalfWidth);
        adjustedTargetPos.set(mNextTarget.position).add(mHalfWidth);
        if (!checkClearLine(position, adjustedTargetPos, -AVOIDANCE_FACTOR)) {
            mNextTarget.reset();
            return;
        }

        // Same thing on the left
        position.set(mRacer.getPosition()).sub(mHalfWidth);
        adjustedTargetPos.set(mNextTarget.position).sub(mHalfWidth);
        if (!checkClearLine(position, adjustedTargetPos, AVOIDANCE_FACTOR)) {
            mNextTarget.reset();
            return;
        }

        // Weight score with the type of material between vehicle and target
        Material material =
                mMaterialChecker.getSlowestMaterialAhead(
                        mRacer.getPosition(), mNextTarget.position);
        if (material.isHole()) {
            mNextTarget.reset();
            return;
        }

        // Nothing between vehicle and target
        mNextTarget.score += Target.NO_OBSTACLES * material.getSpeed();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean checkClearLine(
            Vector2 position, Vector2 adjustedTargetPos, float avoidanceFactor) {
        World world = mGameWorld.getBox2DWorld();
        Body body = mClosestBodyFinder.find(world, position, adjustedTargetPos);
        if (body == null) {
            return true;
        }
        if (BodyIdentifier.isWall(body)) {
            return false;
        }

        // An obstacle we can avoid
        float dx = 2 * mHalfWidth.x * avoidanceFactor;
        float dy = 2 * mHalfWidth.y * avoidanceFactor;
        mNextTarget.position.set(body.getPosition()).add(dx, dy);
        return true;
    }

    private void handleBonus(float dt) {
        Bonus bonus = mRacer.getBonus();
        if (bonus != null) {
            bonus.aiAct(dt);
        }
    }
}
