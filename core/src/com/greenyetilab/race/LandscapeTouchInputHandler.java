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
public class LandscapeTouchInputHandler implements GameInputHandler {
    private GameInput mInput = new GameInput();

    @Override
    public String getName() {
        return "Landscape Touch";
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
            float y = 1 - Gdx.input.getY(i) / (float)Gdx.graphics.getHeight();
            if (y > 0.5f) {
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

    @Override
    public void createHud(Assets assets, HudBridge hudBridge) {
        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);
        hudBridge.getStage().addActor(group);

        TextureRegion bg = assets.hudBackground;
        createHudIndicator(bg, assets.findRegion("hud-left"), true, group, 0, 0.5f);
        createHudIndicator(bg, assets.findRegion("hud-right"), false, group, 0.5f, 1);
    }

    private void createHudIndicator(TextureRegion dot, TextureRegion icon, boolean even, AnchorGroup group, float start, float stop) {
        InputHudIndicator indicator = new InputHudIndicator(dot, icon, even);
        PositionRule rule = new PositionRule();
        rule.reference = group;
        rule.referenceAnchor = new Anchor(start, 1);
        rule.target = indicator;
        rule.targetAnchor = Anchor.TOP_LEFT;
        group.addRule(rule);
        SizeRule sizeRule = new SizeRule(indicator, group, stop - start, SizeRule.IGNORE);
        group.addRule(sizeRule);
    }
}
