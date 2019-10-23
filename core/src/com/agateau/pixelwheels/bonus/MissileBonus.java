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

import com.agateau.pixelwheels.racer.Racer;
import com.badlogic.gdx.utils.Pool;

/** A missile bonus */
public class MissileBonus extends BonusAdapter implements Pool.Poolable {
    private Missile mMissile;

    private boolean mTriggered;
    private boolean mOwnerHit;

    public MissileBonus() {
        reset();
    }

    @Override
    public void reset() {
        mTriggered = false;
        mOwnerHit = false;
    }

    @Override
    public void onPicked(Racer racer) {
        super.onPicked(racer);
        mMissile = Missile.create(mAssets, mGameWorld, mAudioManager, mRacer);
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
        free();
        mRacer.resetBonus();
    }
}
