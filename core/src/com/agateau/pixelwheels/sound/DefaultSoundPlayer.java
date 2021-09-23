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

/** Implementation of SoundPlayer based on libgdx */
public class DefaultSoundPlayer implements SoundPlayer {
    private final SoundThreadManager mSoundThreadManager;
    private final Sound mSound;
    private long mId = -1;
    private boolean mLooping = false;
    private float mVolume = 1;
    private float mPitch = 1;
    private boolean mMuted = false;

    public DefaultSoundPlayer(SoundThreadManager soundThreadManager, Sound sound) {
        mSoundThreadManager = soundThreadManager;
        mSound = sound;
    }

    @Override
    public void play() {
        if (mMuted) {
            return;
        }
        stop();
        mId = mSoundThreadManager.play(mSound, mVolume, mPitch);
    }

    @Override
    public void loop() {
        if (mMuted) {
            return;
        }
        stop();
        mId = mSoundThreadManager.loop(mSound, mVolume, mPitch);
        mLooping = true;
    }

    @Override
    public void stop() {
        if (mId == -1) {
            return;
        }
        mSoundThreadManager.stop(mId);
        mId = -1;
        mLooping = false;
    }

    @Override
    public float getVolume() {
        return mVolume;
    }

    @Override
    public void setVolume(float volume) {
        mVolume = volume;
        updateVolume();
    }

    @Override
    public float getPitch() {
        return mPitch;
    }

    @Override
    public void setPitch(float pitch) {
        mPitch = pitch;
        if (mId != -1) {
            mSoundThreadManager.setPitch(mId, mPitch);
        }
    }

    @Override
    public boolean isLooping() {
        return mLooping;
    }

    void setMuted(boolean muted) {
        mMuted = muted;
        updateVolume();
        if (mMuted) {
            stop();
        }
    }

    private void updateVolume() {
        if (mId != -1) {
            mSoundThreadManager.setVolume(mId, mMuted ? 0 : mVolume);
        }
    }
}
