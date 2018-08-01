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
package com.agateau.pixelwheels.bonus;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.Renderer;
import com.agateau.pixelwheels.debug.DebugShapeMap;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racer.Vehicle;
import com.agateau.pixelwheels.sound.AudioManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

/**
 * A gun bonus
 */
public class GunBonus extends BonusAdapter implements Pool.Poolable {
    private static final float SHOOT_INTERVAL = 0.1f;
    private static final int SHOOT_COUNT = 20;
    private static final float SPREAD_ANGLE = 5;
    private static final float AI_RAYCAST_LENGTH = 20;

    public static class Pool extends BonusPool {
        public Pool(Assets assets, GameWorld gameWorld, AudioManager audioManager) {
            super(assets, gameWorld, audioManager);
            setCounts(new float[]{0, 1, 1});
        }

        @Override
        protected Bonus newObject() {
            return new GunBonus(this);
        }
    }

    private final Pool mPool;

    private boolean mTriggered;
    private float mAnimationTime;
    private float mDelayForNextShot;
    private int mRemainingShots;

    private final ClosestRacerFinder mClosestRacerFinder;

    private final Renderer mBonusRenderer = new Renderer() {
        @Override
        public void draw(Batch batch, int zIndex) {
            TextureRegion region = mPool.getAssets().gunAnimation.getKeyFrame(mAnimationTime, true);
            Vehicle vehicle = mRacer.getVehicle();
            Body body = vehicle.getBody();
            Vector2 center = body.getPosition();
            float angle = body.getAngle() * MathUtils.radiansToDegrees;
            float x = center.x;
            float y = center.y;
            float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
            float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
            batch.draw(region,
                    x - w / 2, y - h / 2, // pos
                    w / 2, h / 2, // origin
                    w, h, // size
                    1, 1, // scale
                    angle - 90);
        }
    };

    private final Vector2 mRayCastV1 = new Vector2();
    private final Vector2 mRayCastV2 = new Vector2();

    private final DebugShapeMap.Shape mDebugShape = new DebugShapeMap.Shape() {
        @Override
        public void draw(ShapeRenderer renderer) {
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.setColor(1, 0, 0, 1);
            renderer.line(mRayCastV1, mRayCastV2);
            renderer.end();
        }
    };

    public GunBonus(Pool pool) {
        mPool = pool;
        mClosestRacerFinder = new ClosestRacerFinder(pool.getGameWorld().getBox2DWorld());
        reset();
    }

    @Override
    public void reset() {
        mTriggered = false;
        mAnimationTime = 0;
        mDelayForNextShot = 0;
        mRemainingShots = SHOOT_COUNT;
    }

    @Override
    public TextureRegion getIconRegion() {
        return mPool.getAssets().bullet;
    }

    @Override
    public void onPicked(Racer racer) {
        super.onPicked(racer);
        mRacer.getVehicleRenderer().addRenderer(mBonusRenderer);
        mClosestRacerFinder.setIgnoredRacer(mRacer);
        DebugShapeMap.put(this, mDebugShape);
    }

    @Override
    public void onOwnerHit() {
        resetBonus();
    }

    @Override
    public void trigger() {
        mTriggered = true;
        mDelayForNextShot = 0;
        DebugShapeMap.remove(this);
    }

    @Override
    public void act(float delta) {
        if (!mTriggered) {
            return;
        }
        mAnimationTime += delta;
        mDelayForNextShot -= delta;
        if (mDelayForNextShot > 0) {
            // Not time to shoot yet
            return;
        }

        // Shoot
        Vehicle vehicle = mRacer.getVehicle();
        float angle = vehicle.getAngle() + MathUtils.random(-SPREAD_ANGLE, SPREAD_ANGLE);
        GameWorld gameWorld = mPool.getGameWorld();
        Bullet bullet = Bullet.create(mPool.getAssets(), gameWorld, mPool.getAudioManager(), mRacer, vehicle.getX(), vehicle.getY(), angle);
        gameWorld.addGameObject(bullet);

        mRemainingShots--;
        if (mRemainingShots == 0) {
            resetBonus();
        } else {
            mDelayForNextShot = SHOOT_INTERVAL;
        }
    }

    @Override
    public void aiAct(float delta) {
        mRayCastV1.set(mRacer.getX(), mRacer.getY());
        mRayCastV2.set(AI_RAYCAST_LENGTH, 0).rotate(mRacer.getVehicle().getAngle()).add(mRayCastV1);
        Racer racer = mClosestRacerFinder.find(mRayCastV1, mRayCastV2);
        if (racer != null) {
            mRacer.triggerBonus();
        }
    }

    private void resetBonus() {
        mRacer.getVehicleRenderer().removeRenderer(mBonusRenderer);
        mPool.free(this);
        mRacer.resetBonus();
    }
}
