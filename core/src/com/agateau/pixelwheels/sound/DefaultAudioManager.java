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
package com.agateau.pixelwheels.sound;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

import java.lang.ref.WeakReference;

/**
 * Default implementation of AudioManager
 */
public class DefaultAudioManager implements AudioManager {
    private boolean mMuted = false;
    private Array<WeakReference<DefaultSoundPlayer>> mSoundPlayers = new Array<WeakReference<DefaultSoundPlayer>>();

    public boolean isMuted() {
        return mMuted;
    }

    public void setMuted(boolean muted) {
        mMuted = muted;
        for (WeakReference<DefaultSoundPlayer> ref : mSoundPlayers) {
            DefaultSoundPlayer player = ref.get();
            if (player != null) {
                player.setMuted(muted);
            }
        }
    }

    @Override
    public void play(Sound sound, float volume) {
        if (mMuted) {
            return;
        }
        sound.play(volume);
    }

    @Override
    public SoundPlayer createSoundPlayer(Sound sound) {
        DefaultSoundPlayer player = new DefaultSoundPlayer(sound);
        player.setMuted(mMuted);
        mSoundPlayers.add(new WeakReference<DefaultSoundPlayer>(player));
        return player;
    }
}
