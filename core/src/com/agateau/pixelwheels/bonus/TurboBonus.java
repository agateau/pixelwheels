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

import com.agateau.pixelwheels.Renderer;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racer.Vehicle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

/** A turbo bonus */
public class TurboBonus extends BonusAdapter implements Pool.Poolable {
    private boolean mTriggered = false;
    private float mAnimationTime;

    private final Renderer mBonusRenderer =
            new Renderer() {
                @Override
                public void draw(Batch batch, float centerX, float centerY) {
                    TextureRegion region = mAssets.turbo.getKeyFrame(mAnimationTime, true);
                    Vehicle vehicle = mRacer.getVehicle();
                    Body body = vehicle.getBody();
                    float angle = body.getAngle() * MathUtils.radiansToDegrees;
                    float w = region.getRegionWidth();
                    float h = region.getRegionHeight();
                    float refH = -vehicle.getRegion(0).getRegionWidth() / 3f;
                    float x = centerX + refH * MathUtils.cosDeg(angle);
                    float y = centerY + refH * MathUtils.sinDeg(angle);
                    batch.draw(
                            region,
                            x - w / 2,
                            y - h / 2, // pos
                            w / 2,
                            h / 2, // origin
                            w,
                            h, // size
                            1,
                            1, // scale
                            angle - 90);
                }
            };

    public TurboBonus() {
        reset();
    }

    @Override
    public void reset() {
        mAnimationTime = 0;
        mTriggered = false;
    }

    @Override
    public void onPicked(Racer racer) {
        super.onPicked(racer);
        mRacer.getVehicleRenderer().addRenderer(mBonusRenderer);
    }

    @Override
    public void onOwnerHit() {
        resetBonus();
    }

    @Override
    public void trigger() {
        if (!mTriggered) {
            mRacer.getVehicle().triggerTurbo();
            mTriggered = true;
        }
    }

    @Override
    public void act(float delta) {
        if (!mTriggered) {
            return;
        }
        mAnimationTime += delta;
        if (mAnimationTime > mAssets.turbo.getAnimationDuration()) {
            resetBonus();
        }
    }

    @Override
    public void aiAct(float delta) {
        mRacer.triggerBonus();
    }

    private void resetBonus() {
        mRacer.getVehicleRenderer().removeRenderer(mBonusRenderer);
        free();
        mRacer.resetBonus();
    }
}
