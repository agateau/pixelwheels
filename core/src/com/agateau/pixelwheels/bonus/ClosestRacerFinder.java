/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.utils.ClosestBodyFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class ClosestRacerFinder {
    private final ClosestBodyFinder mBodyFinder;
    private final RacerBodyFilter mFilter = new RacerBodyFilter();

    private static class RacerBodyFilter implements ClosestBodyFinder.BodyFilter {
        Racer mIgnoredRacer;

        @Override
        public boolean acceptBody(Body body) {
            Object userData = body.getUserData();
            return userData instanceof Racer && userData != mIgnoredRacer;
        }
    }

    public ClosestRacerFinder(World world, float depth) {
        this(world, depth, 0);
    }

    public ClosestRacerFinder(World world, float depth, float arc) {
        mBodyFinder = new ClosestBodyFinder(world, depth, arc);
        mBodyFinder.setBodyFilter(mFilter);
    }

    public void setIgnoredRacer(Racer ignoredRacer) {
        mFilter.mIgnoredRacer = ignoredRacer;
    }

    public Racer find(Vector2 origin, float angle) {
        Body body = mBodyFinder.find(origin, angle);
        if (body == null) {
            return null;
        } else {
            return (Racer)body.getUserData();
        }
    }

    public Vector2 getLeftVertex(Vector2 origin, float angle) {
        return mBodyFinder.getLeftVertex(origin, angle);
    }

    public Vector2 getRightVertex(Vector2 origin, float angle) {
        return mBodyFinder.getRightVertex(origin, angle);
    }
}
