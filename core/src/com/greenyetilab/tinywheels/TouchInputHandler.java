package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
    private InputHudIndicator mLeftIndicator, mRightIndicator, mBrakeIndicator, mBonusIndicator;

    @Override
    public GameInput getGameInput() {
        mInput.direction = 0;
        mInput.triggeringBonus = false;
        mInput.braking = false;
        mInput.accelerating = true;

        mLeftIndicator.setPressed(false);
        mRightIndicator.setPressed(false);
        mBrakeIndicator.setPressed(false);
        mBonusIndicator.setPressed(false);

        for (int i = 0; i < 5; i++) {
            if (!Gdx.input.isTouched(i)) {
                continue;
            }
            float x = Gdx.input.getX(i);
            float y = Gdx.graphics.getHeight() - Gdx.input.getY(i);
            if (isActorHit(mBonusIndicator, x, y)) {
                mBonusIndicator.setPressed(true);
                mInput.triggeringBonus = true;
            } else {
                if (isActorHit(mLeftIndicator, x, 0)) {
                    mLeftIndicator.setPressed(true);
                    mInput.direction = 1;
                } else if (isActorHit(mRightIndicator, x, 0)) {
                    mRightIndicator.setPressed(true);
                    mInput.direction = -1;
                } else if (isActorHit(mBrakeIndicator, x, 0)) {
                    mBrakeIndicator.setPressed(true);
                    mInput.accelerating = false;
                    mInput.braking = true;
                }
            }
        }
        return mInput;
    }

    @Override
    public void createHud(Assets assets, Group root) {
        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);
        root.addActor(group);

        mLeftIndicator = new InputHudIndicator(assets, "left");
        mRightIndicator = new InputHudIndicator(assets, "right");
        mBrakeIndicator = new InputHudIndicator(assets, "back");
        mBonusIndicator = new InputHudIndicator(assets, "square");

        group.addPositionRule(mLeftIndicator, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT);
        group.addPositionRule(mRightIndicator, Anchor.BOTTOM_LEFT, mLeftIndicator, Anchor.BOTTOM_RIGHT);
        group.addPositionRule(mBrakeIndicator, Anchor.BOTTOM_RIGHT, group, Anchor.BOTTOM_RIGHT);
        group.addPositionRule(mBonusIndicator, Anchor.BOTTOM_RIGHT, mBrakeIndicator, Anchor.TOP_RIGHT);
    }

    @Override
    public void setCanTriggerBonus(boolean canTrigger) {
        if (mBonusIndicator.isVisible() != canTrigger) {
            mBonusIndicator.setVisible(canTrigger);
        }
    }

    private static boolean isActorHit(Actor actor, float screenX, float screenY) {
        Stage stage = actor.getStage();
        float x = screenX * stage.getWidth() / Gdx.graphics.getWidth();
        float y = screenY * stage.getHeight() / Gdx.graphics.getHeight();
        return actor.hit(x - actor.getX(), y - actor.getY(), false) != null;
    }
}
