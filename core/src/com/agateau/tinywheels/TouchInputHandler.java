/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.tinywheels;

import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;

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
        mLeftButton = new HudButton(assets, "left");
        mRightButton = new HudButton(assets, "right");
        mBrakeButton = new HudButton(assets, "back");
        mBonusButton = new HudButton(assets, "action");

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
