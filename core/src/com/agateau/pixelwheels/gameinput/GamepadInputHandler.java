/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.ui.GamepadInputMapper;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;

/**
 * Handle gamepad input, for desktop mode
 */
public class GamepadInputHandler extends KeyboardInputHandler {

    public static class Factory implements GameInputHandlerFactory {
        @Override
        public String getId() {
            return "gamepad";
        }

        @Override
        public String getName() {
            return "Gamepad";
        }

        @Override
        public String getDescription() {
            return "Use D-Pad to drive, button to trigger bonuses.";
        }

        @Override
        public GameInputHandler create() {
            return new GamepadInputHandler();
        }
    }

    GamepadInputHandler() {
        GamepadInputMapper mapper = new GamepadInputMapper();
        Array<Controller> controllers = Controllers.getControllers();
        if (controllers.size > 0) {
            mapper.setController(controllers.get(0));
        }
        setInputMapper(mapper);
    }
}
