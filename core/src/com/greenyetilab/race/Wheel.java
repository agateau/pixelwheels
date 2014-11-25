package com.greenyetilab.race;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by aurelien on 25/11/14.
 */
public class Wheel {
    private static final float MAX_LATERAL_IMPULSE = 2;
    private final World mWorld;
    private final Texture mTexture;
    private final Body mBody;

    public Wheel(RaceGame game, World world) {
        mWorld = world;
        mTexture = game.getAssets().wheel;

        float w = mTexture.getWidth();
        float h = mTexture.getHeight();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(100, 300);
        mBody = mWorld.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(w / 2, h / 2);
        mBody.createFixture(polygonShape, 1f);//shape, density
    }

    public void act(float delta) {
        updateFriction();
    }

    public void draw(Batch batch) {
        Vector2 center = mBody.getPosition();
        float w2 = mTexture.getWidth() / 2;
        float h2 = mTexture.getHeight() / 2;
        float angle = mBody.getAngle();
        batch.draw(mTexture, center.x - w2, center.y - h2, w2, h2, mTexture.getWidth(), mTexture.getHeight(), 1f, 1f,
                angle * MathUtils.radiansToDegrees,
                0, 0, mTexture.getWidth(), mTexture.getHeight(), false, false);
    }

    public void adjustSpeed(float amount) {
        if (amount != 0) {
            float impulse = 1000 * amount;
            float angle = mBody.getAngle() + MathUtils.PI / 2;
            mBody.applyLinearImpulse(impulse * MathUtils.cos(angle), impulse * MathUtils.sin(angle), mBody.getPosition().x, mBody.getPosition().y, true);
        }
    }

    Vector2 getLateralVelocity() {
        Vector2 currentRightNormal = mBody.getWorldVector(new Vector2(1, 0));
        float v = currentRightNormal.dot(mBody.getLinearVelocity());
        return currentRightNormal.scl(v);
    }

    void updateFriction() {
        // Kill lateral velocity
        Vector2 impulse = getLateralVelocity().scl(-mBody.getMass());
        if (impulse.len() > MAX_LATERAL_IMPULSE) {
            // Skidding
            impulse.scl(MAX_LATERAL_IMPULSE / impulse.len());
        }
        mBody.applyLinearImpulse(impulse, mBody.getWorldCenter(), true);

        // Kill angular velocity
        mBody.applyAngularImpulse(0.1f * mBody.getInertia() * -mBody.getAngularVelocity(), true);
    }

    float getX() {
        return mBody.getPosition().x;
    }

    float getY() {
        return mBody.getPosition().y;
    }

    void setDirection(float direction) {
        if (direction != 0) {
            float desiredTorque = direction * 15000;
            mBody.applyTorque(desiredTorque, true);
        }
    }
}
