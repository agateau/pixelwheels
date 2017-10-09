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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Handles collisions
 */
public class GroundCollisionHandlerComponent implements Racer.Component {
    private static final float LIFTING_DELAY = 0.5f;
    private static final float MAX_RECOVERING_SPEED = 20;
    private static final float MAX_RECOVERING_ROTATION_SPEED = 720;

    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final Vehicle mVehicle;
    private final LapPositionComponent mLapPositionComponent;
    private final MapInfo mMapInfo;
    private final Racer mRacer;
    private OrientedPoint mDropPoint;
    private final Vector2 mVelocity = new Vector2();
    private Helicopter mHelicopter = null;

    public enum State {
        NORMAL,
        FALLING,
        LIFTING,
        RECOVERING,
        DROPPING
    }

    private State mState = State.NORMAL;
    private float mTime;

    public GroundCollisionHandlerComponent(Assets assets, GameWorld gameWorld, Racer racer, LapPositionComponent lapPositionComponent) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mRacer = racer;
        mVehicle = racer.getVehicle();
        mMapInfo = gameWorld.getMapInfo();
        mLapPositionComponent = lapPositionComponent;
    }

    public State getState() {
        return mState;
    }

    @Override
    public void act(float delta) {
        switch (mState) {
        case NORMAL:
            actNormal();
            break;
        case FALLING:
            actFalling(delta);
            break;
        case LIFTING:
            actLifting(delta);
            break;
        case RECOVERING:
            actRecovering(delta);
            break;
        case DROPPING:
            actDropping(delta);
            break;
        }
    }

    private void actNormal() {
        if (mGameWorld.getMapInfo().getMaterialAt(mVehicle.getPosition()).isHole()) {
            startFalling();
        }
    }

    private void startFalling() {
        mHelicopter = Helicopter.create(mAssets, mGameWorld.getMapInfo(), mVehicle.getPosition(), mVehicle.getAngle());
        mGameWorld.addGameObject(mHelicopter);
        mState = State.FALLING;
        mTime = 0;
        mRacer.looseBonus();
        mVehicle.setStopped(true);
    }

    private void actFalling(float delta) {
        if (mTime < LIFTING_DELAY) {
            mTime += delta;
        }
        mVehicle.setZ(-mTime / LIFTING_DELAY / 10);
        mHelicopter.setEndPosition(mVehicle.getPosition());
        if (mHelicopter.isReadyToRecover()) {
            mState = State.LIFTING;
            mTime = 0;
        }
    }

    private void actLifting(float delta) {
        mTime += delta;
        if (mTime >= LIFTING_DELAY) {
            mTime = LIFTING_DELAY;
            startRecovering();
        }
        mVehicle.setZ(Interpolation.pow2.apply(mTime / LIFTING_DELAY));
    }

    private void startRecovering() {
        mState = State.RECOVERING;
        float distance = mLapPositionComponent.getLapDistance();
        mDropPoint = mMapInfo.getValidPosition(mVehicle.getBody().getWorldCenter(), distance);
    }

    private void actRecovering(float delta) {
        final float POSITION_TOLERANCE = 0.1f;
        final float ANGLE_TOLERANCE = MathUtils.degreesToRadians;

        mVelocity.set(mDropPoint.x, mDropPoint.y).sub(mVehicle.getBody().getPosition()).scl(1 / delta);
        float speed = mVelocity.len();
        if (speed > MAX_RECOVERING_SPEED) {
            mVelocity.scl(MAX_RECOVERING_SPEED / speed);
        }

        float angularVelocity = MathUtils.clamp((mDropPoint.angle - mVehicle.getAngle()) / delta,
                -MAX_RECOVERING_ROTATION_SPEED, MAX_RECOVERING_ROTATION_SPEED) * MathUtils.degreesToRadians;

        boolean posOK = MathUtils.isZero(speed * delta, POSITION_TOLERANCE);
        boolean angleOK = MathUtils.isZero(angularVelocity * delta, ANGLE_TOLERANCE);

        if (posOK) {
            mVehicle.getBody().setLinearVelocity(0, 0);
            mVehicle.getBody().setAngularVelocity(0);
            mState = State.DROPPING;
            mTime = 0;
        } else {
            mVehicle.getBody().setLinearVelocity(mVelocity);
            mVehicle.getBody().setAngularVelocity(angleOK ? 0 : angularVelocity);
            mHelicopter.setPosition(mVehicle.getPosition());
            mHelicopter.setAngle(mVehicle.getAngle());
        }
    }

    private void actDropping(float delta) {
        mTime += delta;
        mVehicle.setZ(Interpolation.bounceOut.apply(1, 0, mTime / LIFTING_DELAY));
        if (mTime >= LIFTING_DELAY) {
            mTime = LIFTING_DELAY;
            mHelicopter.leave();
            mVehicle.setZ(0);
            mVehicle.setStopped(false);
            mState = State.NORMAL;
        }
    }
}
