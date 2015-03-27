package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

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

    private final BonusIndicator mBonusIndicator = new BonusIndicator();
    private GameInput mInput = new GameInput();

    @Override
    public BonusIndicator getBonusIndicator() {
        return mBonusIndicator;
    }

    @Override
    public GameInput getGameInput() {
        mInput.direction = 0;
        /*
        mInput.braking = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        mInput.accelerating = Gdx.input.isKeyPressed(Input.Keys.UP);
        */
        mInput.braking = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        mInput.accelerating = !mInput.braking; //Gdx.input.isKeyPressed(Input.Keys.UP);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mInput.direction = 1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mInput.direction = -1;
        }
        mInput.triggeringBonus = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);

        return mInput;
    }

    @Override
    public void createHud(Assets assets, HudBridge hudBridge) {
        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);
        hudBridge.getStage().addActor(group);
        group.addPositionRule(mBonusIndicator, Anchor.TOP_LEFT, group, Anchor.TOP_LEFT, 16, -48);
    }
}
