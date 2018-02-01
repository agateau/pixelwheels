package com.agateau.tinywheels.sound;

import com.agateau.tinywheels.GameObject;
import com.badlogic.gdx.utils.Array;

/**
 * Render audio
 */
public interface AudioRenderer {
    void render(float delta, Array<? extends GameObject> gameObjects, AudioClipper audioClipper);
}
