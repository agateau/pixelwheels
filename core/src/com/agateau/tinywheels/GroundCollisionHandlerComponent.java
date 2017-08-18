package com.agateau.tinywheels;

/**
 * Handles collisions
 */
public class GroundCollisionHandlerComponent implements Racer.Component {
    private final Vehicle mVehicle;

    public enum State {
        NORMAL,
        FALLING,
        RECOVERING
    }

    private State mState = State.NORMAL;
    private float mDelay;
    private float mGoodX = 0;
    private float mGoodY = 0;

    public GroundCollisionHandlerComponent(Vehicle vehicle) {
        mVehicle = vehicle;
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
        } else if (wheelsInHole == 0) {
            mGoodX = mVehicle.getX();
            mGoodY = mVehicle.getY();
        }
    }

    private void actFalling(float delta) {
        mDelay -= delta;
        if (mDelay <= 0) {
            mState = State.RECOVERING;
        }
    }

    private void actRecovering(float delta) {
        float dx = mVehicle.getX() - mGoodX;
        float dy = mVehicle.getY() - mGoodY;
        mVehicle.teleport(mGoodX - dx, mGoodY - dy);
        mState = State.NORMAL;
    }
}
