package com.agateau.tinywheels;

import com.agateau.ui.KeyMapper;
import com.agateau.ui.VirtualKey;
import com.badlogic.gdx.Input;

/**
 * Handle keyboard input, for desktop mode
 */
public class KeyboardInputHandler implements GameInputHandler {
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

    private KeyMapper mKeyMapper = new KeyMapper();
    private GameInput mInput = new GameInput();

    public KeyboardInputHandler() {
    }

    public void setKeyMapper(KeyMapper keyMapper) {
        mKeyMapper = keyMapper;
    }

    @Override
    public GameInput getGameInput() {
        mInput.direction = 0;
        mInput.braking = mKeyMapper.isKeyPressed(VirtualKey.DOWN);
        mInput.accelerating = !mInput.braking;
        if (mKeyMapper.isKeyPressed(VirtualKey.LEFT)) {
            mInput.direction = 1;
        } else if (mKeyMapper.isKeyPressed(VirtualKey.RIGHT)) {
            mInput.direction = -1;
        }
        mInput.triggeringBonus = mKeyMapper.isKeyPressed(VirtualKey.TRIGGER);

        return mInput;
    }

    @Override
    public void createHudButtons(Assets assets, Hud hud) {
    }

    @Override
    public void setBonus(Bonus bonus) {
    }
}
