package com.agateau.tinywheels.sound;

import com.badlogic.gdx.audio.Sound;

/**
 * Default implementation of AudioManager
 */
public class DefaultAudioManager implements AudioManager {
    @Override
    public AudioRenderer createAudioRenderer() {
        return new DefaultAudioRenderer();
    }

    @Override
    public void play(Sound sound, float volume) {
        sound.play(volume);
    }

    @Override
    public SoundPlayer createSoundPlayer(Sound sound) {
        return new DefaultSoundPlayer(sound);
    }
}
