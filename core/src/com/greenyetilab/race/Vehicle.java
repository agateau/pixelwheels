package com.greenyetilab.race;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Represents a car on the world
 */
class Vehicle implements GameObject, Disposable, Collidable {
    private static final float DYING_DURATION = 0.5f;
    private Pilot mPilot;

    public static class WheelInfo {
        public Wheel wheel;
        public RevoluteJoint joint;
        public float steeringFactor;
    }

    private static enum State {
        ALIVE,
        DYING,
        DEAD
    }

    protected final Body mBody;
    protected final GameWorld mGameWorld;
    private boolean mLimitAngle;
    private boolean mCorrectAngle;

    private static final float LOW_SPEED_MAX_STEER = 40;
    private static final float HIGH_SPEED_MAX_STEER = 10;

    private final TextureRegion mRegion;
    protected final Array<WheelInfo> mWheels = new Array<WheelInfo>();

    private boolean mAccelerating = false;
    private boolean mBraking = false;
    private float mDirection = 0;
    private float mKilledTime;

    private State mState = State.ALIVE;

    public Vehicle(TextureRegion region, GameWorld gameWorld, Vector2 startPosition) {
        this(region, gameWorld, startPosition.x, startPosition.y);
    }

    public Vehicle(TextureRegion region, GameWorld gameWorld, float originX, float originY) {
        mGameWorld = gameWorld;

        float carW = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float carH = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();

        // Main
        mRegion = region;

        // Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(originX, originY);
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
        shape.dispose();

        mBody.setUserData(this);
    }

    @Override
    public void dispose() {
        for (WheelInfo info : mWheels) {
            info.wheel.dispose();
        }
    }

    public WheelInfo addWheel(TextureRegion region, float x, float y) {
        WheelInfo info = new WheelInfo();
        info.wheel = Wheel.create(region, mGameWorld, getX() + x, getY() + y);
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

    public float getWidth() {
        return Constants.UNIT_FOR_PIXEL * mRegion.getRegionWidth();
    }

    public float getHeight() {
        return Constants.UNIT_FOR_PIXEL * mRegion.getRegionHeight();
    }

    public void setPilot(Pilot pilot) {
        mPilot = pilot;
    }

    public void setInitialAngle(float angle) {
        angle = (angle - 90) * MathUtils.degreesToRadians;
        mBody.setTransform(mBody.getPosition(), angle);
    }

    @Override
    public boolean act(float dt) {
        if (mState != State.ALIVE) {
            mBraking = false;
            mAccelerating = false;
        }
        if (mState == State.DYING) {
            actDying(dt);
        }

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
        for (WheelInfo info : mWheels) {
            float angle = info.steeringFactor * steerAngle;
            info.wheel.setBraking(mBraking);
            info.wheel.adjustSpeed(speedDelta);
            info.joint.setLimits(angle, angle);
            info.wheel.act(dt);
        }

        if (mState == State.ALIVE) {
            checkGroundCollisions();
        }
        return mPilot.act(dt);
    }

    private void checkGroundCollisions() {
        int wheelsOnFatalGround = 0;
        for(WheelInfo info: mWheels) {
            Wheel wheel = info.wheel;
            if (wheel.isOnFatalGround()) {
                ++wheelsOnFatalGround;
            }
        }
        if (wheelsOnFatalGround >= 2) {
            kill();
        }
    }

    private void actDying(float dt) {
        if (mKilledTime == 0) {
            onJustDied();
        }
        mKilledTime += dt;
        if (mKilledTime >= DYING_DURATION) {
            mState = State.DEAD;
        }
    }

    protected void onJustDied() {

    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex != Constants.Z_VEHICLES) {
            return;
        }
        for(WheelInfo info: mWheels) {
            info.wheel.draw(batch);
        }
        Color oldColor = batch.getColor();
        if (mState != State.ALIVE) {
            float k = mState == State.DEAD ? 1 : (mKilledTime / DYING_DURATION);
            float rgb = MathUtils.lerp(1, 0.3f, k);
            batch.setColor(rgb, rgb, rgb, 1);
        }
        DrawUtils.drawBodyRegion(batch, mBody, mRegion);
        if (mState != State.ALIVE) {
            batch.setColor(oldColor);
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

    public float getX() {
        return mBody.getPosition().x;
    }

    public float getY() {
        return mBody.getPosition().y;
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

    protected void kill() {
        if (mState == State.ALIVE) {
            mState = State.DYING;
            mKilledTime = 0;
        }
    }

    public boolean isDead() {
        return mState == State.DEAD;
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        mPilot.beginContact(contact, otherFixture);
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {
        mPilot.endContact(contact, otherFixture);
    }
}
