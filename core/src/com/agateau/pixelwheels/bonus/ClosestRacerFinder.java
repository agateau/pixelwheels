/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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

import static com.agateau.pixelwheels.utils.ArcClosestBodyFinder.FilterResult.IGNORE;
import static com.agateau.pixelwheels.utils.ArcClosestBodyFinder.FilterResult.STOP_FAILED;
import static com.agateau.pixelwheels.utils.ArcClosestBodyFinder.FilterResult.STOP_SUCCESS;

import com.agateau.pixelwheels.BodyIdentifier;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.utils.ArcClosestBodyFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class ClosestRacerFinder {
    private final ArcClosestBodyFinder mBodyFinder;
    private final RacerBodyFilter mFilter = new RacerBodyFilter();

    private static class RacerBodyFilter implements ArcClosestBodyFinder.BodyFilter {
        Racer mIgnoredRacer;

        @Override
        public ArcClosestBodyFinder.FilterResult filter(Body body) {
            if (BodyIdentifier.isStaticObstacle(body)) {
                return STOP_FAILED;
            }
            Object userData = body.getUserData();
            if (!(userData instanceof Racer) || userData == mIgnoredRacer) {
                return IGNORE;
            }
            return STOP_SUCCESS;
        }
    }

    public ClosestRacerFinder(float depth) {
        this(depth, 0);
    }

    public ClosestRacerFinder(float depth, float arc) {
        mBodyFinder = new ArcClosestBodyFinder(depth, arc);
        mBodyFinder.setBodyFilter(mFilter);
    }

    public void setIgnoredRacer(Racer ignoredRacer) {
        mFilter.mIgnoredRacer = ignoredRacer;
    }

    public Racer find(World world, Vector2 origin, float angle) {
        Body body = mBodyFinder.find(world, origin, angle);
        if (body == null) {
            return null;
        } else {
            return (Racer) body.getUserData();
        }
    }

    public Vector2 getLeftVertex(Vector2 origin, float angle) {
        return mBodyFinder.getLeftVertex(origin, angle);
    }

    public Vector2 getRightVertex(Vector2 origin, float angle) {
        return mBodyFinder.getRightVertex(origin, angle);
    }
}
