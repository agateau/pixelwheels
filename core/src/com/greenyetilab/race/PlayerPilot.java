package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * A pilot controlled by the player
 */
public class PlayerPilot implements Pilot {
    private final GameWorld mGameWorld;
    private final Vehicle mVehicle;

    private GameInput mInput = new GameInput();
    private GameInputHandler mInputHandler;

    public PlayerPilot(GameWorld gameWorld, Vehicle vehicle) {
        mGameWorld = gameWorld;
        mVehicle = vehicle;

        /*if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            mInputHandler = new AccelerometerInputHandler();
        } else*/ if (Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
            mInputHandler = new TouchInputHandler();
        } else {
            mInputHandler = new KeyboardInputHandler();
        }
    }

    @Override
    public boolean act(float dt) {
        if (mVehicle.isDead()) {
            mGameWorld.setState(GameWorld.State.BROKEN);
        }

        if (mGameWorld.getState() == GameWorld.State.RUNNING) {
            mInput.braking = false;
            mInput.accelerating = false;
            mInput.direction = 0;
            mInputHandler.updateGameInput(mInput);
            mVehicle.setDirection(mInput.direction);
            mVehicle.setAccelerating(mInput.accelerating);
            mVehicle.setBraking(mInput.braking);
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
