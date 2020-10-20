/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.sound.SoundAtlas;
import com.badlogic.gdx.math.MathUtils;

/** Handles the non-visible part of the count down */
public class CountDown {
    private static final int START = 3;
    private static final float TICK_DURATION = 0.75f;

    private final GameWorld mGameWorld;
    private final AudioManager mAudioManager;
    private final SoundAtlas mSoundAtlas;

    private float mTime = START;
    private boolean mFirstCall = true;

    public CountDown(GameWorld gameWorld, AudioManager audioManager, SoundAtlas soundAtlas) {
        mGameWorld = gameWorld;
        mAudioManager = audioManager;
        mSoundAtlas = soundAtlas;
    }

    public int getValue() {
        return MathUtils.ceil(mTime);
    }

    public float getPercent() {
        return mTime - MathUtils.floor(mTime);
    }

    public boolean isFinished() {
        // GO! message is fully gone
        return mTime < -1;
    }

    public void act(float delta) {
        if (isFinished()) {
            return;
        }
        int oldValue = getValue();
        mTime -= delta / TICK_DURATION;
        int newValue = getValue();
        if ((oldValue != newValue && newValue >= 0) || mFirstCall) {
            mFirstCall = false;
            String soundName = newValue > 0 ? "countdown1" : "countdown2";
            mAudioManager.play(mSoundAtlas.get(soundName), 1f);
            if (newValue == 0) {
                mGameWorld.startRace();
            }
        }
    }
}
