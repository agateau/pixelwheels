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
package com.agateau.tinywheels;

/**
 * An adapter for the Bonus class
 */
public abstract class BonusAdapter implements Bonus {
    protected Racer mRacer;

    @Override
    public void onPicked(Racer racer) {
        mRacer = racer;
    }

    @Override
    public void onOwnerHit() {
    }

    @Override
    public void trigger() {

    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void aiAct(float delta) {

    }
}
