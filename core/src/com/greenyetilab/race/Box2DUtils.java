package com.greenyetilab.race;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * A set of utility functions for Box2D
 */
public class Box2DUtils {
    public static Vector2 getForwardVelocity(Body body) {
        Vector2 currentRightNormal = body.getWorldVector(new Vector2(0, 1));
        float v = currentRightNormal.dot(body.getLinearVelocity());
        return currentRightNormal.scl(v);
    }

    public static Vector2 getLateralVelocity(Body body) {
        Vector2 currentRightNormal = body.getWorldVector(new Vector2(1, 0));
        float v = currentRightNormal.dot(body.getLinearVelocity());
        return currentRightNormal.scl(v);
    }

    public static void applyDrag(Body body, float factor) {
        Vector2 currentForwardNormal = getForwardVelocity(body);
        float currentForwardSpeed = currentForwardNormal.len();
        float dragForceMagnitude = -factor * currentForwardSpeed;
        float angle = body.getAngle() + MathUtils.PI / 2;
        body.applyForce(dragForceMagnitude * MathUtils.cos(angle), dragForceMagnitude * MathUtils.sin(angle),
                body.getWorldCenter().x, body.getWorldCenter().y, true);
    }

}
