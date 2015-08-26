package com.greenyetilab.tinywheels;

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
            return "Use virtual buttons to control your vehicle.";
        }

        @Override
        public GameInputHandler create() {
            return new TouchInputHandler();
        }
    }

    private GameInput mInput = new GameInput();
    private HudButton mLeftButton, mRightButton, mBrakeButton, mBonusButton;

    @Override
    public GameInput getGameInput() {
        mInput.direction = 0;
        mInput.triggeringBonus = false;
        mInput.braking = false;
        mInput.accelerating = true;

        if (mBonusButton.isPressed()) {
            mInput.triggeringBonus = true;
        } else {
            if (mLeftButton.isPressed()) {
                mInput.direction = 1;
            } else if (mRightButton.isPressed()) {
                mInput.direction = -1;
            } else if (mBrakeButton.isPressed()) {
                mInput.accelerating = false;
                mInput.braking = true;
            }
        }
        return mInput;
    }

    @Override
    public void createHudButtons(Assets assets, Hud hud) {
        mLeftButton = new HudButton(assets, hud, "left");
        mRightButton = new HudButton(assets, hud, "right");
        mBrakeButton = new HudButton(assets, hud, "back");
        mBonusButton = new HudButton(assets, hud, "action");

        AnchorGroup root = hud.getRoot();
        root.addPositionRule(mLeftButton, Anchor.BOTTOM_LEFT, root, Anchor.BOTTOM_LEFT);
        root.addPositionRule(mRightButton, Anchor.BOTTOM_LEFT, mLeftButton, Anchor.BOTTOM_RIGHT, 12, 0);
        root.addPositionRule(mBrakeButton, Anchor.BOTTOM_RIGHT, root, Anchor.BOTTOM_RIGHT);
        root.addPositionRule(mBonusButton, Anchor.BOTTOM_RIGHT, mBrakeButton, Anchor.TOP_RIGHT);
    }

    @Override
    public void setBonus(Bonus bonus) {
        if (bonus == null) {
            mBonusButton.setVisible(false);
        } else {
            mBonusButton.setVisible(true);
            mBonusButton.setIcon(bonus.getIconRegion());
        }
    }
}
