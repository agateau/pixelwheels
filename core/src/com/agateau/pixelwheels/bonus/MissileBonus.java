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
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racer.Vehicle;
import com.agateau.pixelwheels.sound.AudioManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

/**
 * A missile bonus
 */
public class MissileBonus extends BonusAdapter implements Pool.Poolable {
    private Missile mMissile;

    public static class Pool extends BonusPool {
        public Pool(Assets assets, GameWorld gameWorld, AudioManager audioManager) {
            super(assets, gameWorld, audioManager);
            setCounts(new float[]{0, 1, 1});
        }

        @Override
        protected Bonus newObject() {
            return new MissileBonus(this);
        }
    }

    private final Pool mPool;

    private boolean mTriggered;
    private boolean mOwnerHit;

    private final Renderer mBonusRenderer = new Renderer() {
        @Override
        public void draw(Batch batch, int zIndex) {
            TextureRegion region = mPool.getAssets().missileLaunchpad;
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
                    angle);
        }
    };

    public MissileBonus(Pool pool) {
        mPool = pool;
        reset();
    }

    @Override
    public void reset() {
        mTriggered = false;
        mOwnerHit = false;
    }

    @Override
    public TextureRegion getIconRegion() {
        return mPool.getAssets().missile;
    }

    @Override
    public void onPicked(Racer racer) {
        super.onPicked(racer);
        mRacer.getVehicleRenderer().addRenderer(mBonusRenderer);
        mMissile = Missile.create(mPool.getAssets(), mPool.getGameWorld(), mPool.getAudioManager(), mRacer);
    }

    @Override
    public void onOwnerHit() {
        mOwnerHit = true;
    }

    @Override
    public void trigger() {
        mTriggered = true;
    }

    @Override
    public void act(float delta) {
        if (mTriggered) {
            mMissile.shoot();
            resetBonus();
        }
        if (mOwnerHit) {
            mMissile.remove();
            resetBonus();
        }
    }

    @Override
    public void aiAct(float delta) {
        if (mMissile.hasTarget()) {
            trigger();
        }
    }

    private void resetBonus() {
        mRacer.getVehicleRenderer().removeRenderer(mBonusRenderer);
        mPool.free(this);
        mRacer.resetBonus();
    }
}
