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

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Renderer;
import com.agateau.pixelwheels.ZLevel;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Vector2;

/** Make a vehicle slow down for a short duration */
public class DisruptedComponent implements Racer.Component, Renderer {
    private static final float DURATION = 1.5f;
    private final Assets mAssets;
    private final Racer mRacer;
    private boolean mActive = false;
    private float mRemainingDuration = 0;
    private ParticleEffectPool.PooledEffect mSmokeEffect;

    public DisruptedComponent(Assets assets, Racer racer) {
        mAssets = assets;
        mRacer = racer;
    }

    public boolean isActive() {
        return mActive;
    }

    public float getNormalizedDuration() {
        return mRemainingDuration / DURATION;
    }

    public void start() {
        mRemainingDuration = DURATION;
        if (mActive) {
            return;
        }
        mActive = true;
        mSmokeEffect = mAssets.smokeEffectPool.obtain();
        mRacer.getVehicleRenderer().addRenderer(this);
    }

    @Override
    public void act(float delta) {
        if (mSmokeEffect != null) {
            mSmokeEffect.update(delta);
            if (mActive) {
                if (mSmokeEffect.isComplete()) {
                    mSmokeEffect.start();
                }
            } else {
                if (mSmokeEffect.isComplete()) {
                    mSmokeEffect.free();
                    mRacer.getVehicleRenderer().removeRenderer(this);
                    mSmokeEffect = null;
                }
            }
        }

        if (mActive) {
            mRemainingDuration -= delta;
            if (mRemainingDuration <= 0) {
                mRemainingDuration = 0;
                mActive = false;
            }
        }
    }

    @Override
    public void drawToCell(Batch batch, float centerX, float centerY) {}

    private final Vector2 mTmp = new Vector2();

    @Override
    public void draw(Batch batch, ZLevel zLevel) {
        if (zLevel != ZLevel.FLYING_HIGH) {
            return;
        }
        if (mSmokeEffect == null) {
            return;
        }

        Vehicle vehicle = mRacer.getVehicle();
        // By default vehicle points to the right, so getWidth() / 4 roughly points to the middle of
        // the hood
        mTmp.set(vehicle.getWidth() / 4, 0)
                .rotateDeg(vehicle.getAngle())
                .add(vehicle.getX(), vehicle.getY());
        mSmokeEffect.setPosition(mTmp.x, mTmp.y);
        mSmokeEffect.draw(batch);
    }
}
