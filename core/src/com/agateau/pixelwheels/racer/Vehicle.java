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
import com.agateau.pixelwheels.TextureRegionProvider;
import com.agateau.pixelwheels.map.Material;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.utils.Box2DUtils;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

/** Represents a car on the world */
public class Vehicle implements Racer.Component, Disposable {
    private static final float ACCELERATION_DELTA = 1;
    private static final float BRAKING_DELTA = 0.8f;
    // If the angle in degrees between body and velocity is more than this
    // and we are on ice, consider that we are "ice drifting"
    private static final float MIN_ICE_DRIFT_ANGLE = 5;
    private static final float MIN_ICE_DRIFT_SPEED = 4;

    // Move center of gravity that much percent forward
    private static final float CENTER_OF_GRAVITY_SHIFT_PERCENT = 0.5f;

    public static class WheelInfo {
        public Wheel wheel;
        public RevoluteJoint joint;
        public float steeringFactor;
    }

    private final String mId;
    private final Body mBody;
    private final GameWorld mGameWorld;
    private Racer mRacer;

    private final Animation<TextureRegion> mBodyAnimation;
    private final Array<WheelInfo> mWheels = new Array<>();

    private int mCollisionCategoryBits;
    private int mCollisionMaskBits;

    private boolean mAccelerating = false;
    private boolean mBraking = false;
    private float mZ = 0;
    private float mDirection = 0;
    private float mTurboTime = -1;
    private boolean mStopped = false;
    private Material mMaterial = Material.ROAD;
    private float mSpeedLimiter = 1f;
    private boolean mFlying = false;

    private Probe mProbe = null;

    private final ArrayMap<Long, Float> mTurboCellMap = new ArrayMap<>(8);

    private final Array<Long> mTurboCellsUnderWheels = new Array<>();

    public Vehicle(
            TextureRegionProvider textureRegionProvider,
            GameWorld gameWorld,
            float originX,
            float originY,
            VehicleDef vehicleDef,
            float angle) {
        mId = vehicleDef.id;
        mGameWorld = gameWorld;

        // Main
        mBodyAnimation = vehicleDef.getAnimation(textureRegionProvider);
        mBodyAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(originX, originY);
        bodyDef.angle = angle * MathUtils.degreesToRadians;
        mBody = mGameWorld.getBox2DWorld().createBody(bodyDef);

        // Body fixtures
        for (Shape2D shape : vehicleDef.shapes) {
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = Box2DUtils.createBox2DShape(shape, Constants.UNIT_FOR_PIXEL);
            fixtureDef.density = GamePlay.instance.vehicleDensity / 10.0f;
            fixtureDef.friction = 0.2f;
            fixtureDef.restitution = GamePlay.instance.vehicleRestitution / 10.0f;
            mBody.createFixture(fixtureDef);
            fixtureDef.shape.dispose();
        }

        moveCenterOfGravity(vehicleDef, textureRegionProvider);
    }

    private void moveCenterOfGravity(
            VehicleDef vehicleDef, TextureRegionProvider textureRegionProvider) {
        float halfVehicleLength = vehicleDef.getImage(textureRegionProvider).getRegionHeight() / 2f;
        MassData massData = mBody.getMassData();
        massData.center.x +=
                CENTER_OF_GRAVITY_SHIFT_PERCENT * halfVehicleLength * Constants.UNIT_FOR_PIXEL;
        mBody.setMassData(massData);
    }

    @Override
    public void dispose() {
        for (WheelInfo info : mWheels) {
            info.wheel.dispose();
        }
        mGameWorld.getBox2DWorld().destroyBody(mBody);
    }

    public WheelInfo addWheel(
            TextureRegion region,
            Animation<TextureRegion> splashAnimation,
            float density,
            float x,
            float y,
            float angle) {
        WheelInfo info = new WheelInfo();
        info.wheel =
                new Wheel(
                        mGameWorld,
                        this,
                        region,
                        splashAnimation,
                        density,
                        getX() + x,
                        getY() + y,
                        angle);
        mWheels.add(info);

        Body body = info.wheel.getBody();
        body.setUserData(mBody.getUserData());

        RevoluteJointDef jointDef = new RevoluteJointDef();
        // Call initialize() instead of defining bodies and anchors manually. Defining anchors
        // manually
        // causes Box2D to move the car a bit while it solves the constraints defined by the joints
        jointDef.initialize(mBody, body, body.getPosition());
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.enableLimit = true;
        info.joint = (RevoluteJoint) mGameWorld.getBox2DWorld().createJoint(jointDef);

        mTurboCellsUnderWheels.ensureCapacity(mWheels.size);

        return info;
    }

    public void setRacer(Racer racer) {
        mRacer = racer;
        mBody.setUserData(racer);
        for (WheelInfo info : mWheels) {
            info.wheel.getBody().setUserData(racer);
        }
    }

    public void setProbe(Probe probe) {
        mProbe = probe;
    }

    public void setCollisionInfo(int categoryBits, int maskBits) {
        mCollisionCategoryBits = categoryBits;
        mCollisionMaskBits = maskBits;
        applyCollisionInfo();
    }

