package com.greenyetilab.tinywheels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * An AI pilot
 */
public class AIPilot implements Pilot {
    private final MapInfo mMapInfo;
    private final Racer mRacer;

    public AIPilot(MapInfo mapInfo, Racer racer) {
        mMapInfo = mapInfo;
        mRacer = racer;
    }

    private static float normAngle(float angle) {
        while (angle < 0) {
            angle += 360;
        }
        return angle % 360;
    }

    private final Vector2 mTargetVector = new Vector2();
    @Override
    public void act(float dt) {
        Vehicle vehicle = mRacer.getVehicle();
        vehicle.setAccelerating(true);

        Vector2 waypoint = mMapInfo.getWaypoint(mRacer.getX(), mRacer.getY());
        mTargetVector.set(waypoint.x - mRacer.getX(), waypoint.y - mRacer.getY());

        // Dumb behavior: use bonus as soon as we get it
        Bonus bonus = mRacer.getBonus();
        if (bonus != null) {
            bonus.aiAct(dt);
        }

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
    }
}
