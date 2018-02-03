package com.agateau.tinywheels.sound;

import com.agateau.tinywheels.GameObject;

/**
 * Filter a GameObject to "clip" it if it is too far
 */
public interface AudioClipper {
    float clip(GameObject gameObject);
}
