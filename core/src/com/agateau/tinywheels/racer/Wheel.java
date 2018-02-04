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
package com.agateau.tinywheels.racer;

import com.agateau.tinywheels.Box2DUtils;
import com.agateau.tinywheels.Constants;
import com.agateau.tinywheels.GamePlay;
import com.agateau.tinywheels.GameWorld;
import com.agateau.tinywheels.Material;
import com.agateau.utils.CircularArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * A wheel
 */
public class Wheel implements Pool.Poolable, Disposable {
    private static final float DRIFT_IMPULSE_REDUCTION = 0.5f; // Limit how much of the lateral velocity is killed when drifting
    private static final float DRAG_FACTOR = 1;
    private static final int SKIDMARK_INTERVAL = 3;

    public static final Vector2 END_DRIFT_POS = new Vector2(-12, -12);

    private static ReflectionPool<Wheel> sPool = new ReflectionPool<Wheel>(Wheel.class);

    private final CircularArray<Vector2> mSkidmarks = new CircularArray<Vector2>(GamePlay.instance.maxSkidmarks) {
        @Override
        protected Vector2 initElement(Vector2 existingElement, Vector2 newElement) {
            if (existingElement == null) {
                existingElement = new Vector2();
            }
            existingElement.set(newElement.x, newElement.y);
            return existingElement;
        }
    };

    private Body mBody;
    private GameWorld mGameWorld;
    private TextureRegion mRegion;
    private Vehicle mVehicle;
    private boolean mCanDrift;
    private float mMaxDrivingForce;
    private boolean mGripEnabled = true;
    private Material mMaterial;
    private boolean mDrifting = false;

    public static Wheel create(GameWorld gameWorld, Vehicle vehicle, TextureRegion region, float posX, float posY, float angle) {
        Wheel obj = sPool.obtain();
        obj.mGameWorld = gameWorld;
        obj.mVehicle = vehicle;
        obj.mRegion = region;
        obj.mCanDrift = false;
        obj.mMaxDrivingForce = GamePlay.instance.maxDrivingForce;
        obj.mMaterial = Material.ROAD;

        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(posX, posY);
        bodyDef.angle = angle * MathUtils.degreesToRadians;
        obj.mBody = obj.mGameWorld.getBox2DWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.set(Box2DUtils.createOctogon(w, h, w / 4, w / 4));
        obj.mBody.createFixture(shape, 2f);
        shape.dispose();

        return obj;
    }

    public TextureRegion getRegion() {
        return mRegion;
    }

    public boolean isDrifting() {
        return mDrifting;
    }

    @Override
    public void dispose() {
        sPool.free(this);
    }

    @Override
    public void reset() {
        mGameWorld.getBox2DWorld().destroyBody(mBody);
        mVehicle = null;
    }

    @SuppressWarnings("UnusedParameters")
    public void act(float delta) {
        updateGroundInfo();
        if (!mVehicle.isFlying()) {
            if (mGripEnabled) {
                updateFriction();
            }
            Box2DUtils.applyDrag(mBody, DRAG_FACTOR);
        }
    }

    public Body getBody() {
        return mBody;
    }

    public float getGroundSpeed() {
        return mMaterial.getSpeed();
    }

    public void setGripEnabled(boolean enabled) {
        mGripEnabled = enabled;
    }

    public void adjustSpeed(float amount) {
        if (amount == 0) {
            return;
        }
        final float currentSpeed = mBody.getLinearVelocity().len() * Box2DUtils.MS_TO_KMH;

        final float limit = 1 - 0.2f * Interpolation.sineOut.apply(currentSpeed / GamePlay.instance.maxSpeed);
        amount *= limit;

        float force = mMaxDrivingForce * amount;
        float angle = mBody.getAngle();
        Vector2 pos = mBody.getWorldCenter();
        mBody.applyForce(force * MathUtils.cos(angle), force * MathUtils.sin(angle), pos.x, pos.y, true);
    }

    public long getCellId() {
        return mGameWorld.getMapInfo().getCellIdAt(mBody.getWorldCenter().x, mBody.getWorldCenter().y);
    }

    private int mSkidmarkCount = 0; // Used to limit the number of skidmarks created
    private void updateFriction() {
        // Kill lateral velocity
        Vector2 impulse = Box2DUtils.getLateralVelocity(mBody).scl(-mBody.getMass());
        float maxImpulse = (float)GamePlay.instance.maxLateralImpulse / (mVehicle.isBraking() ? 2 : 1);
        if (mCanDrift && impulse.len() > maxImpulse) {
            // Drift
            if (!mDrifting) {
                mDrifting = true;
            }
            if (mSkidmarkCount == 0) {
                mSkidmarks.add(mBody.getWorldCenter());
            }
            mSkidmarkCount = (mSkidmarkCount + 1) % SKIDMARK_INTERVAL;
            maxImpulse = Math.max(maxImpulse, impulse.len() - DRIFT_IMPULSE_REDUCTION);
            impulse.limit(maxImpulse);
        } else if (mDrifting) {
            mSkidmarks.add(END_DRIFT_POS);
            mDrifting = false;
        }
        mBody.applyLinearImpulse(impulse, mBody.getWorldCenter(), true);

        // Kill angular velocity
        mBody.applyAngularImpulse(0.1f * mBody.getInertia() * -mBody.getAngularVelocity(), true);
    }

    private void updateGroundInfo() {
        if (mVehicle.isFlying()) {
            mMaterial = Material.AIR;
        } else {
            mMaterial = mGameWorld.getMapInfo().getMaterialAt(mBody.getWorldCenter());
        }
    }

    public void setCanDrift(boolean canDrift) {
        mCanDrift = canDrift;
    }

    public void setMaxDrivingForce(float maxDrivingForce) {
        mMaxDrivingForce = maxDrivingForce;
    }

    public CircularArray<Vector2> getSkidmarks() {
        return mSkidmarks;
    }

    public Material getMaterial() {
        return mMaterial;
    }
}
