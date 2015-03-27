package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

/**
 * Handles game input using the accelerometer
 */
public class AccelerometerInputHandler implements GameInputHandler {
    public static class Factory implements GameInputHandlerFactory {
        @Override
        public String getId() {
            return "accelerometer";
        }

        @Override
        public String getName() {
            return "Accelerometer";
        }

        @Override
        public String getDescription() {
            return "Tilt the phone to go left or right, touch anywhere to fire.";
        }

        @Override
        public GameInputHandler create() {
            return new AccelerometerInputHandler();
        }
    }

    private static final float MAX_ACCELEROMETER = 10;

    private GameInput mInput = new GameInput();

    @Override
    public GameInput getGameInput() {
        mInput.braking = false;
        mInput.accelerating = true;
        mInput.triggeringBonus = false;

        float angle = Gdx.input.getAccelerometerY();
        mInput.direction = MathUtils.clamp(angle, -MAX_ACCELEROMETER, MAX_ACCELEROMETER) / MAX_ACCELEROMETER;
        for (int i = 0; i < 5; i++) {
            if (Gdx.input.isTouched(i)) {
                mInput.triggeringBonus = true;
                break;
            }
        }
        return mInput;
    }

    @Override
    public void createHud(Assets assets, HudBridge hudBridge) {

    }

    @Override
    public BonusIndicator getBonusIndicator() {
        return null;
    }
}
