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
package com.agateau.pixelwheels.bonus;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.gameobjet.Explosable;
import com.agateau.pixelwheels.gameobjet.GameObjectAdapter;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racer.Vehicle;
import com.agateau.pixelwheels.racescreen.Collidable;
import com.agateau.pixelwheels.racescreen.CollisionCategories;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.utils.BodyRegionDrawer;
import com.agateau.pixelwheels.utils.Box2DUtils;
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

/** A mine on the road */
public class Mine extends GameObjectAdapter
        implements Collidable, Pool.Poolable, Disposable, Explosable {
    private static final ReflectionPool<Mine> sPool = new ReflectionPool<>(Mine.class);

    private static final float MINE_RADIUS = 0.8f;

    private GameWorld mGameWorld;
    private AudioManager mAudioManager;
    private Assets mAssets;
    private Racer mOwner;
    private BodyDef mBodyDef;
    private final WeldJointDef mJointDef = new WeldJointDef();
    private CircleShape mShape;

    private Body mBody;
    private float mTime;
    private Joint mJoint;

    private static final Vector2 sTmp = new Vector2();

    public static Mine createAttachedMine(
            GameWorld gameWorld, Assets assets, AudioManager audioManager, Racer owner) {
        Vehicle vehicle = owner.getVehicle();
        sTmp.set(-vehicle.getWidth(), 0);
        sTmp.rotate(vehicle.getAngle()).add(vehicle.getX(), vehicle.getY());

        Mine mine = createInternal(gameWorld, assets, audioManager, sTmp);
        mine.mOwner = owner;
        mine.initJoint();
        return mine;
    }

    public static Mine createDroppedMine(
            GameWorld gameWorld, Assets assets, AudioManager audioManager, Vector2 position) {
        Mine mine = createInternal(gameWorld, assets, audioManager, position);
        mine.mBody.setType(BodyDef.BodyType.StaticBody);
        return mine;
    }

    private static Mine createInternal(
            GameWorld gameWorld, Assets assets, AudioManager audioManager, Vector2 position) {
        Mine mine = sPool.obtain();
        if (mine.mBodyDef == null) {
            mine.firstInit(assets);
        }

        mine.mGameWorld = gameWorld;
        mine.mAudioManager = audioManager;
        mine.mOwner = null;
        mine.mTime = 0;
        mine.mJoint = null;
        mine.setFinished(false);

        mine.mBodyDef.position.set(position);

        mine.mBody = gameWorld.getBox2DWorld().createBody(mine.mBodyDef);
        mine.mBody.createFixture(mine.mShape, 0.00001f);
        mine.mBody.setUserData(mine);
        mine.mBody.setType(BodyDef.BodyType.DynamicBody);

        Box2DUtils.setCollisionInfo(
                mine.mBody,
                CollisionCategories.EXPLOSABLE,
                CollisionCategories.WALL
                        | CollisionCategories.RACER
                        | CollisionCategories.RACER_BULLET);

        gameWorld.addGameObject(mine);
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
        mJointDef.localAnchorA.set(
                vehicleBody.getLocalCenter().add(-mOwner.getVehicle().getWidth(), 0));
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

    private final BodyRegionDrawer mBodyRegionDrawer = new BodyRegionDrawer();

    @Override
    public void draw(Batch batch, ZLevel zLevel) {
        mBodyRegionDrawer.setBatch(batch);

        if (zLevel == ZLevel.GROUND) {
            // Smaller shadow if the mine has been dropped
            float z = mJoint == null ? -0.1f : 0f;
            mBodyRegionDrawer.setZ(z);
            TextureRegion region = mAssets.mine.getKeyFrame(mTime);
            mBodyRegionDrawer.drawShadow(mBody, region);
        }
        if (zLevel == ZLevel.VEHICLES) {
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

    @Override
    public void explode() {
        if (mJoint != null) {
            mOwner.resetBonus();
        }
        setFinished(true);
        Vector2 pos = mBody.getPosition();
        mGameWorld.addGameObject(mAssets.createExplosion(mAudioManager, pos.x, pos.y));
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
        ((Racer) other).spin();
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {}

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {}

    public void drop() {
        mGameWorld.getBox2DWorld().destroyJoint(mJoint);
        mJoint = null;
        mBody.setType(BodyDef.BodyType.StaticBody);
    }
}
