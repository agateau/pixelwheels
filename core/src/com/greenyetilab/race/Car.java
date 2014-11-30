package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.greenyetilab.utils.log.NLog;

/**
 * Represents a car on the world
 */
class Car {
    private final Body mBody;
    private final GameWorld mGameWorld;
    private final RevoluteJoint mJointFL;
    private final RevoluteJoint mJointFR;

    public enum State {
        RUNNING,
        BROKEN,
        FINISHED
    }
    private static final float LOW_SPEED_MAX_STEER = 40;
    private static final float HIGH_SPEED_MAX_STEER = 10;

    private static final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
    private static final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 46f;

    private final Sprite mSprite;
    private final Wheel[] mWheels = new Wheel[4];
    private boolean mAccelerating = false;
    private boolean mBraking = false;
    private float mDirection = 0;
    private State mState = State.RUNNING;

    private static final int WHEEL_FL = 0;
    private static final int WHEEL_FR = 1;
    private static final int WHEEL_RL = 2;
    private static final int WHEEL_RR = 3;

    public Car(RaceGame game, GameWorld gameWorld, Vector2 startPosition) {
        mGameWorld = gameWorld;
        Assets assets = game.getAssets();

        float carW = Constants.UNIT_FOR_PIXEL * assets.car.getRegionWidth();
        float carH = Constants.UNIT_FOR_PIXEL * assets.car.getRegionHeight();

        // Main
        mSprite = new Sprite(assets.car);
        mSprite.setSize(carW, carH);
        mSprite.setOriginCenter();

        // Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startPosition.x, startPosition.y);
        mBody = mGameWorld.getBox2DWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(carW / 2, carH / 2);

        // Body fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = 0.4f;
        mBody.createFixture(fixtureDef);

        // Wheels
        float wheelW = Constants.UNIT_FOR_PIXEL * assets.wheel.getRegionWidth();
        float deltaX = carW / 2 - wheelW / 2 + 0.05f;
        float leftX = startPosition.x - deltaX;
        float rightX = startPosition.x + deltaX;
        float rearY = startPosition.y - carH / 2 + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE;

        mWheels[WHEEL_FL] = new Wheel(game, mGameWorld, leftX, frontY);
        mWheels[WHEEL_FR] = new Wheel(game, mGameWorld, rightX, frontY);
        mWheels[WHEEL_RL] = new Wheel(game, mGameWorld, leftX, rearY);
        mWheels[WHEEL_RR] = new Wheel(game, mGameWorld, rightX, rearY);

        mJointFL = joinWheel(mWheels[WHEEL_FL]);
        mJointFR = joinWheel(mWheels[WHEEL_FR]);
        joinWheel(mWheels[WHEEL_RL]);
        joinWheel(mWheels[WHEEL_RR]);
    }

    private RevoluteJoint joinWheel(Wheel wheel) {
        RevoluteJointDef jointDef = new RevoluteJointDef();
        // Call initialize() instead of defining bodies and anchors manually. Defining anchors manually
        // causes Box2D to move the car a bit while it solves the constraints defined by the joints
        jointDef.initialize(mBody, wheel.getBody(), wheel.getBody().getPosition());
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.enableLimit = true;
        return (RevoluteJoint)mGameWorld.getBox2DWorld().createJoint(jointDef);
    }

    public State getState() {
        return mState;
    }

    public float getSpeed() {
        return mBody.getLinearVelocity().len();
    }

    /**
     * Returns the angle the car is facing, not the internal mBody angle
     */
    public float getAngle() {
        return mBody.getAngle() * MathUtils.radiansToDegrees + 90;
    }

    public void act(float dt) {
        if (mState != State.RUNNING) {
            return;
        }

        int wheelsOnFatalGround = 0;
        for(Wheel wheel: mWheels) {
            wheel.act(dt);
            if (wheel.isOnFatalGround()) {
                ++wheelsOnFatalGround;
            }
            if (wheel.isOnFinished()) {
                mState = State.FINISHED;
            }
        }
        if (wheelsOnFatalGround >= 2) {
            mState = State.BROKEN;
        }
        if (mBraking || mAccelerating) {
            float amount = mAccelerating ? 1 : -0.5f;
            for (Wheel wheel: mWheels) {
                wheel.adjustSpeed(amount);
            }
        }
        for (Wheel wheel: mWheels) {
            wheel.setBraking(mBraking);
        }

        float steerFactor = Math.min(mBody.getLinearVelocity().len() / 40f, 1f);
        float steer = LOW_SPEED_MAX_STEER + (HIGH_SPEED_MAX_STEER - LOW_SPEED_MAX_STEER) * steerFactor;
        NLog.i("steerFactor=%f steer=%f", steerFactor, steer);
        float steerAngle = mDirection * steer * MathUtils.degreesToRadians;
        mJointFL.setLimits(steerAngle, steerAngle);
        mJointFR.setLimits(steerAngle, steerAngle);
    }

    public void draw(Batch batch) {
        for(Wheel wheel: mWheels) {
            wheel.draw(batch);
        }
        DrawUtils.drawBodySprite(batch, mBody, mSprite);
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

    public float getX() {
        return mBody.getPosition().x;
    }

    public float getY() {
        return mBody.getPosition().y;
    }
}
