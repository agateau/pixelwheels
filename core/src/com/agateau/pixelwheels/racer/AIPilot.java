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

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.bonus.Bonus;
import com.agateau.pixelwheels.debug.Debug;
import com.agateau.pixelwheels.debug.DebugShapeMap;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.map.WaypointStore;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.stats.TrackStats;
import com.agateau.pixelwheels.utils.DrawUtils;
import com.agateau.pixelwheels.utils.StaticBodyFinder;
import com.agateau.utils.AgcMathUtils;
import com.agateau.utils.Line;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/** An AI pilot */
public class AIPilot implements Pilot {
    private static final float MIN_NORMAL_SPEED = 1;
    private static final float MAX_BLOCKED_DURATION = 1;
    private static final float MAX_REVERSE_DURATION = 0.5f;
    private static final int MAX_FORWARD_WAYPOINTS = 2;

    private final Vector2 mTmpVector1 = new Vector2();
    private final Vector2 mTmpVector2 = new Vector2();
    private final Vector2 mTmpVector3 = new Vector2();

    private final GameWorld mGameWorld;
    private final Track mTrack;
    private final Racer mRacer;
    private final StaticBodyFinder mStaticBodyFinder = new StaticBodyFinder();

    private enum State {
        NORMAL,
        BLOCKED,
    }

    private State mState = State.NORMAL;
    private float mBlockedDuration = 0;
    private float mReverseDuration = 0;

    private static class DebugInfo {
        private final Line[] lines;

        DebugInfo() {
            lines = new Line[] {new Line(), new Line()};
        }

        void setLine(int idx, Vector2 p1, Vector2 p2) {
            lines[idx].set(p1, p2);
        }

        void drawLine(ShapeRenderer renderer, int idx) {
            Line line = lines[idx];
            renderer.line(line.p1, line.p2);
            DrawUtils.drawCross(renderer, line.p2, 12 * Constants.UNIT_FOR_PIXEL);
        }
    }

    private DebugInfo mDebugInfo;

    public AIPilot(GameWorld gameWorld, Track track, Racer racer) {
        mGameWorld = gameWorld;
        mTrack = track;
        mRacer = racer;

        DebugShapeMap.Shape debugShape =
                renderer -> {
                    renderer.begin(ShapeRenderer.ShapeType.Line);
                    renderer.setColor(1, 0, 1, 1);
                    mDebugInfo.drawLine(renderer, 0);
                    mDebugInfo.drawLine(renderer, 1);
                    renderer.end();
                };
        DebugShapeMap.getMap().put(this, debugShape);
    }

    @Override
    public void act(float dt) {
        if (Debug.instance.showDebugLayer && mDebugInfo == null) {
            mDebugInfo = new DebugInfo();
        }
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
        Vector2 waypoint = findNextWaypoint();
        mTmpVector1.set(waypoint).sub(mRacer.getPosition());

        Vehicle vehicle = mRacer.getVehicle();
        float targetAngle = AgcMathUtils.normalizeAngle(mTmpVector1.angle());
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

    private Vector2 findNextWaypoint() {
        float lapDistance = mRacer.getLapPositionComponent().getLapDistance();
        WaypointStore store = mTrack.getWaypointStore();
        int index = store.getWaypointIndex(lapDistance);
        Vector2 waypoint = store.getWaypoint(index);
        if (!isWaypointVisible(waypoint)) {
            NLog.e("Current waypoint is not visible, we might get blocked");
            return waypoint;
        }
        Vector2 nextWaypoint;
        for (int i = 0; i < MAX_FORWARD_WAYPOINTS; ++i) {
            index = store.getNextIndex(index);
            nextWaypoint = store.getWaypoint(index);
            if (!isWaypointVisible(nextWaypoint)) {
                break;
            }
            waypoint = nextWaypoint;
        }
        return waypoint;
    }

    /** Check if both sides of the vehicle can "see" the waypoint */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isWaypointVisible(Vector2 waypoint) {
        World world = mGameWorld.getBox2DWorld();
        Vector2 delta = mTmpVector1;
        Vector2 position = mTmpVector2;
        Vector2 adjustedWaypoint = mTmpVector3;

        Vehicle vehicle = mRacer.getVehicle();
        delta.set(0, vehicle.getHeight() / 2).rotate(vehicle.getAngle());

        position.set(mRacer.getPosition()).add(delta);
        adjustedWaypoint.set(waypoint).add(delta);
        if (mStaticBodyFinder.find(world, position, adjustedWaypoint) != null) {
            return false;
        }
        if (mDebugInfo != null) {
            mDebugInfo.setLine(0, position, adjustedWaypoint);
        }

        delta.scl(-1);
        position.set(mRacer.getPosition()).add(delta);
        adjustedWaypoint.set(waypoint).add(delta);
        if (mDebugInfo != null) {
            mDebugInfo.setLine(1, position, adjustedWaypoint);
        }
        return mStaticBodyFinder.find(world, position, adjustedWaypoint) == null;
    }

    private void handleBonus(float dt) {
        Bonus bonus = mRacer.getBonus();
        if (bonus != null) {
            bonus.aiAct(dt);
        }
    }
}
