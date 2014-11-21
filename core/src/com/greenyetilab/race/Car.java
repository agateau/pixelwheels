package com.greenyetilab.race;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.greenyetilab.utils.log.NLog;

/**
* Created by aurelien on 21/11/14.
*/
class Car extends Group {
    private static final float STEER_SPEED = 10;

    private static final float MAX_SPEED = 800;
    private static final float MIN_SPEED = -50;
    private static final float GROUND_MAX_SPEED = 50;
    private static final float OVERSPEED_DECAY = 10;

    private static final float REAR_WHEEL_Y = 7;
    private static final float WHEEL_BASE = 48;

    private static final Vector2 CAR_SIZE = new Vector2(41, 75);
    private static final Vector2 WHEEL_SIZE = new Vector2(8, 13);

    private final RaceGame mGame;
    private final Image mMainImage;
    private final Image[] mWheels = new Image[4];
    private float mX, mY;
    private float mSpeed = 200;
    private float mAngle = 80;
    private boolean mAccelerating = false;
    private boolean mBraking = false;
    private int mDirection = 0;
    private float mSteerAngle;

    private static final int WHEEL_FL = 0;
    private static final int WHEEL_FR = 1;
    private static final int WHEEL_RL = 2;
    private static final int WHEEL_RR = 3;

    public Car(RaceGame game) {
        mX = 0;
        mY = 0;
        mGame = game;
        Texture texture = new Texture("car.png");
        Texture wheelTexture = new Texture("wheel.png");

        float centerX = texture.getWidth() / 2;
        float centerY = texture.getHeight() / 2;

        // Wheels
        for (int i=0; i < mWheels.length; ++i) {
            Image wheel = new Image(wheelTexture);
            wheel.setOrigin(wheel.getWidth() / 2, wheel.getHeight() / 2);
            mWheels[i] = wheel;
            addActor(wheel);
        }

        float leftX = -centerX - 1;
        float rightX = centerX - WHEEL_SIZE.x + 2;
        float rearY = -centerY + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE;
        mWheels[WHEEL_FL].setPosition(leftX, frontY);
        mWheels[WHEEL_FR].setPosition(rightX, frontY);
        mWheels[WHEEL_RL].setPosition(leftX, rearY);
        mWheels[WHEEL_RR].setPosition(rightX, rearY);

        // Main
        mMainImage = new Image(texture);
        mMainImage.setOrigin(centerX, centerY);
        mMainImage.setPosition(-centerX, -centerY);
        addActor(mMainImage);
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    @Override
    public void act(float dt) {
        if (mBraking) {
            mSpeed = Math.max(mSpeed - 4, MIN_SPEED);
        } else {
            if (mAccelerating) {
                mSpeed = Math.min(mSpeed + 4, MAX_SPEED);
            } else {
                mSpeed = Math.max(mSpeed - 2, 0);
            }
        }
        /*
        if (mSpeed > mMaxSpeed) {
            mSpeed -= OVERSPEED_DECAY;
        }
         */

        updatePosAndAngle(dt);
        updateActors();
}

    public void setAccelerating(boolean value) {
        mAccelerating = value;
    }

    public void setBraking(boolean value) {
        mBraking = value;
    }

    public void setDirection(int direction) {
        mDirection = direction;
    }

    private void updatePosAndAngle(float dt) {
        mSteerAngle = STEER_SPEED * mDirection;

        // We must use double and not float here otherwise the car does not turn when driving slowly
        double angle = MathUtils.degreesToRadians * mAngle;
        double steerAngle = MathUtils.degreesToRadians * mSteerAngle;

        double fWheelX = mX + WHEEL_BASE / 2 * Math.cos(angle);
        double fWheelY = mY + WHEEL_BASE / 2 * Math.sin(angle);

        double rWheelX = mX - WHEEL_BASE / 2 * Math.cos(angle);
        double rWheelY = mY - WHEEL_BASE / 2 * Math.sin(angle);

        rWheelX += mSpeed * dt * Math.cos(angle);
        rWheelY += mSpeed * dt * Math.sin(angle);

        fWheelX += mSpeed * dt * Math.cos(angle + steerAngle);
        fWheelY += mSpeed * dt * Math.sin(angle + steerAngle);

        mX = (float) ((rWheelX + fWheelX) / 2);
        mY = (float) ((rWheelY + fWheelY) / 2);
        mAngle = (float) (MathUtils.radiansToDegrees * Math.atan2(fWheelY - rWheelY, fWheelX - rWheelX));
    }

    private void updateActors() {
        setPosition(mX, mY);
        setRotation(mAngle - 90);
        mWheels[WHEEL_FL].setRotation(mSteerAngle);
        mWheels[WHEEL_FR].setRotation(mSteerAngle);
    }
}
