package com.agateau.tinywheels.sound;

import com.badlogic.gdx.audio.Sound;

/**
 * Implementation of SoundPlayer based on libgdx
 */
public class DefaultSoundPlayer implements SoundPlayer {
    private final Sound mSound;
    private long mId = -1;
    private boolean mLooping = false;
    private float mVolume = 1;
    private float mPitch = 1;
    private float mPan = 0;

    public DefaultSoundPlayer(Sound sound) {
        mSound = sound;
    }

    @Override
    public void play() {
        mId = mSound.play(mVolume, mPitch, mPan);
    }

    @Override
    public void loop() {
        mId = mSound.loop(mVolume, mPitch, mPan);
        mLooping = true;
    }

    @Override
    public void stop() {
        if (mId == -1) {
            return;
        }
        mSound.stop(mId);
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
        if (mId != -1) {
            mSound.setVolume(mId, mVolume);
        }
    }

    @Override
    public float getPitch() {
        return mPitch;
    }

    @Override
    public void setPitch(float pitch) {
        mPitch = pitch;
        if (mId != -1) {
            mSound.setPitch(mId, mPitch);
        }
    }

    @Override
    public boolean isLooping() {
        return mLooping;
    }
}
