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

import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.racescreen.Collidable;
import com.agateau.pixelwheels.racescreen.CollisionCategories;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/** Make a vehicle spin on itself for a full circle */
public class SpinningComponent implements Racer.Component, Collidable {
    private static final float MIN_ANGULAR_VELOCITY = 1f;
    private static final float MAX_ANGULAR_VELOCITY = 15f;
    private final Vehicle mVehicle;
    private boolean mActive = false;
    private float mOriginalAngle;
    private float mTargetBodyAngle;

    public SpinningComponent(Vehicle vehicle) {
        mVehicle = vehicle;
    }

    public boolean isActive() {
        return mActive;
    }

    public float getOriginalAngle() {
        return mOriginalAngle;
    }

    public void start() {
        mActive = true;
        setGripEnabled(false);
        mOriginalAngle = mVehicle.getAngle();
        mTargetBodyAngle = mVehicle.getBody().getAngle() + 2 * MathUtils.PI;
    }

    @Override
    public void act(float delta) {
        if (!mActive) {
            return;
        }
        Body body = mVehicle.getBody();

        // Slow down
        body.applyLinearImpulse(
                body.getLinearVelocity().nor().scl(-body.getMass()), body.getWorldCenter(), true);

        // Spin
        float nextAngle = body.getAngle() + body.getAngularVelocity() * GameWorld.BOX2D_TIME_STEP;
        if (nextAngle > mTargetBodyAngle) {
            stopSpinning();
            return;
        }

        float totalRotation = mTargetBodyAngle - nextAngle;
        float desiredAngularVelocity = totalRotation / GameWorld.BOX2D_TIME_STEP;
        if (desiredAngularVelocity < 0) {
            desiredAngularVelocity =
                    MathUtils.clamp(
                            desiredAngularVelocity, -MAX_ANGULAR_VELOCITY, -MIN_ANGULAR_VELOCITY);
        } else {
            desiredAngularVelocity =
                    MathUtils.clamp(
                            desiredAngularVelocity, MIN_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);
        }
        float impulse = body.getInertia() * (desiredAngularVelocity - body.getAngularVelocity());
        body.applyAngularImpulse(impulse, true);
    }

    private void stopSpinning() {
        mActive = false;
        setGripEnabled(true);
    }

    private void setGripEnabled(boolean enabled) {
        for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
            info.wheel.setGripEnabled(enabled);
        }
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        if (!mActive) {
            return;
        }
        // If we hit something, stop spinning: we may not be able to do a full circle at all if we
        // are blocked by a wall
        if ((otherFixture.getFilterData().categoryBits & CollisionCategories.SOLID_BODIES) != 0) {
            stopSpinning();
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {}

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {}
}
