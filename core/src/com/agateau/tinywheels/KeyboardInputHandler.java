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

import com.agateau.ui.KeyMapper;
import com.agateau.ui.VirtualKey;
import com.badlogic.gdx.Input;

/**
 * Handle keyboard input, for desktop mode
 */
public class KeyboardInputHandler implements GameInputHandler {
    public static class Factory implements GameInputHandlerFactory {
        @Override
        public String getId() {
            return "keyboard";
        }

        @Override
        public String getName() {
            return "Keyboard";
        }

        @Override
        public String getDescription() {
            return "Left and Right keys: Drive.\nLeft-Ctrl: Activate bonus.";
        }

        @Override
        public GameInputHandler create() {
            return new KeyboardInputHandler();
        }

    }

    private KeyMapper mKeyMapper = new KeyMapper();
    private GameInput mInput = new GameInput();

    public KeyboardInputHandler() {
    }

    public void setKeyMapper(KeyMapper keyMapper) {
        mKeyMapper = keyMapper;
    }

    @Override
    public GameInput getGameInput() {
        mInput.direction = 0;
        mInput.braking = mKeyMapper.isKeyPressed(VirtualKey.DOWN);
        mInput.accelerating = !mInput.braking;
        if (mKeyMapper.isKeyPressed(VirtualKey.LEFT)) {
            mInput.direction = 1;
        } else if (mKeyMapper.isKeyPressed(VirtualKey.RIGHT)) {
            mInput.direction = -1;
        }
        mInput.triggeringBonus = mKeyMapper.isKeyPressed(VirtualKey.TRIGGER);

        return mInput;
    }

    @Override
    public void createHudButtons(Assets assets, Hud hud) {
    }

    @Override
    public void setBonus(Bonus bonus) {
    }
}
