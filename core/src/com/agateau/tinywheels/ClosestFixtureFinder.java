package com.agateau.tinywheels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

/**
 * An helper class to find the closest fixture using a raycast
 */
class ClosestFixtureFinder implements RayCastCallback {
    private Body mIgnoredBody = null;
    private Fixture mFixture = null;

    public Body getIgnoredBody() {
        return mIgnoredBody;
    }

    public void setIgnoredBody(Body ignoredBody) {
        mIgnoredBody = ignoredBody;
    }

    public Fixture run(World world, Vector2 v1, Vector2 v2) {
        mFixture = null;
        world.rayCast(this, v1, v2);
        return mFixture;
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if (fixture.getBody() == mIgnoredBody) {
            return 1;
        } else {
            mFixture = fixture;
            return fraction;
        }
    }
}
