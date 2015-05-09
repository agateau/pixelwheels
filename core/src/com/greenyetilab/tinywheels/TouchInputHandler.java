package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Handle inputs with touch screen only
 */
public class TouchInputHandler implements GameInputHandler {
    public static class Factory implements GameInputHandlerFactory {
        @Override
        public String getId() {
            return "touch";
        }

        @Override
        public String getName() {
            return "Touch";
        }

        @Override
        public String getDescription() {
            return "Touch left part of the screen to go left.\nTouch middle part to go right.\nTouch right part to brake.";
        }

        @Override
        public GameInputHandler create() {
            return new TouchInputHandler();
        }
    }

    private GameInput mInput = new GameInput();
    private final BonusIndicator mBonusIndicator = new BonusIndicator();
    private InputHudIndicator mLeftIndicator, mRightIndicator, mBrakeIndicator;

    @Override
    public GameInput getGameInput() {
        mInput.direction = 0;
        mInput.triggeringBonus = false;
        mInput.braking = false;
        mInput.accelerating = true;
        for (int i = 0; i < 5; i++) {
            if (!Gdx.input.isTouched(i)) {
                continue;
            }
            float x = Gdx.input.getX(i);
            float y = Gdx.graphics.getHeight() - Gdx.input.getY(i);
            if (isActorHit(mBonusIndicator, x, y)) {
                mInput.triggeringBonus = true;
            } else {
                if (isActorHit(mLeftIndicator, x, 0)) {
                    mInput.direction = 1;
                } else if (isActorHit(mRightIndicator, x, 0)) {
                    mInput.direction = -1;
                } else if (isActorHit(mBrakeIndicator, x, 0)) {
                    mInput.accelerating = false;
                    mInput.braking = true;
                }
            }
        }
        return mInput;
    }

    @Override
    public void createHud(Assets assets, HudBridge hudBridge) {
        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);
        hudBridge.getStage().addActor(group);

        mLeftIndicator = new InputHudIndicator(assets.findRegion("hud-left"));
        mRightIndicator = new InputHudIndicator(assets.findRegion("hud-right"));
        mBrakeIndicator = new InputHudIndicator(assets.findRegion("hud-back"));

        group.addPositionRule(mLeftIndicator, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT);
        group.addPositionRule(mRightIndicator, Anchor.BOTTOM_LEFT, mRightIndicator, Anchor.BOTTOM_RIGHT);
        group.addPositionRule(mBrakeIndicator, Anchor.BOTTOM_RIGHT, group, Anchor.BOTTOM_RIGHT);
        group.addPositionRule(mBonusIndicator, Anchor.BOTTOM_RIGHT, mBrakeIndicator, Anchor.TOP_RIGHT);
    }

    @Override
    public BonusIndicator getBonusIndicator() {
        return mBonusIndicator;
    }

    private static boolean isActorHit(Actor actor, float x, float y) {
        return actor.hit(x - actor.getX(), y - actor.getY(), false) != null;
    }
}
