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
        if (isDead()) {
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
            kill();
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {

    }
}
