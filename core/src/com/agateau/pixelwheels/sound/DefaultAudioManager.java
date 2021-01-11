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

import com.agateau.pixelwheels.Assets;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import java.lang.ref.WeakReference;

/** Default implementation of AudioManager */
public class DefaultAudioManager implements AudioManager {
    private boolean mSoundFxMuted = false;
    private boolean mMusicMuted = false;
    private final Assets mAssets;
    private final Array<WeakReference<DefaultSoundPlayer>> mSoundPlayers = new Array<>();
    private final MusicFader mMusicFader = new MusicFader();

    private String mMusicId = "";
    private Music mMusic;

    public DefaultAudioManager(Assets assets) {
        mAssets = assets;
    }

    public boolean areSoundFxMuted() {
        return mSoundFxMuted;
    }

    public void setSoundFxMuted(boolean muted) {
        if (mSoundFxMuted == muted) {
            return;
        }
        mSoundFxMuted = muted;
        for (WeakReference<DefaultSoundPlayer> ref : mSoundPlayers) {
            DefaultSoundPlayer player = ref.get();
            if (player != null) {
                player.setMuted(muted);
            }
        }
    }

    @Override
    public boolean isMusicMuted() {
        return mMusicMuted;
    }

    @Override
    public void setMusicMuted(boolean muted) {
        if (mMusicMuted == muted) {
            return;
        }
        mMusicMuted = muted;
        if (mMusic != null) {
            if (mMusicMuted) {
                mMusic.stop();
            } else {
                mMusic.play();
            }
        }
    }

    @Override
    public void play(Sound sound, float volume) {
        if (mSoundFxMuted) {
            return;
        }
        sound.play(volume);
    }

    @Override
    public SoundPlayer createSoundPlayer(Sound sound) {
        DefaultSoundPlayer player = new DefaultSoundPlayer(sound);
        player.setMuted(mSoundFxMuted);
        mSoundPlayers.add(new WeakReference<>(player));
        return player;
    }

    @Override
    public void playMusic(String musicId) {
        if (mMusicId.equals(musicId)) {
            return;
        }
        if (mMusic != null) {
            fadeOutMusic();
        }
        mMusicId = musicId;
        mMusic = mAssets.loadMusic(mMusicId);
        if (mMusic == null) {
            NLog.e("Failed to load music %s", musicId);
            return;
        }
        mMusic.setLooping(true);
        if (!mMusicMuted) {
            mMusic.play();
        }
    }

    @Override
    public void fadeOutMusic() {
        if (mMusic == null || mMusicMuted) {
            return;
        }
        mMusicFader.fadeOut(mMusic);
        // Forget the current music: fader takes care of stopping and disposing it
        mMusic = null;
        mMusicId = "";
    }
}
