package com.agateau.tinywheels;

import com.agateau.utils.anchor.Anchor;
import com.agateau.utils.anchor.AnchorGroup;

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

        ClickArea leftArea = new ClickArea(mLeftButton);
        root.addPositionRule(leftArea, Anchor.BOTTOM_LEFT, root, Anchor.BOTTOM_LEFT);
        root.addSizeRule(leftArea, root, 0.2f, 0.5f);
        root.addPositionRule(mLeftButton, Anchor.BOTTOM_CENTER, leftArea, Anchor.BOTTOM_CENTER);

        ClickArea rightArea = new ClickArea(mRightButton);
        root.addPositionRule(rightArea, Anchor.BOTTOM_LEFT, leftArea, Anchor.BOTTOM_RIGHT);
        root.addSizeRule(rightArea, root, 0.2f, 0.5f);
        root.addPositionRule(mRightButton, Anchor.BOTTOM_CENTER, rightArea, Anchor.BOTTOM_CENTER);

        ClickArea brakeArea = new ClickArea(mBrakeButton);
        root.addSizeRule(brakeArea, root, 0.2f, 0.5f);
        root.addPositionRule(brakeArea, Anchor.BOTTOM_RIGHT, root, Anchor.BOTTOM_RIGHT);
        root.addPositionRule(mBrakeButton, Anchor.BOTTOM_CENTER, brakeArea, Anchor.BOTTOM_CENTER);

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
