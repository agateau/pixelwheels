package com.greenyetilab.race;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * A pilot controlled by the player
 */
public class PlayerPilot implements Pilot {
    private final GameWorld mGameWorld;
    private final Vehicle mVehicle;

    public PlayerPilot(GameWorld gameWorld, Vehicle vehicle) {
        mGameWorld = gameWorld;
        mVehicle = vehicle;
    }

    @Override
    public boolean act(float dt) {
        if (mVehicle.isDead()) {
            mGameWorld.setState(GameWorld.State.BROKEN);
        }
        return true;
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        Object other = otherFixture.getBody().getUserData();
        if (other instanceof EnemyCar) {
            if (!((EnemyCar) other).isDead()) {
                mGameWorld.increaseScore(Constants.SCORE_CAR_HIT);
            }
        }
        if (other instanceof Mine) {
            mVehicle.kill();
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {

    }
}
