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
package com.agateau.pixelwheels.racer;

import com.agateau.pixelwheels.bonus.BonusSpot;
import com.agateau.pixelwheels.racescreen.Collidable;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/** Handles hitting a bonus spot */
public class BonusSpotHitComponent implements Racer.Component, Collidable {
    private final Racer mRacer;
    private boolean mMustSelectBonus = false;

    public BonusSpotHitComponent(Racer racer) {
        mRacer = racer;
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        Object other = otherFixture.getBody().getUserData();
        if (other instanceof BonusSpot) {
            BonusSpot spot = (BonusSpot) other;
            spot.pickBonus();
            if (mRacer.getBonus() == null) {
                // Do not call selectBonus() from here: it would make it harder for bonus code to
                // create Box2D bodies: since we are in the collision handling code, the physic
                // engine is locked so they would have to delay such creations.
                mMustSelectBonus = true;
            }
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {}

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {}

    @Override
    public void act(float delta) {
        if (mMustSelectBonus) {
            mMustSelectBonus = false;
            mRacer.selectBonus();
        }
    }
}
