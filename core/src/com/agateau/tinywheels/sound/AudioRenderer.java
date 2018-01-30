package com.agateau.tinywheels.sound;

import com.agateau.tinywheels.GameObject;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

/**
 * Render audio
 */
public interface AudioRenderer {
    void render(float delta, Array<? extends GameObject> gameObjects, AudioClipper audioClipper);

    /**
     * Basic method for simple sounds
     */
    void play(Sound sound, float volume);

    /**
     * Create a SoundPlayer, for more advanced controls
     */
    SoundPlayer getSoundPlayer(Sound sound);
}
