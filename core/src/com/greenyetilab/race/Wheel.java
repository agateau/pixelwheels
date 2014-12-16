package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * A wheel
 */
public class Wheel {
    private static final float MAX_LATERAL_IMPULSE = 3.5f;
    private static final float DRAG_FACTOR = 1;
    private final TextureRegion mRegion;
    private final Body mBody;
    private final GameWorld mGameWorld;
    private boolean mOnFinished = false;
    private boolean mOnFatalGround = false;
    private boolean mBraking = false;
    private boolean mCanDrift;

    public Wheel(TextureRegion region, GameWorld gameWorld, float posX, float posY) {
        mGameWorld = gameWorld;
        mRegion = region;

        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(posX, posY);
        mBody = mGameWorld.getBox2DWorld().createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(w / 2, h / 2);
        mBody.createFixture(polygonShape, 1f);
    }

    public void act(float delta) {
        checkCollisions();
        updateFriction();
    }

    public void draw(Batch batch) {
        DrawUtils.drawBodyRegion(batch, mBody, mRegion);
    }

    public Body getBody() {
        return mBody;
    }

    public boolean isOnFinished() {
        return mOnFinished;
    }

    public boolean isOnFatalGround() {
        return mOnFatalGround;
    }

    public void adjustSpeed(float amount) {
        if (amount == 0) {
            return;
        }
        float force = 50 * amount;
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
        float maxInpulse = MAX_LATERAL_IMPULSE / (mBraking ? 2 : 1);
        if (mCanDrift && impulse.len() > maxInpulse) {
            mGameWorld.addSkidmarkAt(mBody.getWorldCenter());
            impulse.limit(maxInpulse);
        }
        mBody.applyLinearImpulse(impulse, mBody.getWorldCenter(), true);

        // Kill angular velocity
        mBody.applyAngularImpulse(0.1f * mBody.getInertia() * -mBody.getAngularVelocity(), true);

        // Drag
        Box2DUtils.applyDrag(mBody, DRAG_FACTOR);
    }

    private void checkCollisions() {
        float maxSpeed = mGameWorld.getMapInfo().getMaxSpeedAt(mBody.getWorldCenter());
        mOnFatalGround = maxSpeed == 0f;
        if (maxSpeed < 1f) {
            Box2DUtils.applyDrag(mBody, (1 - maxSpeed) * DRAG_FACTOR * 4);
        }
    }


    public void setCanDrift(boolean canDrift) {
        mCanDrift = canDrift;
    }
}
