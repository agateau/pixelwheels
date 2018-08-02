/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.bonus;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.gameobjet.AnimationObject;
import com.agateau.pixelwheels.gameobjet.AudioClipper;
import com.agateau.pixelwheels.gameobjet.GameObjectAdapter;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racer.Vehicle;
import com.agateau.pixelwheels.racescreen.Collidable;
import com.agateau.pixelwheels.racescreen.CollisionCategories;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.utils.BodyRegionDrawer;
import com.agateau.pixelwheels.utils.Box2DUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * A player bullet
 */
public class Missile extends GameObjectAdapter implements Collidable, Pool.Poolable, Disposable {
    private static final ReflectionPool<Missile> sPool = new ReflectionPool<Missile>(Missile.class);

    private static final float WIDTH = 32;
    private static final float HEIGHT = 6;
    private static final float FORCE = 160;
    private static final float DURATION = 3;

    enum Status {
        WAITING,
        SHOT,
        LOCKED
    }

    // Init-once fields
    private final BodyDef mBodyDef = new BodyDef();
    private final WeldJointDef mJointDef = new WeldJointDef();
    private final PolygonShape mShape = new PolygonShape();
    private final BodyRegionDrawer mDrawer = new BodyRegionDrawer();

    // Init-at-pool-reuse fields
    private Assets mAssets;
    private Racer mShooter;
    private GameWorld mGameWorld;
    private AudioManager mAudioManager;
    private Body mBody;

    // Moving fields
    private float mRemainingTime;
    private Joint mJoint;
    private Status mStatus;
    private boolean mNeedShootSound;

    public Missile() {
        mBodyDef.type = BodyDef.BodyType.DynamicBody;
        mBodyDef.bullet = true;
        mShape.setAsBox(
                WIDTH * Constants.UNIT_FOR_PIXEL / 2,
                HEIGHT * Constants.UNIT_FOR_PIXEL / 2);
    }

    public static Missile create(Assets assets, GameWorld gameWorld, AudioManager audioManager, Racer shooter) {
        NLog.d("");
        Missile object = sPool.obtain();
        object.mAssets = assets;
        object.mGameWorld = gameWorld;
        Vehicle vehicle = shooter.getVehicle();
        object.mShooter = shooter;
        object.mAudioManager = audioManager;
        object.setFinished(false);
        object.mBodyDef.position.set(vehicle.getX(), vehicle.getY());
        object.mBodyDef.angle = vehicle.getAngle() * MathUtils.degRad;

        object.mBody = gameWorld.getBox2DWorld().createBody(object.mBodyDef);
        object.mBody.createFixture(object.mShape, 0.00001f);
        object.mBody.setUserData(object);

        object.mStatus = Status.WAITING;
        object.mNeedShootSound = false;

        Box2DUtils.setCollisionInfo(object.mBody, CollisionCategories.RACER_BULLET,
                CollisionCategories.WALL | CollisionCategories.RACER);

        gameWorld.addGameObject(object);

        object.initJoint();
        return object;
    }

    private void initJoint() {
        Vehicle vehicle = mShooter.getVehicle();
        Body vehicleBody = vehicle.getBody();
        mJointDef.bodyA = vehicleBody;
        mJointDef.bodyB = mBody;
        mJointDef.localAnchorA.set(vehicleBody.getLocalCenter());
        mJointDef.localAnchorB.set(mBody.getLocalCenter());
        mJoint = mGameWorld.getBox2DWorld().createJoint(mJointDef);
    }

    public void shoot() {
        NLog.d("");
        resetJoint();
        mBody.getFixtureList().first().setDensity(1);
        mBody.resetMassData();
        mBody.setAngularVelocity(0);
        mStatus = Status.SHOT;
        mRemainingTime = DURATION;
        mNeedShootSound = true;
    }

    @Override
    public void reset() {
        resetJoint();
        mGameWorld.getBox2DWorld().destroyBody(mBody);
        mBody = null;
    }

    private void resetJoint() {
        if (mJoint != null) {
            mGameWorld.getBox2DWorld().destroyJoint(mJoint);
            mJoint = null;
        }
    }

    @Override
    public void dispose() {
        sPool.free(this);
    }

    @Override
    public void act(float delta) {
        if (mStatus == Status.WAITING) {
            return;
        }
        mBody.applyForce(
                FORCE * MathUtils.cos(mBody.getAngle()), FORCE * MathUtils.sin(mBody.getAngle()),
                mBody.getWorldCenter().x, mBody.getWorldCenter().y, true);
        mRemainingTime -= delta;
        if (mRemainingTime < 0) {
            explode();
        }
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_FLYING) {
            mDrawer.setBatch(batch);
            mDrawer.draw(mBody, mAssets.missile);
        }
    }

    @Override
    public void audioRender(AudioClipper clipper) {
        if (mNeedShootSound) {
            mAudioManager.play(mAssets.soundAtlas.get("shoot"), clipper.clip(this));
            mNeedShootSound = false;
        }
    }

    @Override
    public float getX() {
        return mBody.getPosition().x;
    }

    @Override
    public float getY() {
        return mBody.getPosition().y;
    }

    private void explode() {
        NLog.d("");
        Vector2 pos = mBody.getPosition();
        AnimationObject obj = mAssets.createExplosion(mAudioManager, pos.x, pos.y);
        mGameWorld.addGameObject(obj);
        setFinished(true);
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {
    }

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {
        if (isFinished()) {
            return;
        }
        if (mStatus == Status.WAITING) {
            contact.setEnabled(false);
            return;
        }
        Object other = otherFixture.getBody().getUserData();
        if (other == mShooter) {
            contact.setEnabled(false);
            return;
        }

        explode();
        if (other instanceof Racer) {
            ((Racer)other).spin();
        }
    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {

    }
}
