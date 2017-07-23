package com.agateau.tinywheels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.agateau.utils.GylMathUtils;

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

    private final Vector2 mTargetVector = new Vector2();
    @Override
    public void act(float dt) {
        Vehicle vehicle = mRacer.getVehicle();
        vehicle.setAccelerating(true);

        Vector2 waypoint = mMapInfo.getWaypoint(mRacer.getLapPositionComponent().getLapDistance());
        mTargetVector.set(waypoint.x - mRacer.getX(), waypoint.y - mRacer.getY());

        Bonus bonus = mRacer.getBonus();
        if (bonus != null) {
            bonus.aiAct(dt);
        }

        float targetAngle = GylMathUtils.normalizeAngle(mTargetVector.angle());
        float vehicleAngle = GylMathUtils.normalizeAngle(vehicle.getAngle());
        float deltaAngle = targetAngle - vehicleAngle;
        if (deltaAngle > 180) {
            deltaAngle -= 360;
        } else if (deltaAngle < -180) {
            deltaAngle += 360;
        }
        float direction = MathUtils.clamp(deltaAngle / GamePlay.instance.lowSpeedMaxSteer, -1, 1);
        vehicle.setDirection(direction);
    }
}
