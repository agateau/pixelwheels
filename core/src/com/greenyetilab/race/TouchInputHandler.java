package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;

/**
 * Handle inputs with touch screen only
 */
public class TouchInputHandler implements GameInputHandler {
    @Override
    public void updateGameInput(GameInput input) {
        input.accelerating = true;
        for (int i = 0; i < 5; i++) {
            if (!Gdx.input.isTouched(i)) {
                continue;
            }
            float x = Gdx.input.getX(i) / (float)Gdx.graphics.getWidth();
            if (x < 0.5f) {
                input.direction = 1;
            } else {
                input.direction = -1;
            }
        }
    }
}
