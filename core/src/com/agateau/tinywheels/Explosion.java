package com.agateau.tinywheels;

import com.agateau.tinywheels.sound.AudioManager;

/**
 * An explosion animation
 */
public class Explosion {
    public static AnimationObject create(Assets assets, AudioManager audioManager, float x, float y) {
        AnimationObject obj = AnimationObject.create(assets.explosion, x, y);
        obj.initAudio(audioManager, assets.soundAtlas.get("explosion"));
        return obj;
    }
}
