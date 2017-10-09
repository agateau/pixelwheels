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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * A mine on the road
 */
public class Mine extends GameObjectAdapter implements Collidable, Pool.Poolable, Disposable {
    private static final ReflectionPool<Mine> sPool = new ReflectionPool<Mine>(Mine.class);

    private static final float MINE_RADIUS = 0.8f;

    private GameWorld mGameWorld;
    private Assets mAssets;
    private Racer mOwner;
    private BodyDef mBodyDef;
    private WeldJointDef mJointDef = new WeldJointDef();
    private CircleShape mShape;

    private Body mBody;
    private float mTime;
    private Joint mJoint;

    private static final Vector2 sTmp = new Vector2();
    public static Mine create(GameWorld gameWorld, Assets assets, Racer owner) {
        Mine mine = sPool.obtain();
        if (mine.mBodyDef == null) {
            mine.firstInit(assets);
        }

        mine.mGameWorld = gameWorld;
        mine.mOwner = owner;
        mine.mTime = 0;
        mine.setFinished(false);

        Vehicle vehicle = owner.getVehicle();
        sTmp.set(-vehicle.getWidth(), 0);
        sTmp.rotate(vehicle.getAngle()).add(vehicle.getX(), vehicle.getY());
        mine.mBodyDef.position.set(sTmp.x, sTmp.y);

        mine.mBody = gameWorld.getBox2DWorld().createBody(mine.mBodyDef);
        mine.mBody.createFixture(mine.mShape, 0.00001f);
        mine.mBody.setUserData(mine);
        mine.mBody.setType(BodyDef.BodyType.DynamicBody);

        Box2DUtils.setCollisionInfo(mine.mBody, CollisionCategories.FLAT_OBJECT,
                CollisionCategories.WALL | CollisionCategories.RACER
                | CollisionCategories.FLAT_OBJECT);

        gameWorld.addGameObject(mine);

        mine.initJoint();
        return mine;
    }

    private void firstInit(Assets assets) {
        mAssets = assets;
        mBodyDef = new BodyDef();
        mBodyDef.type = BodyDef.BodyType.DynamicBody;

        mShape = new CircleShape();
        mShape.setRadius(MINE_RADIUS);
    }

    private void initJoint() {
        Body vehicleBody = mOwner.getVehicle().getBody();
        mJointDef.bodyA = mOwner.getVehicle().getBody();
        mJointDef.bodyB = mBody;
        mJointDef.localAnchorA.set(vehicleBody.getLocalCenter().add(-mOwner.getVehicle().getWidth(), 0));
        mJointDef.localAnchorB.set(mBody.getLocalCenter());
        mJoint = mGameWorld.getBox2DWorld().createJoint(mJointDef);
    }

    @Override
    public void reset() {
        mGameWorld.getBox2DWorld().destroyBody(mBody);
        mBody = null;
    }

    @Override
    public void dispose() {
        sPool.free(this);
    }

    @Override
    public void act(float delta) {
        mTime += delta;
    }

    private BodyRegionDrawer mBodyRegionDrawer = new BodyRegionDrawer();
    @Override
    public void draw(Batch batch, int zIndex) {
        mBodyRegionDrawer.setBatch(batch);

        if (zIndex == Constants.Z_GROUND) {
            // Smaller shadow if the mine has been dropped
            float z = mJoint == null ? -0.1f : 0f;
            mBodyRegionDrawer.setZ(z);
            TextureRegion region = mAssets.mine.getKeyFrame(mTime);
            mBodyRegionDrawer.drawShadow(mBody, region);
        }
        if (zIndex == Constants.Z_VEHICLES) {
            TextureRegion region = mAssets.mine.getKeyFrame(mTime);
            mBodyRegionDrawer.draw(mBody, region);
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
        if (mJoint != null) {
            mOwner.resetBonus();
        }
        setFinished(true);
        Vector2 pos = mBody.getPosition();
        mGameWorld.addGameObject(AnimationObject.create(mAssets.explosion, pos.x, pos.y));
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        Object other = otherFixture.getBody().getUserData();
        if (!(other instanceof Racer)) {
            return;
        }
        if (mJoint != null && other == mOwner) {
            return;
        }
        explode();
        ((Racer)other).spin();
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {

    }

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {

    }

    public void drop() {
        mGameWorld.getBox2DWorld().destroyJoint(mJoint);
        mJoint = null;
        mBody.setType(BodyDef.BodyType.StaticBody);
    }
}
