package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * A pilot controlled by the player
 */
public class PlayerPilot implements Pilot {
    private static final float MINIMUM_HIT_IMPULSE = 10;
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final GameObject mPlayerGameObject;
    private final Vehicle mVehicle;
    private final HealthComponent mHealthComponent;

    private GameInput mInput = new GameInput();
    private GameInputHandler mInputHandler;

    private boolean mStrongHitHandled = false;

    public PlayerPilot(Assets assets, GameWorld gameWorld, GameObject playerGameObject, Vehicle vehicle, HealthComponent healthComponent) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mPlayerGameObject = playerGameObject;
        mVehicle = vehicle;
        mHealthComponent = healthComponent;

        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            mInputHandler = new AccelerometerInputHandler();
        } else if (Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
            mInputHandler = new TouchInputHandler();
        } else {
            mInputHandler = new KeyboardInputHandler();
        }
    }

    @Override
    public boolean act(float dt) {
        if (mHealthComponent.getHealth() == 0) {
            mVehicle.setBraking(false);
            mVehicle.setAccelerating(false);
            if (mHealthComponent.getState() == HealthComponent.State.DEAD) {
                mGameWorld.setState(GameWorld.State.BROKEN);
            }
            return true;
        }

        if (mGameWorld.getState() == GameWorld.State.RUNNING) {
            mInput.braking = false;
            mInput.accelerating = false;
            mInput.direction = 0;
            mInputHandler.updateGameInput(mInput);
            mVehicle.setDirection(mInput.direction);
            mVehicle.setAccelerating(mInput.accelerating);
            mVehicle.setBraking(mInput.braking);
            if (mInput.shooting) {
                mGameWorld.addGameObject(Bullet.create(mAssets, mGameWorld, mPlayerGameObject, mVehicle.getX(), mVehicle.getY(), mVehicle.getAngle()));
            }
        }
        return true;
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        mStrongHitHandled = false;
        Object other = otherFixture.getBody().getUserData();
        if (other instanceof Mine) {
            mHealthComponent.kill();
        }
        if (other instanceof Gift) {
            Gift gift = (Gift)other;
            gift.pick();
            mGameWorld.increaseScore(Constants.SCORE_GIFT_PICK);
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
        Object other = otherFixture.getBody().getUserData();
        if (!(other instanceof GameObject)) {
            return;
        }
        GameObject go = (GameObject)other;
        HealthComponent healthComponent = go.getHealthComponent();
        if (healthComponent == null || healthComponent.getHealth() == 0) {
            return;
        }
        float value = impulse.getNormalImpulses()[0];
        if (value < MINIMUM_HIT_IMPULSE || mStrongHitHandled) {
            return;
        }
        mStrongHitHandled = true;
        healthComponent.decreaseHealth();
        mGameWorld.increaseScore(Constants.SCORE_CAR_HIT);
        Vector2 point = contact.getWorldManifold().getPoints()[0];
        mGameWorld.addGameObject(AnimationObject.create(mAssets.impact, point.x, point.y));
    }
}
