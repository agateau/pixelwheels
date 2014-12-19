package com.greenyetilab.race;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
* Created by aurelien on 17/12/14.
*/
class BasicPilot implements Pilot {
    private final Vehicle mVehicle;
    private final MapInfo mMapInfo;

    public BasicPilot(MapInfo mapInfo, Vehicle vehicle) {
        mMapInfo = mapInfo;
        mVehicle = vehicle;
    }
    public boolean act(float dt) {
        if (mVehicle.getHealth() == 0) {
            mVehicle.setAccelerating(false);
            return !mVehicle.isDead();
        }
        mVehicle.setAccelerating(true);

        float directionAngle = mMapInfo.getDirectionAt(mVehicle.getX(), mVehicle.getY());
        float angle = mVehicle.getAngle();
        float delta = Math.abs(angle - directionAngle);
        if (delta < 2) {
            mVehicle.setDirection(0);
            return true;
        }
        float correctionIntensity = Math.min(1, delta / 45f);
        if (directionAngle > angle) {
            mVehicle.setDirection(correctionIntensity);
        } else {
            mVehicle.setDirection(-correctionIntensity);
        }
        return true;
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        Object other = otherFixture.getBody().getUserData();
        if (other instanceof Mine) {
            mVehicle.kill();
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
}
