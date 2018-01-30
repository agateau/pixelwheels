package com.agateau.tinywheels.sound;

import com.badlogic.gdx.audio.Sound;

/**
 * Implementation of SoundPlayer based on libgdx
 */
public class DefaultSoundPlayer implements SoundPlayer {
    private final Sound mSound;
    private long mId = -1;
    private boolean mLooping = false;

    public DefaultSoundPlayer(Sound sound) {
        mSound = sound;
    }

    @Override
    public void play(float volume) {
        mId = mSound.play(volume);
    }

    @Override
    public void loop(float volume) {
        mId = mSound.loop(volume);
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
    public void setVolume(float volume) {
        mSound.setVolume(mId, volume);
    }

    @Override
    public boolean isLooping() {
        return mLooping;
    }
}
