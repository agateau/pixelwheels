package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;
import com.greenyetilab.utils.anchor.PositionRule;
import com.greenyetilab.utils.anchor.SizeRule;

/**
 * Handle inputs using gestures
 */
public class GestureInputHandler implements GameInputHandler {
    public static class Factory implements GameInputHandlerFactory {
        @Override
        public String getId() {
            return "gesture";
        }

        @Override
        public String getName() {
            return "Gesture";
        }

        @Override
        public String getDescription() {
            return "Swipe on the left part of the screen to go left or right, touch right part of the screen to fire.";
        }

        @Override
        public GameInputHandler create() {
            return new GestureInputHandler();
        }
    }

    private static final int NO_POINTER = -1;
    private static final float PANNING_AREA = 0.7f;
    private static final float PANNING_SENSITIVITY = 2.5f;

    private GameInput mInput = new GameInput();
    private int mPanPointer = NO_POINTER;
    private float mPanStart = 0; // mPanStart goes from 0 to 1

    @Override
    public GameInput getGameInput() {
        mInput.braking = false;
        mInput.accelerating = true;
        mInput.triggeringBonus = false;
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
                mInput.accelerating = false;
                mInput.braking = true;
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

    @Override
    public void createHud(Assets assets, HudBridge hudBridge) {
        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);
        hudBridge.getStage().addActor(group);

        createHudIndicator(assets.findRegion("hud-swipe"), group, 0, PANNING_AREA);
        createHudIndicator(assets.findRegion("hud-back"), group, PANNING_AREA, 1);
    }

    private void createHudIndicator(TextureRegion icon, AnchorGroup group, float start, float stop) {
        InputHudIndicator indicator = new InputHudIndicator(icon);
        PositionRule rule = new PositionRule();
        rule.reference = group;
        rule.referenceAnchor = new Anchor(start, 0);
        rule.target = indicator;
        rule.targetAnchor = Anchor.BOTTOM_LEFT;
        group.addRule(rule);
        SizeRule sizeRule = new SizeRule(indicator, group, stop - start, SizeRule.IGNORE);
        group.addRule(sizeRule);
    }

}
