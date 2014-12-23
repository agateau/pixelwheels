package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

/**
 * Handle inputs using gestures
 */
public class GestureInputHandler implements GameInputHandler {
    private static final int NO_POINTER = -1;
    private static final float PANNING_AREA = 0.5f;
    private static final float PANNING_SENSITIVITY = 2.5f;

    private GameInput mInput = new GameInput();
    private int mPanPointer = NO_POINTER;
    private float mPanStart = 0; // mPanStart goes from 0 to 1

    @Override
    public String toString() {
        return "Gesture";
    }

    @Override
    public GameInput getGameInput() {
        mInput.braking = false;
        mInput.accelerating = true;
        mInput.shooting = false;
        mInput.direction = 0;
        if (mPanPointer != NO_POINTER) {
            updatePanning();
        }
        for (int pointer = 0; pointer < 5; pointer++) {
            if (pointer == mPanPointer) {
                continue;
            }
            if (!Gdx.input.isTouched(pointer)) {
                continue;
            }
            float x = Gdx.input.getX(pointer) / (float)Gdx.graphics.getWidth();
            if (x <= PANNING_AREA) {
                mPanPointer = pointer;
                mPanStart = x / PANNING_AREA;
            } else {
                mInput.shooting = true;
            }
        }
        return mInput;
    }

    private void updatePanning() {
        if (!Gdx.input.isTouched(mPanPointer)) {
            mPanPointer = NO_POINTER;
            return;
        }
        float x = Gdx.input.getX(mPanPointer) / (float)Gdx.graphics.getWidth() / PANNING_AREA;
        mInput.direction = -(x - mPanStart) * PANNING_SENSITIVITY;
        mInput.direction = MathUtils.clamp(mInput.direction, -1, 1);
    }
}
