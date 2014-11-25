package com.greenyetilab.race;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
* Created by aurelien on 21/11/14.
*/
class Car {
    private final Body mBody;

    public enum State {
        RUNNING,
        BROKEN,
        FINISHED
    }
    private static final float STEER_SPEED = 15;

    public static final float MAX_SPEED = 8000;
    private static final float MIN_SPEED = -100;
    private static final float OVERSPEED_DECAY = 20;

    private static final float REAR_WHEEL_Y = 7;
    private static final float WHEEL_BASE = 48;

    private final Texture mMainImage;
    //private final Image[] mWheels = new Image[4];
    private final TiledMapTileLayer mLayer;
    private float mSpeed = 0;
    private float mMaxSpeed;
    private boolean mAccelerating = false;
    private boolean mBraking = false;
    private float mDirection = 0;
    private float mSteerAngle;
    private State mState = State.RUNNING;

    private static final int WHEEL_FL = 0;
    private static final int WHEEL_FR = 1;
    private static final int WHEEL_RL = 2;
    private static final int WHEEL_RR = 3;

    public Car(RaceGame game, World world, TiledMapTileLayer layer) {
        Assets assets = game.getAssets();
        mLayer = layer;

        float centerX = assets.car.getWidth() / 2;
        float centerY = assets.car.getHeight() / 2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set our mBody's starting position in the world
        bodyDef.position.set(100, 300);

        // Create our mBody in the world using our mBody definition
        mBody = world.createBody(bodyDef);

        // Create shape
        PolygonShape shape = new PolygonShape();
        // Image is vertical!
        shape.setAsBox(assets.car.getHeight() / 2, assets.car.getWidth() / 2);

        // Create fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;
        Fixture fixture = mBody.createFixture(fixtureDef);


        // Wheels
        /*
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
        */

        // Main
        mMainImage = assets.car;
        /*mMainImage.setOrigin(centerX, centerY);
        mMainImage.setPosition(-centerX, -centerY);
        addActor(mMainImage);
        */
    }

    public State getState() {
        return mState;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public float getAngle() {
        return mBody.getAngle() * MathUtils.radiansToDegrees;
    }

    //@Override
    public void act(float dt) {
        if (mState != State.RUNNING) {
            return;
        }
        /*
        if (mBraking) {
            mSpeed = Math.max(mSpeed - 4, MIN_SPEED);
        } else {
            if (mAccelerating) {
                mSpeed = Math.min(mSpeed + 4, MAX_SPEED);
            } else if (mSpeed > 0) {
                // Freewheel
                mSpeed = Math.max(mSpeed - 2, 0);
            } else {
                // Reverse freewheel
                mSpeed = Math.min(mSpeed + 2, 0);
            }
        }
        if (mSpeed > mMaxSpeed) {
            mSpeed -= OVERSPEED_DECAY;
        }
        */
        float angle = mBody.getAngle();
        Vector2 velocity = mBody.getLinearVelocity();
        float speed = velocity.len();
        if (mAccelerating && speed < MAX_SPEED) {
            //mSpeed = Math.min(mSpeed + 100, MAX_SPEED);
            //mBody.applyForceToCenter(mSpeed * MathUtils.cos(angle), mSpeed * MathUtils.sin(angle), true);
            float impulse = 1000;
            mBody.applyLinearImpulse(impulse * MathUtils.cos(angle), impulse * MathUtils.sin(angle), mBody.getPosition().x, mBody.getPosition().y, true);
        }
        if (mBraking && speed > MIN_SPEED) {
            float impulse = -1000;
            mBody.applyLinearImpulse(impulse * MathUtils.cos(angle), impulse * MathUtils.sin(angle), mBody.getPosition().x, mBody.getPosition().y, true);
        }
        mBody.setAngularVelocity(STEER_SPEED * mDirection);//, true);
        /*
        checkCollisions();
        updatePosAndAngle(dt);
        */
    }

    public void draw(Batch batch) {
        Vector2 center = mBody.getPosition();
        float w2 = mMainImage.getWidth() / 2;
        float h2 = mMainImage.getHeight() / 2;
        float angle = mBody.getAngle() - MathUtils.PI / 2;
        batch.draw(mMainImage, center.x - w2, center.y - h2, w2, h2, mMainImage.getWidth(), mMainImage.getHeight(), 1f, 1f,
                angle * MathUtils.radiansToDegrees,
                0, 0, mMainImage.getWidth(), mMainImage.getHeight(), false, false);
    }

    /*
    private static Vector2 mTmp = new Vector2();
    private void checkCollisions() {
        int maxSpeed0 = 0;
        float tileSpeed = 0;
        for(Image wheel: mWheels) {
            mTmp.x = wheel.getX();
            mTmp.y = wheel.getY();
            mTmp = wheel.localToStageCoordinates(mTmp);
            int tx = MathUtils.floor(mTmp.x / RaceGameScreen.WORLD_SCALE / mLayer.getTileWidth());
            int ty = MathUtils.floor(mTmp.y / RaceGameScreen.WORLD_SCALE / mLayer.getTileHeight());
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
                mState = State.FINISHED;
            }
        }
        mMaxSpeed = MAX_SPEED * tileSpeed / mWheels.length;
        if (maxSpeed0 >= 2) {
            NLog.i("Broken!");
            mState = State.BROKEN;
        }
    }
    */

    public void setAccelerating(boolean value) {
        mAccelerating = value;
    }

    public void setBraking(boolean value) {
        mBraking = value;
    }

    public void setDirection(float direction) {
        mDirection = direction;
    }

    /*
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
    */
    public void setPosition(float mapX, float mapY) {

    }
    public float getX() {
        return mBody.getPosition().x;
    }

    public float getY() {
        return mBody.getPosition().y;
    }
}
