package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;

/**
 * Handle inputs with touch screen only
 */
public class TouchInputHandler implements GameInputHandler {
    @Override
    public void updateGameInput(GameInput input) {
        for (int i = 0; i < 5; i++) {
            if (!Gdx.input.isTouched(i)) {
                continue;
            }
            float x = Gdx.input.getX(i) / (float)Gdx.graphics.getWidth();
            if (x < 0.3f) {
                input.direction = 1;
            } else if (x < 0.7f) {
                input.shooting = true;
            } else {
                input.direction = -1;
            }
        }
        input.accelerating = true;
    }
}
