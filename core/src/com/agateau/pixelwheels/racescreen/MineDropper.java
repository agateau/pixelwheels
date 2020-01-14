/*
 * Copyright 2020 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.racescreen;

import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.bonus.Mine;
import com.agateau.pixelwheels.gameobjet.GameObjectAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

/** A debug helper class to drop mines by clicking on the map */
class MineDropper extends GameObjectAdapter {
    private final Vector2 mCoord = new Vector2();
    private final PwGame mGame;
    private final GameWorld mGameWorld;
    private final GameRenderer mGameRenderer;

    MineDropper(PwGame game, GameWorld gameWorld, GameRenderer gameRenderer) {
        mGame = game;
        mGameWorld = gameWorld;
        mGameRenderer = gameRenderer;
    }

    @Override
    public void act(float delta) {
        if (Gdx.input.justTouched()) {
            mCoord.set(Gdx.input.getX(), Gdx.input.getY());
            mGameRenderer.mapFromScreen(mCoord);

            Mine.createDroppedMine(mGameWorld, mGame.getAssets(), mGame.getAudioManager(), mCoord);
        }
    }

    @Override
    public void draw(Batch batch, ZLevel zLevel) {}

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }
}
