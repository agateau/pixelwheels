package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Disposable;

/**
 * The player character
 */
public class PlayerVehicle implements GameObject, Collidable, Disposable {
    private final GameWorld mGameWorld;
    private final Vehicle mVehicle;
    private final VehicleRenderer mVehicleRenderer;
    private final HealthComponent mHealthComponent = new HealthComponent();
    private final GroundCollisionHandlerComponent mGroundCollisionHandlerComponent;
    private final Pilot mPilot;

    public PlayerVehicle(Assets assets, GameWorld gameWorld, Vehicle vehicle) {
        mGameWorld = gameWorld;
        mHealthComponent.setInitialHealth(Constants.PLAYER_HEALTH);

        mVehicle = vehicle;
        mVehicle.setCollisionInfo(CollisionCategories.PLAYER,
                CollisionCategories.WALL
                | CollisionCategories.AI_VEHICLE | CollisionCategories.FLAT_AI_VEHICLE
                | CollisionCategories.GIFT);

        mVehicleRenderer = new VehicleRenderer(mVehicle, mHealthComponent);
        mGroundCollisionHandlerComponent = new GroundCollisionHandlerComponent(mVehicle, mHealthComponent);
        mPilot = new PlayerPilot(assets, gameWorld, mVehicle, mHealthComponent);
    }

    public Vehicle getVehicle() {
        return mVehicle;
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        Object other = otherFixture.getBody().getUserData();
        if (other instanceof Gift) {
            Gift gift = (Gift)other;
            gift.pick();
            mGameWorld.adjustScore(Constants.SCORE_GIFT_PICK, gift.getX(), gift.getY());
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {
    }

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {
    }

    @Override
    public void dispose() {
        mVehicle.dispose();
    }

    @Override
    public boolean act(float delta) {
        boolean keep = mVehicle.act(delta);
        if (keep) {
            keep = mPilot.act(delta);
        }
        if (keep) {
            keep = mGroundCollisionHandlerComponent.act(delta);
        }
        if (keep) {
            keep = mHealthComponent.act(delta);
        }
        if (!keep) {
            dispose();
            mGameWorld.setState(GameWorld.State.BROKEN);
        }
        return keep;
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        mVehicleRenderer.draw(batch, zIndex);
    }

    @Override
    public float getX() {
        return mVehicle.getX();
    }

    @Override
    public float getY() {
        return mVehicle.getY();
    }

    @Override
    public HealthComponent getHealthComponent() {
        return mHealthComponent;
    }
}
