/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.bonus.Bonus;
import com.agateau.pixelwheels.racescreen.Hud;
import com.agateau.ui.InputMapper;
import com.agateau.ui.VirtualKey;
import com.badlogic.gdx.Preferences;

/** Base class for InputMapper-based GameInputHandlers */
public abstract class InputMapperInputHandler implements GameInputHandler {
    private final InputMapper mInputMapper;
    private final GameInput mInput = new GameInput();
    private final DigitalSteering mSteer = new DigitalSteering();

    InputMapperInputHandler(InputMapper inputMapper) {
        mInputMapper = inputMapper;
    }

    @Override
    public GameInput getGameInput() {
        mInput.braking =
                mInputMapper.isKeyPressed(VirtualKey.DOWN)
                        || mInputMapper.isKeyPressed(VirtualKey.BACK);
        mInput.accelerating = !mInput.braking;
        mInput.direction =
                mSteer.steer(
                        mInputMapper.isKeyPressed(VirtualKey.LEFT),
                        mInputMapper.isKeyPressed(VirtualKey.RIGHT));
        mInput.triggeringBonus = mInputMapper.isKeyPressed(VirtualKey.TRIGGER);

        return mInput;
    }

    @Override
    public void loadConfig(Preferences preferences, String prefix, int playerIdx) {
        mInputMapper.loadConfig(preferences, prefix, playerIdx);
    }

    @Override
    public void saveConfig(Preferences preferences, String prefix) {
        mInputMapper.saveConfig(preferences, prefix);
    }

    @Override
    public void createHudButtons(Assets assets, Hud hud) {}

    @Override
    public void setBonus(Bonus bonus) {}

    @Override
    public boolean isAvailable() {
        return mInputMapper.isAvailable();
    }

    public InputMapper getInputMapper() {
        return mInputMapper;
    }
}
