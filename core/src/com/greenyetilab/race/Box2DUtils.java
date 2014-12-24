package com.greenyetilab.race;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * A set of utility functions for Box2D
 */
public class Box2DUtils {
    private static final Vector2 FORWARD_VECTOR = new Vector2(0, 1);
    private static final Vector2 LATERAL_VECTOR = new Vector2(1, 0);

    public static Vector2 getForwardVelocity(Body body) {
        Vector2 currentRightNormal = body.getWorldVector(FORWARD_VECTOR);
        float v = currentRightNormal.dot(body.getLinearVelocity());
        return currentRightNormal.scl(v);
    }

    public static Vector2 getLateralVelocity(Body body) {
        Vector2 currentRightNormal = body.getWorldVector(LATERAL_VECTOR);
        float v = currentRightNormal.dot(body.getLinearVelocity());
        return currentRightNormal.scl(v);
    }

    public static void applyDrag(Body body, float factor) {
        Vector2 dragForce = body.getLinearVelocity().scl(-factor);
        body.applyForce(dragForce, body.getWorldCenter(), true);
    }

    public static Body createStaticBox(World world, float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width / 2, y + height / 2);
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        body.createFixture(shape, 1);
        return body;
    }

    public static void setCollisionInfo(Body body, int categoryBits, int maskBits) {
        for (Fixture fixture : body.getFixtureList()) {
            Filter filter = fixture.getFilterData();
            filter.categoryBits = (short)categoryBits;
            filter.maskBits = (short)maskBits;
            fixture.setFilterData(filter);
        }
    }
}
