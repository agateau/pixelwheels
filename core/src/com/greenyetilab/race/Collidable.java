package com.greenyetilab.race;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

public interface Collidable {
    public void beginContact(Contact contact, Fixture otherFixture);
    public void endContact(Contact contact, Fixture otherFixture);
}
