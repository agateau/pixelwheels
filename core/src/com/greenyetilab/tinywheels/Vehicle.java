package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

/**
 * Represents a car on the world
 */
class Vehicle implements Disposable {
    public static class WheelInfo {
        public Wheel wheel;
        public RevoluteJoint joint;
        public float steeringFactor;
    }

    protected final Body mBody;
    protected final GameWorld mGameWorld;

    private final TextureRegion mRegion;
    protected final Array<WheelInfo> mWheels = new Array<WheelInfo>();
    private String mId;
    private String mName;

    private boolean mAccelerating = false;
    private boolean mBraking = false;
    private float mDirection = 0;
    private float mTurboTime = -1;

    private ArrayMap<TiledMapTileLayer.Cell, Float> mTurboCellMap = new ArrayMap<TiledMapTileLayer.Cell, Float>(false /* ordered */, 8);

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
        shape.set(Box2DUtils.createOctogon(carW, carH, 0.5f, 0.5f));

        // Body fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = GamePlay.instance.vehicleDensity / 10.0f;
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = GamePlay.instance.vehicleRestitution / 10.0f;
        mBody.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void dispose() {
        for (WheelInfo info : mWheels) {
            info.wheel.dispose();
        }
        mGameWorld.getBox2DWorld().destroyBody(mBody);
    }

    public WheelInfo addWheel(TextureRegion region, float x, float y) {
        WheelInfo info = new WheelInfo();
        info.wheel = Wheel.create(region, mGameWorld, getX() + x, getY() + y);
        mWheels.add(info);

        Body body = info.wheel.getBody();
        body.setUserData(mBody.getUserData());

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

    public void setUserData(Object userData) {
        mBody.setUserData(userData);
        for (WheelInfo info : mWheels) {
            info.wheel.getBody().setUserData(userData);
        }
    }

    public void setCollisionInfo(int categoryBits, int maskBits) {
        Box2DUtils.setCollisionInfo(mBody, categoryBits, maskBits);
        for (WheelInfo info : mWheels) {
            Box2DUtils.setCollisionInfo(info.wheel.getBody(), categoryBits, maskBits);
        }
    }

    public Array<WheelInfo> getWheelInfos() {
        return mWheels;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Body getBody() {
        return mBody;
    }

    public TextureRegion getRegion() {
        return mRegion;
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

    public float getWidth() {
        return Constants.UNIT_FOR_PIXEL * mRegion.getRegionWidth();
    }

    public float getHeight() {
        return Constants.UNIT_FOR_PIXEL * mRegion.getRegionHeight();
    }

    public void setInitialAngle(float angle) {
        angle = (angle - 90) * MathUtils.degreesToRadians;
        mBody.setTransform(mBody.getPosition(), angle);
    }

    public void act(float dt) {
        final GamePlay GP = GamePlay.instance;

        float speedDelta = 0;
        if (mBraking || mAccelerating) {
            speedDelta = mAccelerating ? 1 : -0.8f;
        }

        float steerAngle = 0;
        if (mDirection != 0) {
            float direction = mDirection;
            float speed = mBody.getLinearVelocity().len() * 3.6f;
            float steer;
            if (speed < GP.lowSpeed) {
                steer = MathUtils.lerp(100, GP.lowSpeedMaxSteer, speed / GP.lowSpeed);
            } else {
                float factor = Math.min((speed - GP.lowSpeed) / (GP.maxSpeed - GP.lowSpeed), 1);
                steer = MathUtils.lerp(GP.lowSpeedMaxSteer, GP.highSpeedMaxSteer, factor);
            }
            steerAngle = direction * steer;
        }
        float turboStrength = 0;
        if (mTurboTime >= 0) {
            turboStrength = GP.turboStrength * Interpolation.pow2.apply(1f - mTurboTime / GP.turboDuration);
            mTurboTime += dt;
            if (mTurboTime > GP.turboDuration) {
                mTurboTime = -1;
            }
        }

        boolean turboOn = mTurboTime > 0;

        steerAngle *= MathUtils.degreesToRadians;
        float groundSpeed = 0;
        for (WheelInfo info : mWheels) {
            float angle = info.steeringFactor * steerAngle;
            info.wheel.setBraking(mBraking);
            info.wheel.adjustSpeed(speedDelta);
            info.joint.setLimits(angle, angle);
            info.wheel.setTurboStrength(turboStrength);
            info.wheel.act(dt);
            float wheelGroundSpeed = info.wheel.getGroundSpeed();
            groundSpeed += wheelGroundSpeed;
            TiledMapTileLayer.Cell cell = info.wheel.getCell();
            boolean isTurboCell = wheelGroundSpeed > 1;
            if (isTurboCell && (!turboOn || !alreadyTriggeredTurboCell(cell))) {
                triggerTurbo();
                addTriggeredTurboCell(cell);
            }
        }
        updateTriggeredTurboTiles(dt);

        groundSpeed /= mWheels.size;

        if (groundSpeed < 1f && !turboOn) {
            Box2DUtils.applyDrag(mBody, (1 - groundSpeed) * GP.groundDragFactor);
        }
    }

    private boolean alreadyTriggeredTurboCell(TiledMapTileLayer.Cell cell) {
        return mTurboCellMap.containsKey(cell);
    }

    private void addTriggeredTurboCell(TiledMapTileLayer.Cell tile) {
        mTurboCellMap.put(tile, GamePlay.instance.turboDuration);
    }

    private void updateTriggeredTurboTiles(float delta) {
        for (int idx = mTurboCellMap.size - 1; idx >= 0; --idx) {
            float duration = mTurboCellMap.getValueAt(idx) - delta;
            if (duration <= 0) {
                mTurboCellMap.removeIndex(idx);
            } else {
                mTurboCellMap.setValue(idx, duration);
            }
        }
    }

    public boolean isAccelerating() {
        return mAccelerating;
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

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public float getTurboTime() {
        return mTurboTime;
    }

    public void triggerTurbo() {
        mTurboTime = 0;
    }
}
