package com.agateau.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Handle keyboard input, for desktop mode
 */
public class KeyboardInputHandler implements GameInputHandler {
    public enum Action {
        LEFT(0),
        RIGHT(1),
        BRAKE(2),
        TRIGGER(3);

        int id;

        Action(int id) {
            this.id = id;
        }
    }

    public static class Factory implements GameInputHandlerFactory {
        @Override
        public String getId() {
            return "keyboard";
        }

        @Override
        public String getName() {
            return "Keyboard";
        }

        @Override
        public String getDescription() {
            return "Left and Right keys: Drive.\nLeft-Ctrl: Activate bonus.";
        }

        @Override
        public GameInputHandler create() {
            return new KeyboardInputHandler();
        }

    }

    private final int[] mKeyForAction = new int[4];
    private GameInput mInput = new GameInput();

    public KeyboardInputHandler() {
        mKeyForAction[Action.LEFT.id] = Input.Keys.LEFT;
        mKeyForAction[Action.RIGHT.id] = Input.Keys.RIGHT;
        mKeyForAction[Action.BRAKE.id] = Input.Keys.DOWN;
        mKeyForAction[Action.TRIGGER.id] = Input.Keys.CONTROL_LEFT;
    }

    public void setActionKey(Action action, int key) {
        mKeyForAction[action.id] = key;
    }

    @Override
    public GameInput getGameInput() {
        mInput.direction = 0;
        /*
        mInput.braking = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        mInput.accelerating = Gdx.input.isKeyPressed(Input.Keys.UP);
        */
        mInput.braking = isKeyPressed(Action.BRAKE);
        mInput.accelerating = !mInput.braking; //Gdx.input.isKeyPressed(Input.Keys.UP);
        if (isKeyPressed(Action.LEFT)) {
            mInput.direction = 1;
        } else if (isKeyPressed(Action.RIGHT)) {
            mInput.direction = -1;
        }
        mInput.triggeringBonus = isKeyPressed(Action.TRIGGER);

        return mInput;
    }

    @Override
    public void createHudButtons(Assets assets, Hud hud) {
    }

    @Override
    public void setBonus(Bonus bonus) {
    }

    private boolean isKeyPressed(Action action) {
        return Gdx.input.isKeyPressed(mKeyForAction[action.id]);
    }
}
