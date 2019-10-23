/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.bonus;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.debug.DebugShapeMap;
import com.agateau.pixelwheels.gameobjet.AnimationObject;
import com.agateau.pixelwheels.gameobjet.AudioClipper;
import com.agateau.pixelwheels.gameobjet.Explosable;
import com.agateau.pixelwheels.gameobjet.GameObjectAdapter;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racer.Vehicle;
import com.agateau.pixelwheels.racescreen.Collidable;
import com.agateau.pixelwheels.racescreen.CollisionCategories;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.sound.SoundPlayer;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.utils.BodyRegionDrawer;
import com.agateau.pixelwheels.utils.Box2DUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/** A player bullet */
public class Missile extends GameObjectAdapter
        implements Collidable, Pool.Poolable, Disposable, Explosable {
    private static final ReflectionPool<Missile> sPool = new ReflectionPool<>(Missile.class);

    private static final float WIDTH = 44;
    private static final float HEIGHT = 13;
    private static final float DURATION = 3;

    private static final float LOCK_DISTANCE = 40;
    private static final float LOCK_ARC = 120;
    private static final float WAITING_DENSITY = 0.0000001f;
    private static final float SHOT_DENSITY = 0.0001f;
    private static final Color TARGETED_COLOR = new Color(1, 1, 1, 0.7f);
    private static final Color LOCKED_COLOR = new Color(1, 0.3f, 0.3f, 0.9f);
    private SoundPlayer mSoundPlayer;

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
    private final ClosestRacerFinder mRacerFinder = new ClosestRacerFinder(LOCK_DISTANCE, LOCK_ARC);
    private final MissileGuidingSystem mGuidingSystem = new MissileGuidingSystem();
    private Assets mAssets;

    private final DebugShapeMap.Shape mDebugShape =
            new DebugShapeMap.Shape() {
                @Override
                public void draw(ShapeRenderer renderer) {
                    renderer.begin(ShapeRenderer.ShapeType.Line);
                    renderer.setColor(1, 0, 0, 1);

                    Vector2 origin = mBody.getWorldCenter();
                    float angle = mBody.getAngle() * MathUtils.radDeg;
                    renderer.line(origin, mRacerFinder.getLeftVertex(origin, angle));
                    renderer.line(origin, mRacerFinder.getRightVertex(origin, angle));
                    renderer.end();
                }
            };

    // Init-at-pool-reuse fields
    private GameWorld mGameWorld;
    private AudioManager mAudioManager;
    private Racer mShooter;
    private Body mBody;

    // Moving fields
    private float mTime;
    private Joint mJoint;
    private Status mStatus;
    private boolean mNeedShootSound;
    private Racer mTarget;

    public Missile() {
        mBodyDef.type = BodyDef.BodyType.DynamicBody;
        mBodyDef.bullet = true;
        mShape.setAsBox(
                WIDTH * Constants.UNIT_FOR_PIXEL / 2, HEIGHT * Constants.UNIT_FOR_PIXEL / 2);
    }

    public static Missile create(
            Assets assets, GameWorld gameWorld, AudioManager audioManager, Racer shooter) {
        Missile object = sPool.obtain();
        object.mAssets = assets;
        object.mGameWorld = gameWorld;
        object.mAudioManager = audioManager;
        object.setFinished(false);
        object.mRacerFinder.setIgnoredRacer(shooter);
        Vehicle vehicle = shooter.getVehicle();
        object.mShooter = shooter;
        object.mBodyDef.position.set(vehicle.getX(), vehicle.getY());
        object.mBodyDef.angle = vehicle.getAngle() * MathUtils.degRad;

        object.mBody = gameWorld.getBox2DWorld().createBody(object.mBodyDef);
        object.mBody.createFixture(object.mShape, WAITING_DENSITY);
        object.mBody.setUserData(object);
        Box2DUtils.setCollisionInfo(
                object.mBody,
                CollisionCategories.RACER_BULLET,
                CollisionCategories.WALL
                        | CollisionCategories.RACER
                        | CollisionCategories.EXPLOSABLE);

        object.mStatus = Status.WAITING;
        object.mNeedShootSound = false;
        object.mTarget = null;
        object.initJoint();

        object.mGuidingSystem.init(object.mBody);

        gameWorld.addGameObject(object);

        DebugShapeMap.put(object, object.mDebugShape);

        return object;
    }

    public boolean hasTarget() {
        return mTarget != null;
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
        resetJoint();
        mBody.getFixtureList().first().setDensity(SHOT_DENSITY);
        mBody.resetMassData();
        mBody.setAngularVelocity(0);
        mStatus = Status.SHOT;
        mTime = 0;
        mNeedShootSound = true;
    }

    @Override
    public void reset() {
        // Do not reset the joint here: it crashes for some reason.
        // The joint is deleted when the body is destroyed anyway.
        mGameWorld.getBox2DWorld().destroyBody(mBody);
        mBody = null;
        DebugShapeMap.remove(this);
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
        switch (mStatus) {
            case WAITING:
                actWaiting();
                break;
            case SHOT:
                actShot(delta);
                break;
            case LOCKED:
                actLocked(delta);
                break;
        }
    }

    private void actWaiting() {
        findTarget();
    }

    private void actShot(float delta) {
        findTarget();
        if (mTarget != null) {
            mStatus = Status.LOCKED;
        }
        mGuidingSystem.act(null);
        consumeTime(delta);
    }

    private void actLocked(float delta) {
        mGuidingSystem.act(mTarget.getPosition());
        consumeTime(delta);
    }

    private void consumeTime(float delta) {
        mTime += delta;
        if (mTime >= DURATION) {
            explode();
        }
    }

    private void findTarget() {
        World world = mGameWorld.getBox2DWorld();
        mTarget =
                mRacerFinder.find(
                        world, mBody.getWorldCenter(), mBody.getAngle() * MathUtils.radDeg);
    }

    @Override
    public void draw(Batch batch, ZLevel zLevel) {
        if (zLevel == ZLevel.FLYING) {
            // Draw the shadow at Z_FLYING so that the shadow is drawn *over* its vehicle
            drawShadow(batch);
            drawMissile(batch);
            drawTarget(batch);
            if (mStatus != Status.WAITING) {
                drawReactorFire(batch);
            }
        }
    }

    private void drawTarget(Batch batch) {
        if (mTarget == null) {
            return;
        }
        batch.setColor(mStatus == Status.LOCKED ? LOCKED_COLOR : TARGETED_COLOR);
        TextureRegion region = mAssets.target;
        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        batch.draw(region, mTarget.getX() - w / 2, mTarget.getY() - h / 2, w, h);
        batch.setColor(Color.WHITE);
    }

    private void drawMissile(Batch batch) {
        mDrawer.setBatch(batch);
        mDrawer.draw(mBody, mAssets.missile);
    }

    private void drawReactorFire(Batch batch) {
        TextureRegion region = mAssets.turboFlame.getKeyFrame(mTime, true);
        Vector2 center = mBody.getPosition();
        float angle = mBody.getAngle();
        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        float refH = Constants.UNIT_FOR_PIXEL * -WIDTH / 2;
        float x = center.x + refH * MathUtils.cos(angle);
        float y = center.y + refH * MathUtils.sin(angle);
        batch.draw(
                region,
                x - w / 2,
                y - h, // pos
                w / 2,
                h, // origin
                w,
                h, // size
                1,
                1, // scale
                angle * MathUtils.radDeg - 90);
    }

    private void drawShadow(Batch batch) {
        mDrawer.setBatch(batch);
        mDrawer.drawShadow(mBody, mAssets.missile);
    }

    @Override
    public void audioRender(AudioClipper clipper) {
        if (mNeedShootSound) {
            mSoundPlayer = mAudioManager.createSoundPlayer(mAssets.soundAtlas.get("missile"));
            mSoundPlayer.setVolume(clipper.clip(this));
            mSoundPlayer.play();

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

    public void remove() {
        setFinished(true);
    }

    @Override
    public void explode() {
        Vector2 pos = mBody.getPosition();
        AnimationObject obj = mAssets.createExplosion(mAudioManager, pos.x, pos.y);
        mGameWorld.addGameObject(obj);
        mSoundPlayer.stop();
        setFinished(true);
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {}

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {}

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
            mShooter.getGameStats().recordEvent(GameStats.Event.MISSILE_HIT);
            ((Racer) other).spin();
        } else if (other instanceof Explosable) {
            ((Explosable) other).explode();
        }
    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {}
}
