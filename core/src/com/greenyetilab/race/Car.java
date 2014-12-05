package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;

/**
 * Represents a car on the world
 */
class Car {
    private final Body mBody;
    private final GameWorld mGameWorld;

    public static class WheelInfo {
        public Wheel wheel;
        public RevoluteJoint joint;
        public float steeringFactor;
    }

    public enum State {
        RUNNING,
        BROKEN,
        FINISHED
    }
    private static final float LOW_SPEED_MAX_STEER = 40;
    private static final float HIGH_SPEED_MAX_STEER = 10;

    private final Sprite mSprite;
    private final Array<WheelInfo> mWheels = new Array<WheelInfo>();

    private boolean mAccelerating = false;
    private boolean mBraking = false;
    private float mDirection = 0;
    private State mState = State.RUNNING;

    public Car(TextureRegion region, GameWorld gameWorld, Vector2 startPosition) {
        mGameWorld = gameWorld;

        float carW = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float carH = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();

        // Main
        mSprite = new Sprite(region);
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
    }

    public WheelInfo addWheel(TextureRegion region, float x, float y) {
        WheelInfo info = new WheelInfo();
        info.wheel = new Wheel(region, mGameWorld, getX() + x, getY() + y);
        mWheels.add(info);

        Body body = info.wheel.getBody();

        RevoluteJointDef jointDef = new RevoluteJointDef();
        // Call initialize() instead of defining bodies and anchors manually. Defining anchors manually
        // causes Box2D to move the car a bit while it solves the constraints defined by the joints
        jointDef.initialize(mBody, body, body.getPosition());
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.enableLimit = true;
        info.joint = (RevoluteJoint)mGameWorld.getBox2DWorld().createJoint(jointDef);

        return info;
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
        for(WheelInfo info: mWheels) {
            Wheel wheel = info.wheel;
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

        float speedDelta = 0;
        if (mBraking || mAccelerating) {
            speedDelta = mAccelerating ? 1 : -0.5f;
        }

        float steerFactor = Math.min(mBody.getLinearVelocity().len() / 40f, 1f);
        float steer = LOW_SPEED_MAX_STEER + (HIGH_SPEED_MAX_STEER - LOW_SPEED_MAX_STEER) * steerFactor;
        float steerAngle = mDirection * steer * MathUtils.degreesToRadians;

        for(WheelInfo info: mWheels) {
            float angle = info.steeringFactor * steerAngle;
            info.wheel.setBraking(mBraking);
            info.wheel.adjustSpeed(speedDelta);
            info.joint.setLimits(angle, angle);
        }
    }

    public void draw(Batch batch) {
        for(WheelInfo info: mWheels) {
            info.wheel.draw(batch);
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
