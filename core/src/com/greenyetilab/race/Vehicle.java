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
class Vehicle {
    private final Body mBody;
    protected final GameWorld mGameWorld;
    private boolean mLimitAngle;
    private boolean mCorrectAngle;

    public static class WheelInfo {
        public Wheel wheel;
        public RevoluteJoint joint;
        public float steeringFactor;
    }

    private static final float LOW_SPEED_MAX_STEER = 40;
    private static final float HIGH_SPEED_MAX_STEER = 10;

    private final Sprite mSprite;
    protected final Array<WheelInfo> mWheels = new Array<WheelInfo>();

    private boolean mAccelerating = false;
    private boolean mBraking = false;
    private float mDirection = 0;

    public Vehicle(TextureRegion region, GameWorld gameWorld, Vector2 startPosition) {
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
        mBody.setUserData(this);
    }

    public WheelInfo addWheel(TextureRegion region, float x, float y) {
        WheelInfo info = new WheelInfo();
        info.wheel = new Wheel(region, mGameWorld, getX() + x, getY() + y);
        mWheels.add(info);

        Body body = info.wheel.getBody();
        body.setUserData(this);

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

    public float getSpeed() {
        return mBody.getLinearVelocity().len();
    }

    /**
     * Returns the angle the car is facing, not the internal mBody angle
     */
    public float getAngle() {
        return mBody.getAngle() * MathUtils.radiansToDegrees + 90;
    }

    public boolean getLimitAngle() {
        return mLimitAngle;
    }

    public void setLimitAngle(boolean limitAngle) {
        mLimitAngle = limitAngle;
    }

    public boolean getCorrectAngle() {
        return mCorrectAngle;
    }

    public void setCorrectAngle(boolean correctAngle) {
        mCorrectAngle = correctAngle;
    }

    public void act(float dt) {
        float speedDelta = 0;
        if (mBraking || mAccelerating) {
            speedDelta = mAccelerating ? 1 : -0.5f;
        }

        float steerAngle = 0;
        if (mDirection == 0) {
            if (mCorrectAngle) {
                steerAngle = computeAutoSteerAngle();
            }
        } else {
            float direction = mDirection;
            if (mLimitAngle) {
                float currentAngle = mBody.getLinearVelocity().angle();
                if ((direction > 0 && currentAngle >= 135) || (direction < 0 && currentAngle <= 45)) {
                    direction = 0;
                }
            }
            float steerFactor = Math.min(mBody.getLinearVelocity().len() / 40f, 1f);
            float steer = LOW_SPEED_MAX_STEER + (HIGH_SPEED_MAX_STEER - LOW_SPEED_MAX_STEER) * steerFactor;
            steerAngle = direction * steer;
        }

        steerAngle *= MathUtils.degreesToRadians;
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

    public Vector2 getPosition() {
        return mBody.getPosition();
    }

    private static float normAngle(float angle) {
        while (angle < 0) {
            angle += 360;
        }
        return angle % 360;
    }

    private static float computeAngleDelta(float a1, float a2) {
        float delta = Math.abs(a2 - a1);
        if (delta > 180) {
            delta = 360 - delta;
        }
        return delta;
    }

    private float computeAutoSteerAngle() {
        float angle = normAngle(getAngle());
        float velocityAngle = normAngle(mBody.getLinearVelocity().angle());
        float angleDelta = computeAngleDelta(angle, velocityAngle);
        boolean reverse = angleDelta > 90;

        float targetAngle;
        if (mLimitAngle) {
            targetAngle = 90;
        } else {
            targetAngle = MathUtils.round(angle / 45) * 45;
        }
        if (reverse) {
            targetAngle = (targetAngle + 180) % 360;
        }
        float correctedAngle = (targetAngle - velocityAngle) / 3;
        return reverse ? -correctedAngle : correctedAngle;
    }
}
