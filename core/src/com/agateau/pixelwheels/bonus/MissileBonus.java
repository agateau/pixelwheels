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
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.debug.DebugShapeMap;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    public MissileBonus(Pool pool) {
        mPool = pool;
        reset();
    }

    @Override
    public void reset() {
        mTriggered = false;
    }

    @Override
    public TextureRegion getIconRegion() {
        return mPool.getAssets().missile;
    }

    @Override
    public void onPicked(Racer racer) {
        super.onPicked(racer);
        mMissile = Missile.create(mPool.getAssets(), mPool.getGameWorld(), mPool.getAudioManager(), mRacer);
    }

    @Override
    public void onOwnerHit() {
        mTriggered = true;
    }

    @Override
    public void trigger() {
        mTriggered = true;
        DebugShapeMap.remove(this);
    }

    @Override
    public void act(float delta) {
        if (mTriggered) {
            mMissile.shoot();
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
        NLog.d("");
        mPool.free(this);
        mRacer.resetBonus();
    }
}
