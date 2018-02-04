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
package com.agateau.tinywheels.racer;

import com.agateau.tinywheels.Assets;
import com.agateau.tinywheels.GameInput;
import com.agateau.tinywheels.GameInputHandler;
import com.agateau.tinywheels.GameWorld;
import com.agateau.tinywheels.Hud;

/**
 * A pilot controlled by the player
 */
public class PlayerPilot implements Pilot {
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final Racer mRacer;

    private GameInputHandler mInputHandler;

    public PlayerPilot(Assets assets, GameWorld gameWorld, Racer racer, GameInputHandler inputHandler) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mRacer = racer;
        mInputHandler = inputHandler;
    }

    public void createHudButtons(Hud hud) {
        mInputHandler.createHudButtons(mAssets, hud);
    }

    @Override
    public void act(float dt) {
        Vehicle vehicle = mRacer.getVehicle();

        if (mGameWorld.getState() == GameWorld.State.RUNNING) {
            mInputHandler.setBonus(mRacer.getBonus());
            GameInput input = mInputHandler.getGameInput();
            vehicle.setDirection(input.direction);
            vehicle.setAccelerating(input.accelerating);
            vehicle.setBraking(input.braking);
            if (input.triggeringBonus) {
                mRacer.triggerBonus();
            }
        }
    }
}
