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
package com.agateau.pixelwheels.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

/**
 * An helper class to find the closest fixture using a raycast
 */
public class ClosestFixtureFinder implements RayCastCallback {
    private final World mWorld;
    private FixtureFilter mFixtureFilter = null;
    private Fixture mFixture = null;

    public interface FixtureFilter {
        boolean acceptFixture(Fixture fixture);
    }

    public ClosestFixtureFinder(World world) {
        mWorld = world;
    }

    public void setFixtureFilter(FixtureFilter fixtureFilter) {
        mFixtureFilter = fixtureFilter;
    }

    public Fixture run(Vector2 v1, Vector2 v2) {
        mFixture = null;
        mWorld.rayCast(this, v1, v2);
        return mFixture;
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if (mFixtureFilter != null && !mFixtureFilter.acceptFixture(fixture)) {
            return -1;
        }
        mFixture = fixture;
        return fraction;
    }
}
