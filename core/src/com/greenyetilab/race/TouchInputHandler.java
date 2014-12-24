package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;

/**
 * Handle inputs with touch screen only
 */
public class TouchInputHandler implements GameInputHandler {
    private GameInput mInput = new GameInput();

    @Override
    public String toString() {
        return "Touch";
    }

    @Override
    public GameInput getGameInput() {
        mInput.direction = 0;
        mInput.shooting = false;
        mInput.braking = false;
        mInput.accelerating = true;
        for (int i = 0; i < 5; i++) {
            if (!Gdx.input.isTouched(i)) {
                continue;
            }
            float x = Gdx.input.getX(i) / (float)Gdx.graphics.getWidth();
            float y = 1f - Gdx.input.getY(i) / (float)Gdx.graphics.getHeight();
            if (y < 0.5f) {
                if (x < 0.5f) {
                    mInput.direction = 1;
                } else {
                    mInput.direction = -1;
                }
            } else {
                mInput.shooting = true;
            }
        }
        return mInput;
    }
}
