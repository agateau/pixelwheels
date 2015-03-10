package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;
import com.greenyetilab.utils.anchor.PositionRule;
import com.greenyetilab.utils.anchor.SizeRule;

/**
 * Handle inputs with touch screen only
 */
public class TouchInputHandler implements GameInputHandler {
    private static final float LEFT_PERCENT = 0.25f;
    private static final float RIGHT_PERCENT = 0.5f;
    private GameInput mInput = new GameInput();

    @Override
    public String getName() {
        return "Touch";
    }

    @Override
    public String getDescription() {
        return "Touch left part of the screen to go left.\nTouch middle part to go right.\nTouch right part to brake.";
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
            if (x < LEFT_PERCENT) {
                mInput.direction = 1;
            } else if (x < RIGHT_PERCENT) {
                mInput.direction = -1;
            } else {
                mInput.accelerating = false;
                mInput.braking = true;
            }
        }
        return mInput;
    }

    @Override
    public void createHud(Assets assets, HudBridge hudBridge) {
        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);
        hudBridge.getStage().addActor(group);

        createHudIndicator(assets.findRegion("hud-left"), group, 0, LEFT_PERCENT);
        createHudIndicator(assets.findRegion("hud-right"), group, LEFT_PERCENT, RIGHT_PERCENT);
        createHudIndicator(assets.findRegion("hud-back"), group, RIGHT_PERCENT, 1);
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
