package com.agateau.tinywheels;

/**
 * Handles collisions
 */
public class GroundCollisionHandlerComponent implements Racer.Component {
    private final Vehicle mVehicle;
    private final MapInfo mMapInfo;
    private final LapPositionComponent mLapPositionComponent;

    public enum State {
        NORMAL,
        FALLING,
        RECOVERING
    }

    private State mState = State.NORMAL;
    private float mDelay;

    public GroundCollisionHandlerComponent(Vehicle vehicle, MapInfo mapInfo, LapPositionComponent lapPositionComponent) {
        mVehicle = vehicle;
        mMapInfo = mapInfo;
        mLapPositionComponent = lapPositionComponent;
    }

    public State getState() {
        return mState;
    }

    @Override
    public void act(float delta) {
        switch (mState) {
        case NORMAL:
            actNormal();
            break;
        case FALLING:
            actFalling(delta);
            break;
        case RECOVERING:
            actRecovering(delta);
            break;
        }
    }

    private void actNormal() {
        int wheelsInHole = 0;
        for(Vehicle.WheelInfo info: mVehicle.getWheelInfos()) {
            Wheel wheel = info.wheel;
            if (wheel.getGroundSpeed() == 0) {
                ++wheelsInHole;
            }
        }
        if (wheelsInHole >= 2) {
            mDelay = 1;
            mState = State.FALLING;
        }
    }

    private void actFalling(float delta) {
        mDelay -= delta;
        if (mDelay <= 0) {
            mState = State.RECOVERING;
        }
    }

    private void actRecovering(float delta) {
        float distance = mLapPositionComponent.getLapDistance();
        OrientedPoint point = mMapInfo.getValidPosition(mVehicle.getBody().getWorldCenter(), distance);
        mVehicle.teleport(point);
        mState = State.NORMAL;
    }
}
