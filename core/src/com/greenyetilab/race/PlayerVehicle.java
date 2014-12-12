package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * The player vehicle
 */
public class PlayerVehicle extends Vehicle implements Collidable {
    public PlayerVehicle(TextureRegion region, GameWorld gameWorld, Vector2 startPosition) {
        super(region, gameWorld, startPosition);
    }

    @Override
    public boolean act(float dt) {
        super.act(dt);
        int wheelsOnFatalGround = 0;
        for(WheelInfo info: mWheels) {
            Wheel wheel = info.wheel;
            if (wheel.isOnFatalGround()) {
                ++wheelsOnFatalGround;
            }
            if (wheel.isOnFinished()) {
                mGameWorld.setState(GameWorld.State.FINISHED);
            }
        }
        if (wheelsOnFatalGround >= 2) {
            mGameWorld.setState(GameWorld.State.BROKEN);
        }
        return true;
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        EnemyCar enemy = (EnemyCar)otherFixture.getBody().getUserData();
        if (enemy != null && enemy.isAlive()) {
            mGameWorld.increaseScore(Constants.SCORE_CAR_HIT);
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {

    }
}
