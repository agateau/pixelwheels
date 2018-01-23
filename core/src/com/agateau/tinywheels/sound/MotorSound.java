package com.agateau.tinywheels.sound;

import com.agateau.tinywheels.SoundAtlas;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;

/**
 * Simulates the sound of a motor
 */
public class MotorSound {
    public static final float MIN_PITCH = 1f;
    public static final float MAX_PITCH = 3f;
    private float mPitch = MIN_PITCH;

    private static class SoundInfo {
        final Sound sound;
        long id = -1;
        float volume = 0;

        SoundInfo(Sound sound) {
            this.sound = sound;
        }

        void play(float volume, float pitch) {
            this.volume = volume;
            if (volume == 0) {
                if (id != -1) {
                    sound.stop();
                    id = -1;
                }
                return;
            }
            if (id == -1) {
                id = sound.loop(volume, pitch, 0);
            } else {
                sound.setVolume(id, volume);
                sound.setPitch(id, pitch);
            }
        }
    }

    private final Array<SoundInfo> mEngine = new Array<SoundInfo>();

    public int getSoundCount() {
        return mEngine.size;
    }

    public float getSoundVolume(int idx) {
        return mEngine.get(idx).volume;
    }

    public float getPitch() {
        return mPitch;
    }

    public MotorSound(SoundAtlas atlas) {
        for (int i = 0;; ++i) {
            String name = String.format(Locale.US, "engine-%d", i);
            if (!atlas.contains(name)) {
                break;
            }
            Sound sound = atlas.get(name);
            mEngine.add(new SoundInfo(sound));
        }
    }

    public void play(float speed) {
        mPitch = Interpolation.pow2Out.apply(MIN_PITCH, MAX_PITCH, speed);
        float idx = speed * (mEngine.size - 1);
        for (int i = 0; i < mEngine.size; ++i) {
            float di = Math.abs(i - idx);
            float volume = Math.max(1 - di, 0);
            mEngine.get(i).play(volume, mPitch);
        }
    }
}
