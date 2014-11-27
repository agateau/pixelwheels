package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.greenyetilab.utils.log.NLog;

/**
* Created by aurelien on 21/11/14.
*/
class Car {
    private final Body mBody;
    private final World mWorld;
    private final RevoluteJoint mJointFL;
    private final RevoluteJoint mJointFR;

    public enum State {
        RUNNING,
        BROKEN,
        FINISHED
    }
    private static final float STEER_SPEED = 30;

    public static final float MAX_SPEED = 8000;
    private static final float MIN_SPEED = -100;
    private static final float OVERSPEED_DECAY = 20;

    private static final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
    private static final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 46f;

    private final Sprite mSprite;
    private final Wheel[] mWheels = new Wheel[4];
    private final TiledMapTileLayer mLayer;
    private float mSpeed = 0;
    private float mMaxSpeed;
    private boolean mAccelerating = false;
    private boolean mBraking = false;
    private float mDirection = 0;
    private State mState = State.RUNNING;

    private static final int WHEEL_FL = 0;
    private static final int WHEEL_FR = 1;
    private static final int WHEEL_RL = 2;
    private static final int WHEEL_RR = 3;

    public Car(RaceGame game, World world, TiledMapTileLayer layer, Vector2 startPosition) {
        mWorld = world;
        Assets assets = game.getAssets();
        mLayer = layer;

        float carW = Constants.UNIT_FOR_PIXEL * assets.car.getWidth();
        float carH = Constants.UNIT_FOR_PIXEL * assets.car.getHeight();

        // Main
        mSprite = new Sprite(assets.car);
        mSprite.setSize(carW, carH);
        mSprite.setOriginCenter();

        // Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startPosition.x, startPosition.y);
        mBody = world.createBody(bodyDef);

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
        float wheelW = Constants.UNIT_FOR_PIXEL * assets.wheel.getWidth();
        float deltaX = carW / 2 - wheelW / 2 + 0.05f;
        float leftX = startPosition.x - deltaX;
        float rightX = startPosition.x + deltaX;
        float rearY = startPosition.y - carH / 2 + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE;

        mWheels[WHEEL_FL] = new Wheel(game, world, leftX, frontY);
        mWheels[WHEEL_FR] = new Wheel(game, world, rightX, frontY);
        mWheels[WHEEL_RL] = new Wheel(game, world, leftX, rearY);
        mWheels[WHEEL_RR] = new Wheel(game, world, rightX, rearY);

        mJointFL = joinWheel(mWheels[WHEEL_FL]);
        mJointFR = joinWheel(mWheels[WHEEL_FR]);
        joinWheel(mWheels[WHEEL_RL]);
        joinWheel(mWheels[WHEEL_RR]);
    }

    private RevoluteJoint joinWheel(Wheel wheel) {
        RevoluteJointDef jointDef = new RevoluteJointDef();
        // Call initialize() instead of defining bodies and anchors manually as this causes the
        // world to move the car a bit as it solve the constraints defined by the joints
        jointDef.initialize(mBody, wheel.getBody(), wheel.getBody().getPosition());
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.enableLimit = true;
        return (RevoluteJoint)mWorld.createJoint(jointDef);
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

    public void act(float dt) {
        if (mState != State.RUNNING) {
            return;
        }

        for(Wheel wheel: mWheels) {
            wheel.act(dt);
        }
        if (mBraking || mAccelerating) {
            float amount = mAccelerating ? 1 : -1;
            for (Wheel wheel: mWheels) {
                wheel.adjustSpeed(amount);
            }
        }
        float steerAngle = mDirection * STEER_SPEED * MathUtils.degreesToRadians;
        mJointFL.setLimits(steerAngle, steerAngle);
        mJointFR.setLimits(steerAngle, steerAngle);
        checkCollisions();
    }

    public void draw(Batch batch) {
        for(Wheel wheel: mWheels) {
            wheel.draw(batch);
        }
        DrawUtils.drawBodySprite(batch, mBody, mSprite);
    }

    private void checkCollisions() {
        int maxSpeed0 = 0;
        float tileSpeed = 0;
        float tileW = Constants.UNIT_FOR_PIXEL * mLayer.getTileWidth();
        float tileH = Constants.UNIT_FOR_PIXEL * mLayer.getTileHeight();
        for(Wheel wheel: mWheels) {
            Vector2 pos = wheel.getBody().getWorldCenter();
            int tx = MathUtils.floor(pos.x / tileW);
            int ty = MathUtils.floor(pos.y / tileH);
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
