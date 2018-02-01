package com.agateau.tinywheels.sound;

import com.badlogic.gdx.audio.Sound;

/**
 * Create audio objects
 */
public interface AudioManager {
    /**
     * Basic method for simple sounds
     */
    void play(Sound sound, float volume);

    /**
     * Create a SoundPlayer, for more advanced controls
     */
    SoundPlayer createSoundPlayer(Sound sound);
}
