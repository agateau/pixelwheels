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

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.racescreen.Helicopter;
import com.agateau.pixelwheels.utils.OrientedPoint;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/** Handles falling in holes */
public class HoleHandlerComponent implements Racer.Component {
    private static final float LIFTING_DELAY = 0.5f;
    private static final float MAX_RECOVERING_SPEED = 20;
    private static final float MAX_RECOVERING_ROTATION_SPEED = 720;

    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final Vehicle mVehicle;
    private final LapPositionComponent mLapPositionComponent;
    private final Track mTrack;
    private final Racer mRacer;
    private OrientedPoint mDropPoint;
    private final Vector2 mVelocity = new Vector2();
    private Helicopter mHelicopter = null;

    public enum State {
        NORMAL,
        FALLING, // Falling in a hole/water
        CLIMBING, // Getting out of the hole/water before needing the helicopter
        LIFTING, // Getting lifted by the helicopter
        RECOVERING, // Carried by the helicopter
        DROPPING // Dropping from the helicopter
    }

    private State mState = State.NORMAL;
    private float mTime;

    public HoleHandlerComponent(
            Assets assets,
            GameWorld gameWorld,
            Racer racer,
            LapPositionComponent lapPositionComponent) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mRacer = racer;
        mVehicle = racer.getVehicle();
        mTrack = gameWorld.getTrack();
        mLapPositionComponent = lapPositionComponent;
    }

    public State getState() {
        return mState;
    }

    public Vehicle getVehicle() {
        return mVehicle;
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
            case CLIMBING:
                actClimbing(delta);
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
        if (isInHole()) {
            switchToFallingState();
        }
    }

    private void switchToFallingState() {
        mHelicopter =
                Helicopter.create(mAssets, mRacer.getAudioManager(), mGameWorld.getTrack(), this);
        mGameWorld.addGameObject(mHelicopter);
        mState = State.FALLING;
        mTime = 0;
    }

    private void actFalling(float delta) {
        mTime = Math.min(mTime + delta, LIFTING_DELAY);
        mVehicle.setZ(-mTime / LIFTING_DELAY / 10);

        if (!isInHole()) {
            switchToClimbingState();
            return;
        }

        if (mHelicopter.isReadyToRecover()) {
            mState = State.LIFTING;
            mTime = 0;
            mVehicle.setStopped(true);
            mRacer.looseBonus();
        }
    }

    private void switchToClimbingState() {
        mState = State.CLIMBING;
    }

    private void actClimbing(float delta) {
        mTime = Math.max(mTime - delta, 0);
        mVehicle.setZ(-mTime / LIFTING_DELAY / 10);

        if (mTime == 0) {
            switchToNormalState();
            return;
        }

        if (isInHole()) {
            switchToFallingState();
        }
    }

    private void actLifting(float delta) {
        mTime += delta;
        if (mTime >= LIFTING_DELAY) {
            mTime = LIFTING_DELAY;
            switchToRecoveringState();
        }
        mVehicle.setZ(Interpolation.pow2.apply(mTime / LIFTING_DELAY));
    }

    private void switchToRecoveringState() {
        mState = State.RECOVERING;
        float distance = mLapPositionComponent.getLapDistance();
        mDropPoint = mTrack.getValidPosition(mVehicle.getBody().getWorldCenter(), distance);
    }

    private void actRecovering(float delta) {
        final float POSITION_TOLERANCE = 0.1f;
        final float ANGLE_TOLERANCE = MathUtils.degreesToRadians;

        mVelocity
                .set(mDropPoint.x, mDropPoint.y)
                .sub(mVehicle.getBody().getPosition())
                .scl(1 / delta);
        float speed = mVelocity.len();
        if (speed > MAX_RECOVERING_SPEED) {
            mVelocity.scl(MAX_RECOVERING_SPEED / speed);
        }

        float angularVelocity =
                MathUtils.clamp(
                                (mDropPoint.angle - mVehicle.getAngle()) / delta,
                                -MAX_RECOVERING_ROTATION_SPEED,
                                MAX_RECOVERING_ROTATION_SPEED)
                        * MathUtils.degreesToRadians;

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
            switchToNormalState();
        }
    }

    private void switchToNormalState() {
        mVehicle.setZ(0);
        mVehicle.setStopped(false);
        mState = State.NORMAL;
    }

    public boolean isInHole() {
        return mGameWorld.getTrack().getMaterialAt(mVehicle.getPosition()).isHole();
    }
}
