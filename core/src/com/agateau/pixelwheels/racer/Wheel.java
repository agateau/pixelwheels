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
import com.agateau.pixelwheels.debug.Debug;
import com.agateau.pixelwheels.map.Material;
import com.agateau.pixelwheels.utils.Box2DUtils;
import com.agateau.utils.CircularArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Disposable;

/** A wheel */
public class Wheel implements Disposable {
    private static final float DRIFT_IMPULSE_REDUCTION =
            0.5f; // Limit how much of the lateral velocity is killed when drifting
    private static final float DRAG_FACTOR = 1;
    private static final int SKIDMARK_INTERVAL = 3;
    private static final float SKIDMARK_LIFETIME = 10f;

    public static class Skidmark {
        private final Vector2 mPos = new Vector2();
        private boolean mIsEnd = false;
        private float mRemainingLife;

        public boolean isEnd() {
            return mIsEnd;
        }

        public Vector2 getPos() {
            return mPos;
        }

        public void init(Vector2 pos) {
            mPos.set(pos);
            mRemainingLife = SKIDMARK_LIFETIME;
        }

        public void initAsEnd() {
            mIsEnd = true;
        }

        public void act(float delta) {
            mRemainingLife = Math.max(0, mRemainingLife - delta);
        }

        public float getOpacity() {
            return mRemainingLife / SKIDMARK_LIFETIME;
        }
    }

    private final CircularArray<Skidmark> mSkidmarks =
            new CircularArray<Skidmark>(Debug.instance.maxSkidmarks) {
                @Override
                protected Skidmark createInstance() {
                    return new Skidmark();
                }
            };
    private int mSkidmarkCount = 0; // Used to limit the number of skidmarks created

    private final Body mBody;
    private final GameWorld mGameWorld;
    private final TextureRegion mRegion;
    private final Vehicle mVehicle;
    private boolean mCanDrift = false;
    private float mMaxDrivingForce = GamePlay.instance.maxDrivingForce;
    private boolean mGripEnabled = true;
    private Material mMaterial = Material.ROAD;
    private boolean mDrifting = false;

    public Wheel(
            GameWorld gameWorld,
            Vehicle vehicle,
            TextureRegion region,
            float posX,
            float posY,
            float angle) {
        mGameWorld = gameWorld;
        mVehicle = vehicle;
        mRegion = region;

        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(posX, posY);
        bodyDef.angle = angle * MathUtils.degreesToRadians;
        mBody = mGameWorld.getBox2DWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.set(Box2DUtils.createOctogon(w, h, w / 4, w / 4));
        mBody.createFixture(shape, 2f);
        shape.dispose();
    }

    public TextureRegion getRegion() {
        return mRegion;
    }

    public boolean isDrifting() {
        return mDrifting;
    }

    @Override
    public void dispose() {
        mGameWorld.getBox2DWorld().destroyBody(mBody);
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
        for (int idx = mSkidmarks.getBeginIndex(), end = mSkidmarks.getEndIndex();
                idx != end;
                idx = mSkidmarks.getNextIndex(idx)) {
            mSkidmarks.get(idx).act(delta);
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

        final float limit =
                1 - 0.2f * Interpolation.sineOut.apply(currentSpeed / GamePlay.instance.maxSpeed);
        amount *= limit;

        float force = mMaxDrivingForce * amount;
        float angle = mBody.getAngle();
        Vector2 pos = mBody.getWorldCenter();
        mBody.applyForce(
                force * MathUtils.cos(angle), force * MathUtils.sin(angle), pos.x, pos.y, true);
    }

    public long getCellId() {
        return mGameWorld
                .getTrack()
                .getCellIdAt(mBody.getWorldCenter().x, mBody.getWorldCenter().y);
    }

    private void updateFriction() {
        // Kill lateral velocity
        Vector2 impulse =
                Box2DUtils.getLateralVelocity(mBody).scl(-mBody.getMass()).scl(mMaterial.getGrip());
        float maxImpulse =
                (float) GamePlay.instance.maxLateralImpulse / (mVehicle.isBraking() ? 0.2f : 1);
        if (mMaterial != Material.ICE && mCanDrift && impulse.len() > maxImpulse) {
            // Drift
            mDrifting = true;
            if (mSkidmarkCount == 0) {
                mSkidmarks.add().init(mBody.getWorldCenter());
            }
            mSkidmarkCount = (mSkidmarkCount + 1) % SKIDMARK_INTERVAL;
            maxImpulse = Math.max(maxImpulse, impulse.len() - DRIFT_IMPULSE_REDUCTION);
            impulse.limit(maxImpulse);
        } else if (mDrifting) {
            mSkidmarks.add().initAsEnd();
            mDrifting = false;
        }
        mBody.applyLinearImpulse(impulse, mBody.getWorldCenter(), true);

        // Kill angular velocity
        mBody.applyAngularImpulse(0.1f * mBody.getInertia() * -mBody.getAngularVelocity(), true);
    }

    private void updateGroundInfo() {
        if (mVehicle.isFlying()) {
            mMaterial = Material.AIR;
            return;
        }
        mMaterial = mGameWorld.getTrack().getMaterialAt(mBody.getWorldCenter());
    }

    public void setCanDrift(boolean canDrift) {
        mCanDrift = canDrift;
    }

    public void setMaxDrivingForce(float maxDrivingForce) {
        mMaxDrivingForce = maxDrivingForce;
    }

    public CircularArray<Skidmark> getSkidmarks() {
        return mSkidmarks;
    }

    public Material getMaterial() {
        return mMaterial;
    }
}
