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

import com.agateau.tinywheels.sound.AudioClipper;

/**
 * An adapter for the GameObject interface
 */
public abstract class GameObjectAdapter implements GameObject {
    private boolean mIsFinished = false;

    @Override
    public boolean isFinished() {
        return mIsFinished;
    }

    @Override
    public void audioRender(AudioClipper audioClipper) {
    }

    protected void setFinished(boolean value) {
        mIsFinished = value;
    }
}
