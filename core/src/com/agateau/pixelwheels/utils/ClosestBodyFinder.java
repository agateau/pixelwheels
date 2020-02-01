/*
 * Copyright 2020 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

/** Helper class to find the closest body between two points */
public class ClosestBodyFinder {
    private final BodyFilter mFilter;

    private class StaticBodyRayCastCallback implements RayCastCallback {
        Body mBody = null;

        @Override
        public float reportRayFixture(
                Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            Body body = fixture.getBody();
            if (mFilter == null || mFilter.acceptBody(body)) {
                mBody = body;
                return 0;
            } else {
                return -1;
            }
        }
    }

    public interface BodyFilter {
        boolean acceptBody(Body body);
    }

    public ClosestBodyFinder() {
        this(null);
    }

    public ClosestBodyFinder(BodyFilter filter) {
        mFilter = filter;
    }

    private final StaticBodyRayCastCallback mRayCastCallback = new StaticBodyRayCastCallback();

    public Body find(World world, Vector2 point1, Vector2 point2) {
        mRayCastCallback.mBody = null;
        world.rayCast(mRayCastCallback, point1, point2);
        return mRayCastCallback.mBody;
    }
}