    public Array<WheelInfo> getWheelInfos() {
        return mWheels;
    }

    public String getId() {
        return mId;
    }

    public Body getBody() {
        return mBody;
    }

    public TextureRegion getRegion(float time) {
        return mBodyAnimation.getKeyFrame(time);
    }

    public float getSpeed() {
        return mBody.getLinearVelocity().len();
    }

    public boolean isDrifting() {
        for (WheelInfo wheelInfo : mWheels) {
            if (wheelInfo.wheel.isDrifting()) {
                return true;
            }
        }
        return false;
    }

    public boolean isIceDrifting() {
        if (getSpeed() < MIN_ICE_DRIFT_SPEED) {
            return false;
        }
        for (WheelInfo wheelInfo : mWheels) {
            if (wheelInfo.wheel.getMaterial() == Material.ICE) {
                float delta =
                        AgcMathUtils.angleDelta(mBody.getLinearVelocity().angleDeg(), getAngle());
                return Math.abs(delta % 180) > MIN_ICE_DRIFT_ANGLE;
            }
        }
        return false;
    }

    boolean isOnWater() {
        for (WheelInfo wheelInfo : mWheels) {
            if (wheelInfo.wheel.getMaterial() == Material.WATER) {
                return true;
            }
        }
        return false;
    }

    /**
     * speedLimiter is a percentage. Set it to 0.9 to make the vehicle drive at 90% of its maximum
     * speed
     */
    public void setSpeedLimiter(float speedLimiter) {
        mSpeedLimiter = speedLimiter;
    }

    public float getSpeedLimiter() {
        return mSpeedLimiter;
    }

    /** Returns the angle the car is facing */
    public float getAngle() {
        return AgcMathUtils.normalizeAngle(mBody.getAngle() * MathUtils.radiansToDegrees);
    }

    public float getWidth() {
        return Constants.UNIT_FOR_PIXEL * getRegion(0).getRegionWidth();
    }

    public float getHeight() {
        return Constants.UNIT_FOR_PIXEL * getRegion(0).getRegionHeight();
    }

    public void setFlying(boolean flying) {
        if (flying == mFlying) {
            // No changes
            return;
        }
        if (flying) {
            // Taking off
            Box2DUtils.setCollisionInfo(mBody, 0, 0);
            for (WheelInfo info : mWheels) {
                Box2DUtils.setCollisionInfo(info.wheel.getBody(), 0, 0);
            }
        } else {
            // Landing
            applyCollisionInfo();
        }
        mFlying = flying;
    }

    public boolean isFlying() {
        return mFlying;
    }

    public boolean isFalling() {
        return mZ < 0;
    }

    public float getZ() {
        return mZ;
    }

    public void setZ(float z) {
        mZ = z;
    }

    /** Call this when the vehicle needs to stop as soon as possible For example because it fell */
    public void setStopped(boolean stopped) {
        if (stopped) {
            mTurboTime = -1;
        }
        mStopped = stopped;
    }

    @Override
    public void act(float dt) {
        if (!isFlying()) {
            if (mStopped) {
                actStopping(dt);
            } else {
                applyTurbo(dt);
                applyPilotCommands();
                applyGroundEffects(dt);
                updateMaterial();
            }
        }
        actWheels(dt);
    }

    private void actStopping(float dt) {
        Vector2 invVelocity = mBody.getLinearVelocity().scl(-0.1f);
        mBody.applyForce(
                invVelocity.scl(mBody.getMass()).scl(1 / dt), mBody.getWorldCenter(), true);
    }

    /**
     * Apply ground effects on the vehicle:
     *
     * <ul>
     *   <li>trigger turbo when driving on turbo tiles
     *   <li>apply drag
     * </ul>
     */
    private void applyGroundEffects(float dt) {
        final GamePlay GP = GamePlay.instance;
        float groundSpeed = 0;
        mTurboCellsUnderWheels.clear();
        for (WheelInfo info : mWheels) {
            float wheelGroundSpeed = info.wheel.getGroundSpeed();
            groundSpeed += wheelGroundSpeed;
            long cellId = info.wheel.getCellId();
            boolean isTurboCell = wheelGroundSpeed > 1;
            if (isTurboCell && !mRacer.isDisrupted()) {
                mTurboCellsUnderWheels.add(cellId);
                if (!alreadyTriggeredTurboCell(cellId)) {
                    triggerTurbo();
                    addTriggeredTurboCell(cellId);
                }
            }
        }
        groundSpeed /= mWheels.size;

        updateTriggeredTurboCells(dt);

        boolean turboOn = mTurboTime > 0;
        if (groundSpeed < 1f && !turboOn) {
            Box2DUtils.applyDrag(mBody, (1 - groundSpeed) * GP.groundDragFactor);
        }
    }

    /** Apply pilot commands to the wheels */
    private void applyPilotCommands() {
        float speedDelta = 0;
        if (mGameWorld.getState() == GameWorld.State.RUNNING) {
            if (mAccelerating) {
                speedDelta = ACCELERATION_DELTA * mSpeedLimiter;
            }
            if (mBraking) {
                speedDelta -= BRAKING_DELTA;
            }
        }

        float steerAngle = computeSteerAngle() * MathUtils.degRad;
        for (WheelInfo info : mWheels) {
            float angle = info.steeringFactor * steerAngle;
            info.wheel.adjustSpeed(speedDelta);
            info.joint.setLimits(angle, angle);
        }
    }

