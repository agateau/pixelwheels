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
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.gameobjet.AnimationObject;
import com.agateau.pixelwheels.gameobjet.AudioClipper;
import com.agateau.pixelwheels.gameobjet.Explosable;
import com.agateau.pixelwheels.gameobjet.GameObjectAdapter;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racescreen.Collidable;
import com.agateau.pixelwheels.racescreen.CollisionCategories;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.utils.BodyRegionDrawer;
import com.agateau.pixelwheels.utils.Box2DUtils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/** A player bullet */
public class Bullet extends GameObjectAdapter implements Collidable, Pool.Poolable, Disposable {
    private static final ReflectionPool<Bullet> sPool = new ReflectionPool<>(Bullet.class);

    private static final float IMPULSE = 160;

    private Racer mShooter;
    private GameWorld mGameWorld;
    private AudioManager mAudioManager;
    private Assets mAssets;
    private BodyDef mBodyDef;
    private PolygonShape mShape;

    private Body mBody;
    private boolean mJustShot = false;

    private final BodyRegionDrawer mDrawer = new BodyRegionDrawer();

    public static Bullet create(
            Assets assets,
            GameWorld gameWorld,
            AudioManager audioManager,
            Racer shooter,
            float originX,
            float originY,
            float angle) {
        Bullet object = sPool.obtain();
        if (object.mBodyDef == null) {
            object.firstInit(assets);
        }
        object.mShooter = shooter;
        object.mGameWorld = gameWorld;
        object.mAudioManager = audioManager;
        object.setFinished(false);
        object.mJustShot = true;
        object.mBodyDef.position.set(originX, originY);
        object.mBodyDef.angle = angle * MathUtils.degreesToRadians;

        object.mBody = gameWorld.getBox2DWorld().createBody(object.mBodyDef);
        object.mBody.createFixture(object.mShape, 0f);
        object.mBody.setUserData(object);
        object.mBody.applyLinearImpulse(
                IMPULSE * MathUtils.cosDeg(angle),
                IMPULSE * MathUtils.sinDeg(angle),
                originX,
                originY,
                true);

        Box2DUtils.setCollisionInfo(
                object.mBody,
                CollisionCategories.RACER_BULLET,
                CollisionCategories.WALL
                        | CollisionCategories.RACER
                        | CollisionCategories.EXPLOSABLE);
        return object;
    }

    private void firstInit(Assets assets) {
        mAssets = assets;
        mBodyDef = new BodyDef();
        mBodyDef.type = BodyDef.BodyType.DynamicBody;
        mBodyDef.bullet = true;

        mShape = new PolygonShape();
        mShape.setAsBox(
                assets.bullet.getRegionWidth() * Constants.UNIT_FOR_PIXEL / 2,
                assets.bullet.getRegionHeight() * Constants.UNIT_FOR_PIXEL / 2);
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
    public void act(float delta) {}

    @Override
    public void draw(Batch batch, ZLevel zLevel) {
        if (zLevel == ZLevel.GROUND) {
            mDrawer.setBatch(batch);
            mDrawer.draw(mBody, mAssets.bullet);
        }
    }

    @Override
    public void audioRender(AudioClipper clipper) {
        if (mJustShot) {
            mAudioManager.play(mAssets.soundAtlas.get("shoot"), clipper.clip(this));
            mJustShot = false;
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
        Vector2 pos = mBody.getPosition();
        AnimationObject obj = AnimationObject.create(mAssets.impact, pos.x, pos.y);
        obj.initAudio(mAudioManager, mAssets.soundAtlas.get("impact"));
        mGameWorld.addGameObject(obj);
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
        Object other = otherFixture.getBody().getUserData();
        if (other == mShooter) {
            contact.setEnabled(false);
            return;
        }

        explode();
        if (other instanceof Racer) {
            ((Racer) other).spin();
        } else if (other instanceof Explosable) {
            ((Explosable) other).explode();
        }
    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {}
}
