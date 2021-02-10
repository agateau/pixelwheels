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
package com.agateau.pixelwheels;

import com.agateau.pixelwheels.bonus.BonusPool;
import com.agateau.pixelwheels.gameobjet.GameObject;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racescreen.CountDown;
import com.agateau.pixelwheels.stats.GameStats;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/** Contains all the information and objects running in the world */
public interface GameWorld {
    float BOX2D_TIME_STEP = 1f / 60f;
    int VELOCITY_ITERATIONS = 6;
    int POSITION_ITERATIONS = 2;

    enum State {
        COUNTDOWN,
        RUNNING,
        FINISHED
    }

    Track getTrack();

    World getBox2DWorld();

    Racer getPlayerRacer(int playerId);

    Array<Racer> getPlayerRacers();

    Array<Racer> getRacers();

    @SuppressWarnings("rawtypes")
    Array<BonusPool> getBonusPools();

    Array<GameObject> getActiveGameObjects();

    void addGameObject(GameObject object);

    CountDown getCountDown();

    int getRacerRank(Racer racer);

    float getRacerNormalizedRank(Racer racer);

    GameStats getGameStats();

    void act(float delta);

    State getState();

    void startRace();

    void setState(State state);
}
