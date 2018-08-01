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
import com.agateau.pixelwheels.utils.ClosestFixtureFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

public class ClosestRacerFinder {
    private final ClosestFixtureFinder mFixtureFinder;
    private final RacerFixtureFilter mFixtureFilter = new RacerFixtureFilter();

    private static class RacerFixtureFilter implements ClosestFixtureFinder.FixtureFilter {
        Racer mIgnoredRacer;

        @Override
        public boolean acceptFixture(Fixture fixture) {
            if (mIgnoredRacer != null
                    && fixture.getBody() == mIgnoredRacer.getVehicle().getBody()) {
                return false;
            }
            Object userData = fixture.getBody().getUserData();
            return userData instanceof Racer;
        }
    }

    public ClosestRacerFinder(World world) {
        mFixtureFinder = new ClosestFixtureFinder(world);
        mFixtureFinder.setFixtureFilter(mFixtureFilter);
    }

    public void setIgnoredRacer(Racer ignoredRacer) {
        mFixtureFilter.mIgnoredRacer = ignoredRacer;
    }

    public Racer find(Vector2 v1, Vector2 v2) {
        Fixture fixture = mFixtureFinder.find(v1, v2);
        if (fixture == null) {
            return null;
        } else {
            return (Racer)fixture.getBody().getUserData();
        }
    }
}
