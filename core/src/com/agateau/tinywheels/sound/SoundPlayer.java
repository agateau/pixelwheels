package com.agateau.tinywheels.sound;

/**
 * Plays a sound
 */
public interface SoundPlayer {
    void play(float volume);
    void loop(float volume);
    void stop();
    void setVolume(float volume);
    boolean isLooping();
}
