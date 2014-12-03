package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

/**
 * Handles game input using the accelerometer
 */
public class AccelerometerInputHandler implements GameInputHandler {
    private static final float ACCELEROMETER_DEAD_ZONE = 2;
    private static final float MAX_ACCELEROMETER = 12 + ACCELEROMETER_DEAD_ZONE;

    @Override
    public void updateGameInput(GameInput input) {
        float angle = -Gdx.input.getAccelerometerY();
        if (Math.abs(angle) >= ACCELEROMETER_DEAD_ZONE) {
            input.direction = MathUtils.clamp(angle - ACCELEROMETER_DEAD_ZONE, -MAX_ACCELEROMETER, MAX_ACCELEROMETER) / MAX_ACCELEROMETER;
        }
        for (int i = 0; i < 5; i++) {
            if (!Gdx.input.isTouched(i)) {
                continue;
            }
            float x = Gdx.input.getX(i) / (float)Gdx.graphics.getWidth();
            if (x < 0.5f) {
                input.braking = true;
            } else {
                input.accelerating = true;
            }
        }
    }
}
