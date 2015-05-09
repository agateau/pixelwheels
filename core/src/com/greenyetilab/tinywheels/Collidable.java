package com.greenyetilab.tinywheels;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public interface Collidable {
    public void beginContact(Contact contact, Fixture otherFixture);
    public void endContact(Contact contact, Fixture otherFixture);
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold);
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse);
}
