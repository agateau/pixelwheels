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
package com.agateau.pixelwheels.racer;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.gameinput.GameInput;
import com.agateau.pixelwheels.gameinput.GameInputHandler;
import com.agateau.pixelwheels.gameinput.KeyboardInputHandler;
import com.agateau.pixelwheels.racescreen.Hud;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.ui.InputMapper;
import com.agateau.ui.VirtualKey;

/** A pilot controlled by the player */
public class PlayerPilot implements Pilot {
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final Racer mRacer;
    private final GameConfig mGameConfig;
    private final int mPlayerIndex;

    private GameInputHandler mInputHandler;
    private boolean mLastTriggering = false;

    public PlayerPilot(
            Assets assets,
            GameWorld gameWorld,
            Racer racer,
            GameConfig gameConfig,
            int playerIndex) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mRacer = racer;
        mGameConfig = gameConfig;
        mPlayerIndex = playerIndex;
        updateInputHandler();

        mGameConfig.addListener(() -> updateInputHandler());
    }

    public void createHudButtons(Hud hud) {
        hud.deleteInputUiContainer();
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
            if (input.triggeringBonus && !mLastTriggering) {
                mRacer.triggerBonus();
            }
            mLastTriggering = input.triggeringBonus;
        }
    }

    @Override
    public GameStats getGameStats() {
        return mGameWorld.getGameStats();
    }

    public boolean isPauseKeyPressed() {
        if (!(mInputHandler instanceof KeyboardInputHandler)) {
            return false;
        }
        InputMapper inputMapper = ((KeyboardInputHandler) mInputHandler).getInputMapper();
        return inputMapper.isKeyJustPressed(VirtualKey.BACK);
    }

    private void updateInputHandler() {
        mInputHandler = mGameConfig.getPlayerInputHandler(mPlayerIndex);
    }
}
