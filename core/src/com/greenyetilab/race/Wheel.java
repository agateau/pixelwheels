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
    private static final float MAX_LATERAL_IMPULSE = 2f;
    private static final float DRAG_FACTOR = 1;
    private final World mWorld;
    private final Sprite mSprite;
    private final Body mBody;

    public Wheel(RaceGame game, World world, float posX, float posY) {
        mWorld = world;

        Texture texture = game.getAssets().wheel;
        mSprite = new Sprite(texture);

        float w = Constants.UNIT_FOR_PIXEL * texture.getWidth();
        float h = Constants.UNIT_FOR_PIXEL * texture.getHeight();
        mSprite.setSize(w, h);
        mSprite.setOriginCenter();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(posX, posY);
        mBody = mWorld.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(w / 2, h / 2);
        mBody.createFixture(polygonShape, 1f);
    }

    public void act(float delta) {
        updateFriction();
    }

    public void draw(Batch batch) {
        DrawUtils.drawBodySprite(batch, mBody, mSprite);
    }

    public void adjustSpeed(float amount) {
        if (amount == 0) {
            return;
        }
        float force = 40 * amount;
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
            NMessageBus.post("skid", this);
            //impulse.scl(MAX_LATERAL_IMPULSE / impulse.len());
        }
        mBody.applyLinearImpulse(impulse, mBody.getWorldCenter(), true);

        // Kill angular velocity
        mBody.applyAngularImpulse(0.1f * mBody.getInertia() * -mBody.getAngularVelocity(), true);

        // Drag
        Vector2 currentForwardNormal = getForwardVelocity();
        float currentForwardSpeed = currentForwardNormal.len();
        float dragForceMagnitude = -DRAG_FACTOR * currentForwardSpeed;
        float angle = mBody.getAngle() + MathUtils.PI / 2;
        mBody.applyForce(dragForceMagnitude * MathUtils.cos(angle), dragForceMagnitude * MathUtils.sin(angle),
                mBody.getWorldCenter().x, mBody.getWorldCenter().y, true);
    }

    public Body getBody() {
        return mBody;
    }
}
