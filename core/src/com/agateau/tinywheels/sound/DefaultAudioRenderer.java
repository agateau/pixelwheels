package com.agateau.tinywheels.sound;

import com.agateau.tinywheels.GameObject;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

/**
 * Default implementation of AudioRenderer
 */
public class DefaultAudioRenderer implements AudioRenderer {
    @Override
    public void render(float delta, Array<? extends GameObject> gameObjects, AudioClipper audioClipper) {
        for (GameObject gameObject : gameObjects) {
            gameObject.audioRender(this, audioClipper);
        }
    }

    @Override
    public SoundPlayer getSoundPlayer(Sound sound) {
        return new DefaultSoundPlayer(sound);
    }

    @Override
    public void play(Sound sound, float volume) {
        sound.play(volume);
    }
}
