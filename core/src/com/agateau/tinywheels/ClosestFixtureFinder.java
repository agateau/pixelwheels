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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

/**
 * An helper class to find the closest fixture using a raycast
 */
class ClosestFixtureFinder implements RayCastCallback {
    private Body mIgnoredBody = null;
    private Fixture mFixture = null;

    public void setIgnoredBody(Body ignoredBody) {
        mIgnoredBody = ignoredBody;
    }

    public Fixture run(World world, Vector2 v1, Vector2 v2) {
        mFixture = null;
        world.rayCast(this, v1, v2);
        return mFixture;
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if (fixture.getBody() == mIgnoredBody) {
            return 1;
        } else {
            mFixture = fixture;
            return fraction;
        }
    }
}
