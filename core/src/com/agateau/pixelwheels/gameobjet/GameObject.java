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

import com.agateau.pixelwheels.ZLevel;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

/** A generic game object */
public interface GameObject {
    void act(float delta);

    /**
     * Returns true if the object is done and should be removed from the game. If the object
     * implements Disposable, GameWorld will take care of calling dispose() on it.
     */
    boolean isFinished();

    void draw(Batch batch, ZLevel zLevel);

    float getX();

    float getY();

    Vector2 getPosition();

    void audioRender(AudioClipper audioClipper);
}
