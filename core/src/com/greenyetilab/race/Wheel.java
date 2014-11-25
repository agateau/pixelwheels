package com.greenyetilab.race;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.greenyetilab.utils.log.NLog;

/**
 * Created by aurelien on 25/11/14.
 */
public class Wheel {
    private static final float MAX_LATERAL_IMPULSE = 3;
    private static final float DRAG_FACTOR = 2;
    private final World mWorld;
    private final Sprite mSprite;
    private final Body mBody;

    public Wheel(RaceGame game, World world) {
        mWorld = world;

        Texture texture = game.getAssets().wheel;
        mSprite = new Sprite(texture);

        float hfw = texture.getHeight() / (float)texture.getWidth();
        float w = Constants.UNIT_FOR_PIXEL * texture.getWidth();
        float h = w * hfw;
        mSprite.setSize(w, h);
        mSprite.setOriginCenter();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
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
        float x = center.x;
        float y = center.y;
        mSprite.setPosition(x - mSprite.getWidth() / 2, y - mSprite.getHeight() / 2);
        mSprite.setRotation(mBody.getAngle() * MathUtils.radiansToDegrees);
        mSprite.draw(batch);
    }

    public void adjustSpeed(float amount) {
        if (amount == 0) {
            return;
        }
        float force = 100 * amount;
        float angle = mBody.getAngle() + MathUtils.PI / 2;
        Vector2 pos = mBody.getWorldCenter();
        mBody.applyForce(force * MathUtils.cos(angle), force * MathUtils.sin(angle), pos.x, pos.y, true);
    }

    Vector2 getLateralVelocity() {
        Vector2 currentRightNormal = mBody.getWorldVector(new Vector2(1, 0));
        float v = currentRightNormal.dot(mBody.getLinearVelocity());
        return currentRightNormal.scl(v);
    }

    Vector2 getForwardVelocity() {
        Vector2 currentRightNormal = mBody.getWorldVector(new Vector2(0, 1));
        float v = currentRightNormal.dot(mBody.getLinearVelocity());
        return currentRightNormal.scl(v);
    }

    void updateFriction() {
        // Kill lateral velocity
        Vector2 impulse = getLateralVelocity().scl(-mBody.getMass());
        if (impulse.len() > MAX_LATERAL_IMPULSE) {
            // Skidding
            NLog.i("Skidding");
            impulse.scl(MAX_LATERAL_IMPULSE / impulse.len());
        } else {
            NLog.i("!Skidding");
        }
        mBody.applyLinearImpulse(impulse, mBody.getWorldCenter(), true);

        // Kill angular velocity
        mBody.applyAngularImpulse(0.1f * mBody.getInertia() * -mBody.getAngularVelocity(), true);

        // Drag
        Vector2 currentForwardNormal = getForwardVelocity();
        float currentForwardSpeed = currentForwardNormal.len();
        float dragForceMagnitude = -DRAG_FACTOR * currentForwardSpeed;
        float angle = mBody.getAngle();
        mBody.applyForce(dragForceMagnitude * MathUtils.cos(angle), dragForceMagnitude * MathUtils.sin(angle),
                mBody.getWorldCenter().x, mBody.getWorldCenter().y, true);
    }

    public Body getBody() {
        return mBody;
    }
}
