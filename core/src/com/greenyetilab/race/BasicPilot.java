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
    private final HealthComponent mHealthComponent;

    public BasicPilot(MapInfo mapInfo, Vehicle vehicle, HealthComponent healthComponent) {
        mMapInfo = mapInfo;
        mVehicle = vehicle;
        mHealthComponent = healthComponent;
    }
    public boolean act(float dt) {
        if (mHealthComponent.getHealth() == 0) {
            mVehicle.setBraking(true);
            mVehicle.setAccelerating(false);
            return true;
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
}
