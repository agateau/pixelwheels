/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
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
package com.agateau.pixelwheels.gameinput;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.racescreen.Hud;
import com.agateau.pixelwheels.racescreen.PieButton;
import com.agateau.pixelwheels.bonus.Bonus;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * Handle inputs with touch screen only
 */
public class TouchInputHandler implements GameInputHandler {
    public static class Factory implements GameInputHandlerFactory {
        final Array<GameInputHandler> mHandlers = new Array<GameInputHandler>();

        Factory() {
            mHandlers.add(new TouchInputHandler());
        }

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
        public Array<GameInputHandler> getAllHandlers() {
            return mHandlers;
        }
    }

    private GameInput mInput = new GameInput();
    private PieButton mLeftButton, mRightButton, mBrakeButton, mBonusButton;

    @Override
    public GameInput getGameInput() {
        mInput.braking = mBrakeButton.isPressed();
        mInput.accelerating = !mInput.braking;
        if (mLeftButton.isPressed()) {
            mInput.direction += GamePlay.instance.steeringStep;
        } else if (mRightButton.isPressed()) {
            mInput.direction -= GamePlay.instance.steeringStep;
        } else {
            mInput.direction = 0;
        }
        mInput.direction = MathUtils.clamp(mInput.direction, -1, 1);
        mInput.triggeringBonus = mBonusButton.isPressed();

        return mInput;
    }

    @Override
    public void loadConfig(Preferences preferences, String prefix) {

    }

    @Override
    public void saveConfig(Preferences preferences, String prefix) {

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
        mBonusButton.setEnabled(bonus != null);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
