package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Handle keyboard input, for desktop mode
 */
public class KeyboardInputHandler implements GameInputHandler {
    private GameInput mInput = new GameInput();

    @Override
    public String toString() {
        return "Keyboard";
    }

    @Override
    public GameInput getGameInput() {
        mInput.braking = false;
        mInput.accelerating = true;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mInput.direction = 1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mInput.direction = -1;
        }
        mInput.shooting = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);

        return mInput;
    }
}
