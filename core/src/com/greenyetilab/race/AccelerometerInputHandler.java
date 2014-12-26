package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

/**
 * Handles game input using the accelerometer
 */
public class AccelerometerInputHandler implements GameInputHandler {
    private static final float MAX_ACCELEROMETER = 10;

    private GameInput mInput = new GameInput();

    @Override
    public String getName() {
        return "Accelerometer";
    }

    @Override
    public GameInput getGameInput() {
        mInput.braking = false;
        mInput.accelerating = true;
        mInput.shooting = false;

        float angle = Gdx.input.getAccelerometerX();
        mInput.direction = MathUtils.clamp(angle, -MAX_ACCELEROMETER, MAX_ACCELEROMETER) / MAX_ACCELEROMETER;
        for (int i = 0; i < 5; i++) {
            if (Gdx.input.isTouched(i)) {
                mInput.shooting = true;
                break;
            }
        }
        return mInput;
    }

    @Override
    public void createHud(Assets assets, HudBridge hudBridge) {

    }
}
