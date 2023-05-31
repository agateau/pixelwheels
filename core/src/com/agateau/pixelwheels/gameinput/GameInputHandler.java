/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
import com.badlogic.gdx.Preferences;

/** Responsible for updating a GameInput according to player (or anything else) inputs */
public interface GameInputHandler {
    GameInput getGameInput();

    /** playerIdx is used by some input handlers (keyboard for example) to select default values */
    void loadConfig(Preferences preferences, String prefix, int playerIdx);

    void saveConfig(Preferences preferences, String prefix);

    void createHudButtons(Assets assets, Hud hud);

    void setBonus(Bonus bonus);

    boolean isAvailable();

    /**
     * Return the name of the input handler, if it has a distinctive name. For a gamepad handler,
     * that would be the name of the gamepad.
     */
    String getName();

    /**
     * Return the name for the type of device.
     *
     * <p>Should be the same as the matching GameInputHandlerFactory.getName()
     */
    String getTypeName();
}
