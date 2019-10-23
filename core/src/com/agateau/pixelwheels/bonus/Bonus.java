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

/** A bonus. When the bonus is done, it must call Racer.resetBonus(). */
public interface Bonus {
    void init(
            ReflectionPool<? extends Bonus> pool,
            Assets assets,
            GameWorld gameWorld,
            AudioManager audioManager);

    /**
     * Called when a Racer picked the bonus. Should store the racer parameter for future use, such
     * as when aiAct() is called.
     */
    void onPicked(Racer racer);

    /** Called when a Racer is hit while carrying a bonus. */
    void onOwnerHit();

    void trigger();

    /** Called by the racer as long as it owns the bonus. */
    void act(float delta);

    /** Implements behavior of the AI when it owns this bonus */
    void aiAct(float delta);
}
