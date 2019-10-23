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
package com.agateau.pixelwheels.gameobjet;

import com.badlogic.gdx.math.Vector2;

/** An adapter for the GameObject interface */
public abstract class GameObjectAdapter implements GameObject {
    private boolean mIsFinished = false;
    private final Vector2 mPosition = new Vector2();

    @Override
    public boolean isFinished() {
        return mIsFinished;
    }

    @Override
    public void audioRender(AudioClipper audioClipper) {}

    @Override
    public Vector2 getPosition() {
        mPosition.set(getX(), getY());
        return mPosition;
    }

    protected void setFinished(boolean value) {
        mIsFinished = value;
    }
}
