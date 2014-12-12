package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * An enemy car
 */
public class EnemyCar extends Vehicle implements Collidable {
    private boolean mHasCollided = false;

    public EnemyCar(GameWorld world, Assets assets, float originX, float originY) {
        super(selectCarTextureRegion(assets), world, originX, originY);

        // Wheels
        TextureRegion wheelRegion = assets.wheel;
        final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
        final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 46f;

        float wheelW = Constants.UNIT_FOR_PIXEL * wheelRegion.getRegionWidth();
        float rightX = getWidth() / 2 - wheelW / 2 + 0.05f;
        float leftX = -rightX;
        float rearY = -getHeight() / 2 + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE;

        Vehicle.WheelInfo info;
        info = addWheel(wheelRegion, leftX, frontY);
        info.steeringFactor = 1;
        info = addWheel(wheelRegion, rightX, frontY);
        info.steeringFactor = 1;
        info = addWheel(wheelRegion, leftX, rearY);
        info.wheel.setCanDrift(true);
        info = addWheel(wheelRegion, rightX, rearY);
        info.wheel.setCanDrift(true);

        mBody.setAwake(false);
    }

    public void setDrivingAngle(float angle) {
        angle = (angle - 90) * MathUtils.degreesToRadians;
        mBody.setTransform(mBody.getPosition(), angle);
    }

    @Override
    public boolean act(float dt) {
        super.act(dt);
        boolean isVisible = mGameWorld.isVisible(getX(), getY());
        if (isVisible) {
            if (mHasCollided) {
                setAccelerating(false);
            } else {
                drive();
            }
        } else {
            mBody.setAwake(false);
        }
        return true;
    }

    private final Vector2 mTmpPos = new Vector2();
    private static final float FORWARD_ANGLE = 20;
    private static final float FORWARD_TIME = 0.5f;
    private void drive() {
        setAccelerating(true);
        setDirection(0);

        moveTmpPos(0);
        float front = mGameWorld.getMaxSpeedAt(mTmpPos);
        if (front == 1.0f) {
            return;
        }

        moveTmpPos(FORWARD_ANGLE);
        float left = mGameWorld.getMaxSpeedAt(mTmpPos);

        moveTmpPos(-FORWARD_ANGLE);
        float right = mGameWorld.getMaxSpeedAt(mTmpPos);

        if (left > right) {
            setDirection(0.5f);
        } else {
            setDirection(-0.5f);
        }
    }

    private void moveTmpPos(float deltaAngle) {
        Vector2 vel = mBody.getLinearVelocity();
        float length = vel.len() * FORWARD_TIME;
        float angle = vel.angle() + deltaAngle;
        Vector2 pos = mBody.getWorldCenter();
        mTmpPos.set(pos.x + length * MathUtils.cosDeg(angle), pos.y + length * MathUtils.sinDeg(angle));
    }

    private static TextureRegion selectCarTextureRegion(Assets assets) {
        return assets.cars.get(MathUtils.random(assets.cars.size - 1));
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        mHasCollided = true;
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {

    }
}
