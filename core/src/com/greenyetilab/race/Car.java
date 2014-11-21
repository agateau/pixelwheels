package com.greenyetilab.race;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
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

    public static final float MAX_SPEED = 800;
    private static final float MIN_SPEED = -100;
    private static final float OVERSPEED_DECAY = 10;

    private static final float REAR_WHEEL_Y = 7;
    private static final float WHEEL_BASE = 48;

    private final Image mMainImage;
    private final Image[] mWheels = new Image[4];
    private final TiledMapTileLayer mLayer;
    private float mSpeed = 0;
    private float mMaxSpeed;
    private float mAngle = 90;
    private boolean mAccelerating = false;
    private boolean mBraking = false;
    private float mDirection = 0;
    private float mSteerAngle;

    private static final int WHEEL_FL = 0;
    private static final int WHEEL_FR = 1;
    private static final int WHEEL_RL = 2;
    private static final int WHEEL_RR = 3;

    public Car(RaceGame game, TiledMapTileLayer layer) {
        Assets assets = game.getAssets();
        mLayer = layer;

        float centerX = assets.car.getWidth() / 2;
        float centerY = assets.car.getHeight() / 2;

        // Wheels
        for (int i=0; i < mWheels.length; ++i) {
            Image wheel = new Image(assets.wheel);
            wheel.setOrigin(wheel.getWidth() / 2, wheel.getHeight() / 2);
            mWheels[i] = wheel;
            addActor(wheel);
        }

        float leftX = -centerX - 1;
        float rightX = centerX - assets.wheel.getWidth() + 2;
        float rearY = -centerY + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE;
        mWheels[WHEEL_FL].setPosition(leftX, frontY);
        mWheels[WHEEL_FR].setPosition(rightX, frontY);
        mWheels[WHEEL_RL].setPosition(leftX, rearY);
        mWheels[WHEEL_RR].setPosition(rightX, rearY);

        // Main
        mMainImage = new Image(assets.car);
        mMainImage.setOrigin(centerX, centerY);
        mMainImage.setPosition(-centerX, -centerY);
        addActor(mMainImage);
    }

    public float getSpeed() {
        return mSpeed;
    }

    public float getAngle() {
        return mAngle;
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
        if (mSpeed > mMaxSpeed) {
            mSpeed -= OVERSPEED_DECAY;
        }
        checkCollisions();

        updatePosAndAngle(dt);
    }

    private static Vector2 mTmp = new Vector2();
    private void checkCollisions() {
        int maxSpeed0 = 0;
        float tileSpeed = 0;
        for(Image wheel: mWheels) {
            mTmp.x = wheel.getX();
            mTmp.y = wheel.getY();
            mTmp = wheel.localToStageCoordinates(mTmp);
            int tx = MathUtils.floor(mTmp.x / mLayer.getTileWidth());
            int ty = MathUtils.floor(mTmp.y / mLayer.getTileHeight());
            TiledMapTileLayer.Cell cell = mLayer.getCell(tx, ty);
            if (cell == null) {
                continue;
            }
            MapProperties properties = cell.getTile().getProperties();
            String txt = properties.get("max_speed", String.class);
            float tileMaxSpeed = txt == null ? 1.0f : Float.valueOf(txt);
            tileSpeed += tileMaxSpeed;
            if (tileMaxSpeed == 0) {
                ++maxSpeed0;
            }
            if (properties.containsKey("finish")) {
                NLog.i("Finish!");
            }
        }
        mMaxSpeed = MAX_SPEED * tileSpeed / mWheels.length;
        if (maxSpeed0 >= 2) {
            NLog.i("Broken!");
        }
    }

    public void setAccelerating(boolean value) {
        mAccelerating = value;
    }

    public void setBraking(boolean value) {
        mBraking = value;
    }

    public void setDirection(float direction) {
        mDirection = direction;
    }

    private void updatePosAndAngle(float dt) {
        mSteerAngle = STEER_SPEED * mDirection;

        // We must use double and not float here otherwise the car does not turn when driving slowly
        double angle = MathUtils.degreesToRadians * mAngle;
        double steerAngle = MathUtils.degreesToRadians * mSteerAngle;

        double fWheelX = getX() + WHEEL_BASE / 2 * Math.cos(angle);
        double fWheelY = getY() + WHEEL_BASE / 2 * Math.sin(angle);

        double rWheelX = getX() - WHEEL_BASE / 2 * Math.cos(angle);
        double rWheelY = getY() - WHEEL_BASE / 2 * Math.sin(angle);

        rWheelX += mSpeed * dt * Math.cos(angle);
        rWheelY += mSpeed * dt * Math.sin(angle);

        fWheelX += mSpeed * dt * Math.cos(angle + steerAngle);
        fWheelY += mSpeed * dt * Math.sin(angle + steerAngle);

        setPosition(
                (float) ((rWheelX + fWheelX) / 2),
                (float) ((rWheelY + fWheelY) / 2)
        );
        mAngle = (float) (MathUtils.radiansToDegrees * Math.atan2(fWheelY - rWheelY, fWheelX - rWheelX));
        setRotation(mAngle - 90);
        mWheels[WHEEL_FL].setRotation(mSteerAngle);
        mWheels[WHEEL_FR].setRotation(mSteerAngle);
    }
}
