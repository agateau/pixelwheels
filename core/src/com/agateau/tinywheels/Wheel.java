package com.agateau.tinywheels;

import com.agateau.utils.CircularArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
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
    private static final float DRIFT_IMPULSE_REDUCTION = 0.5f; // Limit how much of the lateral velocity is killed when drifting
    private static final float DRAG_FACTOR = 1;
    private static final int SKIDMARK_INTERVAL = 3;

    public static final Vector2 END_DRIFT_POS = new Vector2(-12, -12);

    private static ReflectionPool<Wheel> sPool = new ReflectionPool<Wheel>(Wheel.class);

    private final CircularArray<Vector2> mSkidmarks = new CircularArray<Vector2>(GamePlay.instance.maxSkidmarks) {
        @Override
        protected Vector2 initElement(Vector2 existingElement, Vector2 newElement) {
            if (existingElement == null) {
                existingElement = new Vector2();
            }
            existingElement.set(newElement.x, newElement.y);
            return existingElement;
        }
    };

    private Body mBody;
    private TextureRegion mRegion;
    private GameWorld mGameWorld;
    private boolean mBraking;
    private boolean mCanDrift;
    private float mMaxDrivingForce;
    private boolean mGripEnabled = true;
    private float mGroundSpeed;
    private MapInfo.Material mMaterial;
    private boolean mDrifting = false;

    public static Wheel create(TextureRegion region, GameWorld gameWorld, float posX, float posY, float angle) {
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
        bodyDef.angle = angle * MathUtils.degreesToRadians;
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
        updateGroundInfo();
        if (mGripEnabled) {
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

    public void setGripEnabled(boolean enabled) {
        mGripEnabled = enabled;
    }

    public void adjustSpeed(float amount) {
        if (amount == 0) {
            return;
        }
        final float currentSpeed = mBody.getLinearVelocity().len() * 3.6f;

        final float limit = 1 - 0.2f * Interpolation.sineOut.apply(currentSpeed / GamePlay.instance.maxSpeed);
        amount *= limit;

        float force = mMaxDrivingForce * amount;
        float angle = mBody.getAngle();
        Vector2 pos = mBody.getWorldCenter();
        mBody.applyForce(force * MathUtils.cos(angle), force * MathUtils.sin(angle), pos.x, pos.y, true);
    }

    public void setBraking(boolean braking) {
        mBraking = braking;
    }

    public long getCellId() {
        return mGameWorld.getMapInfo().getCellIdAt(mBody.getWorldCenter().x, mBody.getWorldCenter().y);
    }

    private int mSkidmarkCount = 0; // Used to limit the number of skidmarks created
    private void updateFriction() {
        // Kill lateral velocity
        Vector2 impulse = Box2DUtils.getLateralVelocity(mBody).scl(-mBody.getMass());
        float maxImpulse = (float)GamePlay.instance.maxLateralImpulse / (mBraking ? 2 : 1);
        if (mCanDrift && impulse.len() > maxImpulse) {
            // Drift
            if (!mDrifting) {
                mDrifting = true;
            }
            if (mSkidmarkCount == 0) {
                mSkidmarks.add(mBody.getWorldCenter());
            }
            mSkidmarkCount = (mSkidmarkCount + 1) % SKIDMARK_INTERVAL;
            maxImpulse = Math.max(maxImpulse, impulse.len() - DRIFT_IMPULSE_REDUCTION);
            impulse.limit(maxImpulse);
        } else if (mDrifting) {
            mSkidmarks.add(END_DRIFT_POS);
            mDrifting = false;
        }
        mBody.applyLinearImpulse(impulse, mBody.getWorldCenter(), true);

        // Kill angular velocity
        mBody.applyAngularImpulse(0.1f * mBody.getInertia() * -mBody.getAngularVelocity(), true);
    }

    private void updateGroundInfo() {
        mGroundSpeed = mGameWorld.getMapInfo().getMaxSpeedAt(mBody.getWorldCenter());
        mMaterial = mGameWorld.getMapInfo().getMaterialAt(mBody.getWorldCenter());
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

    public CircularArray<Vector2> getSkidmarks() {
        return mSkidmarks;
    }

    public MapInfo.Material getMaterial() {
        return mMaterial;
    }
}
