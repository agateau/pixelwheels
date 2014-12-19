package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

/**
 * Handles game input using the accelerometer
 */
public class AccelerometerInputHandler implements GameInputHandler {
    private static final float MAX_ACCELEROMETER = 10;

    @Override
    public void updateGameInput(GameInput input) {
        input.braking = false;
        input.accelerating = true;
        float angle = -Gdx.input.getAccelerometerY();
        input.direction = MathUtils.clamp(angle, -MAX_ACCELEROMETER, MAX_ACCELEROMETER) / MAX_ACCELEROMETER;
        }
    }
}
