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
package com.agateau.tinywheels.bonus;

import com.agateau.tinywheels.Assets;
import com.agateau.tinywheels.GameWorld;
import com.agateau.tinywheels.racer.Racer;
import com.agateau.tinywheels.sound.AudioManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;

/**
 * A mine which can be dropped behind the racer
 */
public class MineBonus extends BonusAdapter implements Pool.Poolable {
    private static final float AI_KEEP_BONUS_MIN_TIME = 2f;
    private static final float AI_KEEP_BONUS_MAX_TIME = 5f;

    private final Pool mPool;
    private Mine mMine;
    private boolean mTriggered;

    private float mAiKeepTime;

    public static class Pool extends BonusPool {
        public Pool(Assets assets, GameWorld gameWorld, AudioManager audioManager) {
            super(assets, gameWorld, audioManager);
            setCounts(new float[]{2, 1, 0});
        }

        @Override
        protected Bonus newObject() {
            return new MineBonus(this);
        }
    }

    public MineBonus(Pool pool) {
        mPool = pool;
    }

    @Override
    public void reset() {
        mTriggered = false;
    }

    @Override
    public TextureRegion getIconRegion() {
        return mPool.getAssets().mine.getKeyFrame(0);
    }

    @Override
    public void onPicked(Racer racer) {
        super.onPicked(racer);
        mMine = Mine.create(mPool.getGameWorld(), mPool.getAssets(), mPool.getAudioManager(), mRacer);
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
            mPool.free(this);
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