    private void updateMaterial() {
        Material oldMaterial = mMaterial;
        mMaterial = mGameWorld.getTrack().getMaterialAt(mBody.getWorldCenter());
        if (!mMaterial.isRoad() && oldMaterial.isRoad()) {
            mRacer.getGameStats().recordEvent(GameStats.Event.LEAVING_ROAD);
        }
    }

    private void actWheels(float dt) {
        for (WheelInfo info : mWheels) {
            info.wheel.act(dt);
        }
    }

    private final Vector2 mDirectionVector = new Vector2();

    private Vector2 computeDirectionVector(float strength) {
        return mDirectionVector.set(strength, 0).rotateRad(mBody.getAngle());
    }

    private void applyTurbo(float dt) {
        final GamePlay GP = GamePlay.instance;

        if (mTurboTime == 0) {
            mBody.applyLinearImpulse(
                    computeDirectionVector(GP.turboStrength / 6f), mBody.getWorldCenter(), true);
        }
        if (mTurboTime >= 0) {
            mTurboTime += dt;
            mBody.applyForce(
                    computeDirectionVector(GP.turboStrength), mBody.getWorldCenter(), true);
            if (mTurboTime > GP.turboDuration) {
                mTurboTime = -1;
            }
        }
    }

    private float computeSteerAngle() {
        final GamePlay GP = GamePlay.instance;
        if (mDirection == 0) {
            if (mProbe != null) {
                float speed = mBody.getLinearVelocity().len() * Box2DUtils.MS_TO_KMH;
                mProbe.addValue("steer", 0);
                mProbe.addValue("speed", speed);
                mProbe.addValue("category", 0);
            }
            return 0;
        }

        float speed = mBody.getLinearVelocity().len() * Box2DUtils.MS_TO_KMH;
        float steer;
        // Category is 0 if speed is < GP.lowSpeed, 1 if < GP.maxSpeed, 2 if > GP.maxSpeed
        // For a better driving experience, it should not reach 2 except when triggering turbos
        float category;
        if (speed < GP.lowSpeed) {
            steer = MathUtils.lerp(GP.stoppedMaxSteer, GP.lowSpeedMaxSteer, speed / GP.lowSpeed);
            category = 0;
        } else if (speed < GP.maxSpeed) {
            float factor = (speed - GP.lowSpeed) / (GP.maxSpeed - GP.lowSpeed);
            steer = MathUtils.lerp(GP.lowSpeedMaxSteer, GP.highSpeedMaxSteer, factor);
            category = 1;
        } else {
            steer = GP.highSpeedMaxSteer;
            category = 2;
        }
        if (mProbe != null) {
            mProbe.addValue("steer", steer);
            mProbe.addValue("speed", speed);
            mProbe.addValue("category", category);
        }
        return mDirection * steer;
    }

    private boolean alreadyTriggeredTurboCell(long cellId) {
        return mTurboCellMap.containsKey(cellId);
    }

    private void addTriggeredTurboCell(long cellId) {
        mTurboCellMap.put(cellId, GamePlay.instance.turboDuration);
    }

    private void updateTriggeredTurboCells(float delta) {
        for (int idx = mTurboCellMap.size - 1; idx >= 0; --idx) {
            float duration = mTurboCellMap.getValueAt(idx) - delta;
            if (duration <= 0) {
                long cellId = mTurboCellMap.getKeyAt(idx);
                if (mTurboCellsUnderWheels.contains(cellId, false /* identity */)) {
                    // Keep the cell in mTurboCellMap until the vehicle has left it to ensure turbo
                    // is not triggered more than once
                    mTurboCellMap.setValue(idx, 0f);
                } else {
                    mTurboCellMap.removeIndex(idx);
                }
            } else {
                mTurboCellMap.setValue(idx, duration);
            }
        }
    }

    public void setAccelerating(boolean value) {
        mAccelerating = value;
    }

    public void setBraking(boolean value) {
        mBraking = value;
    }

    public boolean isBraking() {
        return mBraking;
    }

    public void setDirection(float direction) {
        mDirection = direction;
    }

    public Vector2 getPosition() {
        return mBody.getPosition();
    }

    public float getX() {
        return mBody.getPosition().x;
    }

    public float getY() {
        return mBody.getPosition().y;
    }

    public float getTurboTime() {
        return mTurboTime;
    }

    public void triggerTurbo() {
        mRacer.getAudioComponent().triggerTurbo();
        mTurboTime = 0;
    }

    private void applyCollisionInfo() {
        Box2DUtils.setCollisionInfo(mBody, mCollisionCategoryBits, mCollisionMaskBits);
        for (WheelInfo info : mWheels) {
            Box2DUtils.setCollisionInfo(
                    info.wheel.getBody(), mCollisionCategoryBits, mCollisionMaskBits);
        }
    }

    @Override
    public String toString() {
        return mId;
    }
}
