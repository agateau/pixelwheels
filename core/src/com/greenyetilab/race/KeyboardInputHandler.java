package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Handle keyboard input, for desktop mode
 */
public class KeyboardInputHandler implements GameInputHandler {
    private GameInput mInput = new GameInput();

    @Override
    public String getName() {
        return "Keyboard";
    }

    @Override
    public String getDescription() {
        return "Left and Right keys: Drive.\nLeft-Ctrl: Fire.";
    }

    @Override
    public GameInput getGameInput() {
        mInput.direction = 0;
        mInput.braking = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        mInput.accelerating = !mInput.braking;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mInput.direction = 1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mInput.direction = -1;
        }
        mInput.shooting = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);

        return mInput;
    }

    @Override
    public void createHud(Assets assets, HudBridge hudBridge) {
    }
}
