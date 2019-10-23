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
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.sound.AudioManager;
import com.badlogic.gdx.utils.ReflectionPool;

/** An adapter for the Bonus class */
public abstract class BonusAdapter implements Bonus {
    protected Racer mRacer;

    private ReflectionPool mPool;
    protected Assets mAssets;
    protected GameWorld mGameWorld;
    protected AudioManager mAudioManager;

    @Override
    public void init(
            ReflectionPool<? extends Bonus> pool,
            Assets assets,
            GameWorld gameWorld,
            AudioManager audioManager) {
        mPool = pool;
        mAssets = assets;
        mGameWorld = gameWorld;
        mAudioManager = audioManager;
    }

    protected void free() {
        //noinspection unchecked
        mPool.free(this);
    }

    @Override
    public void onPicked(Racer racer) {
        mRacer = racer;
    }

    @Override
    public void onOwnerHit() {}

    @Override
    public void trigger() {}

    @Override
    public void act(float delta) {}

    @Override
    public void aiAct(float delta) {}
}
