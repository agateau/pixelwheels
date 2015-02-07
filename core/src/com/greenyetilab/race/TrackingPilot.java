package com.greenyetilab.race;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * A pilot which follows another
 */
public class TrackingPilot implements Pilot {
    private final Racer mRacer;
    private final Racer mTargetRacer;

    private static final float MIN_LENGTH2 = 100;

    public TrackingPilot(Racer racer, Racer targetRacer) {
        mRacer = racer;
        mTargetRacer = targetRacer;
    }

    private static float normAngle(float angle) {
        while (angle < 0) {
            angle += 360;
        }
        return angle % 360;
    }

    private final Vector2 mTargetVector = new Vector2();
    @Override
    public boolean act(float dt) {
        Vehicle vehicle = mRacer.getVehicle();
        mTargetVector.set(mTargetRacer.getX() - mRacer.getX(), mTargetRacer.getY() - mRacer.getY());
        vehicle.setAccelerating(mTargetVector.len2() > MIN_LENGTH2);
        float targetAngle = normAngle(mTargetVector.angle());
        float vehicleAngle = normAngle(vehicle.getAngle());
        float deltaAngle = targetAngle - vehicleAngle;
        if (deltaAngle > 180) {
            deltaAngle -= 360;
        } else if (deltaAngle < -180) {
            deltaAngle += 360;
        }
        float direction = MathUtils.clamp(deltaAngle / 4, -10, 10) / 10f;
        vehicle.setDirection(direction);
        return true;
    }
}
