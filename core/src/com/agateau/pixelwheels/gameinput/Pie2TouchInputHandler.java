/*
 * Copyright 2025 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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

import static com.agateau.translations.Translator.tr;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.bonus.Bonus;
import com.agateau.pixelwheels.racescreen.Hud;
import com.agateau.pixelwheels.racescreen.PieButton;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

/** Handle input using pie buttons in the bottom left and right corners */
public class Pie2TouchInputHandler implements GameInputHandler {
    private final GameInput mInput = new GameInput();
    private final DigitalSteering mSteer = new DigitalSteering();
    private PieButton mLeftButton, mRightButton, mBrakeButton, mBonusButton;

    public static class Factory implements GameInputHandlerFactory {
        final Array<GameInputHandler> mHandlers = new Array<>();

        Factory() {
            mHandlers.add(new Pie2TouchInputHandler());
        }

        @Override
        public String getId() {
            return "pie2";
        }

        @Override
        public String getName() {
            return tr("Round buttons");
        }

        @Override
        public Array<GameInputHandler> getAllHandlers() {
            return mHandlers;
        }
    }

    @Override
    public GameInput getGameInput() {
        mInput.braking = mBrakeButton.isPressed();
        mInput.accelerating = !mInput.braking;
        mInput.direction = mSteer.steer(mLeftButton.isPressed(), mRightButton.isPressed());
        mInput.triggeringBonus = mBonusButton.isPressed();
        return mInput;
    }

    @Override
    public void loadConfig(Preferences preferences, String prefix, int playerIdx) {}

    @Override
    public void saveConfig(Preferences preferences, String prefix) {}

    @Override
    public void setBonus(Bonus bonus) {
        mBonusButton.setEnabled(bonus != null);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getTypeName() {
        return tr("Pie buttons");
    }

    @Override
    public void createHudButtons(Assets assets, Hud hud) {
        float margin = 4;
        float offset = 128 + margin;
        mLeftButton = new PieButton(assets, hud, "pie2-left");
        mLeftButton.setSector(45, 90);
        mLeftButton.setOffset(margin, offset);
        mRightButton = new PieButton(assets, hud, "pie2-right");
        mRightButton.setSector(0, 45);
        mRightButton.setOffset(offset, margin);
        mBonusButton = new PieButton(assets, hud, "pie2-action");
        mBonusButton.setSector(90, 135);
        mBonusButton.setEnabled(false);
        mBonusButton.setOffset(-margin, offset);
        mBrakeButton = new PieButton(assets, hud, "pie2-brake");
        mBrakeButton.setSector(135, 180);
        mBrakeButton.setOffset(-offset, margin);

        AnchorGroup root = hud.getInputUiContainer();

        root.addPositionRule(mLeftButton, Anchor.BOTTOM_LEFT, root, Anchor.BOTTOM_LEFT);
        root.addPositionRule(mRightButton, Anchor.BOTTOM_LEFT, root, Anchor.BOTTOM_LEFT);

        root.addPositionRule(mBrakeButton, Anchor.BOTTOM_RIGHT, root, Anchor.BOTTOM_RIGHT);
        root.addPositionRule(mBonusButton, Anchor.BOTTOM_RIGHT, root, Anchor.BOTTOM_RIGHT);
    }
}
