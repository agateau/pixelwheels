package com.agateau.tinywheels;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Handles hitting a bonus spot
 */
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
            BonusSpot spot = (BonusSpot)other;
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
    public void endContact(Contact contact, Fixture otherFixture) {

    }

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {

    }

    @Override
    public void act(float delta) {
        if (mMustSelectBonus) {
            mMustSelectBonus = false;
            mRacer.selectBonus();
        }
    }
}
