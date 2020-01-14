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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;

/** A mine which can be dropped behind the racer */
public class MineBonus extends BonusAdapter implements Pool.Poolable {
    private static final float AI_KEEP_BONUS_MIN_TIME = 2f;
    private static final float AI_KEEP_BONUS_MAX_TIME = 5f;

    private Mine mMine;
    private boolean mTriggered;

    private float mAiKeepTime;

    @Override
    public void reset() {
        mTriggered = false;
    }

    @Override
    public void onPicked(Racer racer) {
        super.onPicked(racer);
        mMine = Mine.createAttachedMine(mGameWorld, mAssets, mAudioManager, mRacer);
        mAiKeepTime = MathUtils.random(AI_KEEP_BONUS_MIN_TIME, AI_KEEP_BONUS_MAX_TIME);
    }

    @Override
    public void trigger() {
        mTriggered = true;
    }

    @Override
    public void onOwnerHit() {
        mTriggered = true;
    }

    @Override
    public void act(float delta) {
        if (mTriggered) {
            mRacer.resetBonus();
            mMine.drop();
            free();
        }
    }

    @Override
    public void aiAct(float delta) {
        mAiKeepTime -= delta;
        if (mAiKeepTime <= 0) {
            mRacer.triggerBonus();
        }
    }
}
