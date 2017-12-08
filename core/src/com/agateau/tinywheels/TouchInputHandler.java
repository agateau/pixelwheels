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
    private PieButton mLeftButton, mRightButton, mBrakeButton, mBonusButton;

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
        final float radius = 132;
        mLeftButton = new PieButton(assets, hud, "left");
        mLeftButton.setSector(45, 90);
        mLeftButton.setRadius(radius);
        mRightButton = new PieButton(assets, hud, "right");
        mRightButton.setSector(0, 45);
        mRightButton.setRadius(radius);
        mBonusButton = new PieButton(assets, hud, "action");
        mBonusButton.setSector(90, 135);
        mBonusButton.setRadius(radius);
        mBrakeButton = new PieButton(assets, hud, "brake");
        mBrakeButton.setSector(135, 180);
        mBrakeButton.setRadius(radius);

        AnchorGroup root = hud.getRoot();

        root.addPositionRule(mLeftButton, Anchor.BOTTOM_LEFT, root, Anchor.BOTTOM_LEFT);
        root.addPositionRule(mRightButton, Anchor.BOTTOM_LEFT, root, Anchor.BOTTOM_LEFT);

        root.addPositionRule(mBrakeButton, Anchor.BOTTOM_RIGHT, root, Anchor.BOTTOM_RIGHT);
        root.addPositionRule(mBonusButton, Anchor.BOTTOM_RIGHT, root, Anchor.BOTTOM_RIGHT);
    }

    @Override
    public void setBonus(Bonus bonus) {
        if (bonus == null) {
            mBonusButton.setEnabled(false);
            mBonusButton.setIcon(null);
        } else {
            mBonusButton.setEnabled(true);
            mBonusButton.setIcon(bonus.getIconRegion());
        }
    }
}
