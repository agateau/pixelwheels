package com.agateau.tinywheels.sound;

import com.agateau.tinywheels.SoundAtlas;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;

/**
 * Simulates the sound of a vehicle engine
 */
public class EngineSoundPlayer {
    public static final float MIN_PITCH = 1f;
    public static final float MAX_PITCH = 3f;
    private float mPitch = MIN_PITCH;

    private final Array<SoundPlayer> mSoundPlayers = new Array<SoundPlayer>();

    public int getSoundCount() {
        return mSoundPlayers.size;
    }

    public float getSoundVolume(int idx) {
        return mSoundPlayers.get(idx).getVolume();
    }

    public float getPitch() {
        return mPitch;
    }

    public EngineSoundPlayer(SoundAtlas atlas, AudioManager audioManager) {
        for (int i = 0;; ++i) {
            String name = String.format(Locale.US, "engine-%d", i);
            if (!atlas.contains(name)) {
                break;
            }
            Sound sound = atlas.get(name);
            mSoundPlayers.add(audioManager.createSoundPlayer(sound));
        }
    }

    public void play(float speed, float maxVolume) {
        mPitch = Interpolation.pow2Out.apply(MIN_PITCH, MAX_PITCH, speed);
        float idx = speed * (mSoundPlayers.size - 1);
        for (int i = 0; i < mSoundPlayers.size; ++i) {
            float di = Math.abs(i - idx);
            float volume = Math.max(1 - di, 0) * maxVolume;
            SoundPlayer player = mSoundPlayers.get(i);
            player.setVolume(volume);
            player.setPitch(mPitch);
            if (volume > 0.01) {
                if (!player.isLooping()) {
                    player.loop();
                }
            } else {
                player.stop();
            }
        }
    }

    public void stop() {
        for (SoundPlayer player : mSoundPlayers) {
            player.stop();
        }
    }
}
