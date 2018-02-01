package com.agateau.tinywheels.sound;

/**
 * Plays a sound
 */
public interface SoundPlayer {
    void play();
    void loop();
    void stop();
    void setVolume(float volume);
    void setPitch(float pitch);
    boolean isLooping();
}
