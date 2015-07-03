package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * A wheel
 */
public class Wheel implements Pool.Poolable, Disposable {
    private static final float DRIFT_IMPULSE_REDUCTION = 2; // Limit how much of the lateral velocity is killed when drifting
    private static final float DRAG_FACTOR = 1;

    private static ReflectionPool<Wheel> sPool = new ReflectionPool<Wheel>(Wheel.class);

    private Body mBody;
    private TextureRegion mRegion;
    private GameWorld mGameWorld;
    private boolean mBraking;
    private boolean mCanDrift;
    private float mMaxDrivingForce;
    private float mDisabledGripDelay = 0;
    private float mGroundSpeed;

    public static Wheel create(TextureRegion region, GameWorld gameWorld, float posX, float posY) {
        Wheel obj = sPool.obtain();
        obj.mGameWorld = gameWorld;
        obj.mRegion = region;
        obj.mGroundSpeed = 1;
        obj.mBraking = false;
        obj.mCanDrift = false;
        obj.mMaxDrivingForce = GamePlay.instance.maxDrivingForce;

        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(posX, posY);
        obj.mBody = obj.mGameWorld.getBox2DWorld().createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(w / 2, h / 2);
        obj.mBody.createFixture(polygonShape, 2f);
        polygonShape.dispose();

        return obj;
    }

    public TextureRegion getRegion() {
        return mRegion;
    }

    @Override
    public void dispose() {
        sPool.free(this);
    }

    @Override
    public void reset() {
        mGameWorld.getBox2DWorld().destroyBody(mBody);
    }

    public void act(float delta) {
        updateGroundSpeed();
        if (mDisabledGripDelay > 0) {
            mDisabledGripDelay -= delta;
        } else {
            updateFriction();
        }
        Box2DUtils.applyDrag(mBody, DRAG_FACTOR);
    }

    public Body getBody() {
        return mBody;
    }

    public float getGroundSpeed() {
        return mGroundSpeed;
    }

    public void disableGripFor(float seconds) {
        mDisabledGripDelay = seconds;
    }

    public void adjustSpeed(float amount) {
        if (amount == 0) {
            return;
        }
        final float currentSpeed = mBody.getLinearVelocity().len() * 3.6f;

        // When currentSpeed is between midSpeed and maxSpeed linearly limit the force applied to
        // the wheel so that vehicles accelerate strongly at start then less when they are driving
        // faster
        final float midSpeed = GamePlay.instance.midSpeed;
        final float maxSpeed = GamePlay.instance.maxSpeed;
        final float limit = MathUtils.clamp(1f - (currentSpeed - midSpeed) / (maxSpeed - midSpeed), 0f, 1f);
        amount *= limit;

        float force = mMaxDrivingForce * amount;
        float angle = mBody.getAngle() + MathUtils.PI / 2;
        Vector2 pos = mBody.getWorldCenter();
        mBody.applyForce(force * MathUtils.cos(angle), force * MathUtils.sin(angle), pos.x, pos.y, true);
    }

    public void setBraking(boolean braking) {
        mBraking = braking;
    }

    private void updateFriction() {
        // Kill lateral velocity
        Vector2 impulse = Box2DUtils.getLateralVelocity(mBody).scl(-mBody.getMass());
        float maxImpulse = (float)GamePlay.instance.maxLateralImpulse / (mBraking ? 2 : 1);
        if (mCanDrift && impulse.len() > maxImpulse) {
            mGameWorld.addSkidmarkAt(mBody.getWorldCenter());
            maxImpulse = Math.max(maxImpulse, impulse.len() - DRIFT_IMPULSE_REDUCTION);
            impulse.limit(maxImpulse);
        }
        mBody.applyLinearImpulse(impulse, mBody.getWorldCenter(), true);

        // Kill angular velocity
        mBody.applyAngularImpulse(0.1f * mBody.getInertia() * -mBody.getAngularVelocity(), true);
    }

    private void updateGroundSpeed() {
        mGroundSpeed = mGameWorld.getMapInfo().getMaxSpeedAt(mBody.getWorldCenter());
    }

    public void setCanDrift(boolean canDrift) {
        mCanDrift = canDrift;
    }

    public float getMaxDrivingForce() {
        return mMaxDrivingForce;
    }

    public void setMaxDrivingForce(float maxDrivingForce) {
        mMaxDrivingForce = maxDrivingForce;
    }
}
