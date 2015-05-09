package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Handles game input using the accelerometer
 */
public class AccelerometerInputHandler implements GameInputHandler {
    public static class Factory implements GameInputHandlerFactory {
        @Override
        public String getId() {
            return "accelerometer";
        }

        @Override
        public String getName() {
            return "Accelerometer";
        }

        @Override
        public String getDescription() {
            return "Tilt the phone to go left or right, touch anywhere to fire.";
        }

        @Override
        public GameInputHandler create() {
            return new AccelerometerInputHandler();
        }
    }

    private static final float MAX_ACCELEROMETER = 10;

    private final BonusIndicator mBonusIndicator = new BonusIndicator();
    private GameInput mInput = new GameInput();

    @Override
    public GameInput getGameInput() {
        mInput.braking = false;
        mInput.accelerating = true;
        mInput.triggeringBonus = false;

        float angle = -Gdx.input.getAccelerometerY();
        mInput.direction = MathUtils.clamp(angle, -MAX_ACCELEROMETER, MAX_ACCELEROMETER) / MAX_ACCELEROMETER;

        for (int i = 0; i < 5; i++) {
            if (!Gdx.input.isTouched(i)) {
                continue;
            }
            float x = Gdx.input.getX(i) / Gdx.graphics.getWidth();
            if (x < 0.5f) {
                mInput.accelerating = false;
                mInput.braking = true;
            } else {
                mInput.triggeringBonus = true;
            }
        }

        return mInput;
    }

    @Override
    public void createHud(Assets assets, HudBridge hudBridge) {
        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);
        hudBridge.getStage().addActor(group);
        group.addPositionRule(mBonusIndicator, Anchor.TOP_RIGHT, group, Anchor.CENTER_RIGHT, 0, 0);
    }

    @Override
    public BonusIndicator getBonusIndicator() {
        return mBonusIndicator;
    }
}
