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
package com.agateau.pixelwheels.sound;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import java.util.Locale;

/** Simulates the sound of a vehicle engine */
public class EngineSoundPlayer {
    public static final float MIN_PITCH = 0.5f;
    public static final float MAX_PITCH = 2f;
    private float mPitch = MIN_PITCH;

    private final Array<SoundPlayer> mSoundPlayers = new Array<>();

    public int getSoundCount() {
        return mSoundPlayers.size;
    }

    public float getSoundVolume(int idx) {
        return mSoundPlayers.get(idx).getVolume();
    }

    public float getPitch() {
        return mPitch;
    }

    public EngineSoundPlayer(SoundAtlas atlas, AudioManager audioManager) {
        for (int i = 0; ; ++i) {
            String name = String.format(Locale.US, "engine-%d", i);
            if (!atlas.contains(name)) {
                break;
            }
            Sound sound = atlas.get(name);
            mSoundPlayers.add(audioManager.createSoundPlayer(sound));
        }
    }

    public void play(float speed, float maxVolume) {
        mPitch = Interpolation.pow2Out.apply(MIN_PITCH, MAX_PITCH, speed);
        float idx = speed * (mSoundPlayers.size - 1);
        for (int i = 0; i < mSoundPlayers.size; ++i) {
            float di = Math.abs(i - idx);
            float volume = Math.max(1 - di, 0) * maxVolume;
            SoundPlayer player = mSoundPlayers.get(i);
            player.setVolume(volume);
            player.setPitch(mPitch);
            if (volume > 0.01) {
                if (!player.isLooping()) {
                    player.loop();
                }
            } else {
                player.stop();
            }
        }
    }

    public void stop() {
        for (SoundPlayer player : mSoundPlayers) {
            player.stop();
        }
    }
}
