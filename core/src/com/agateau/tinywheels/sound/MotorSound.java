package com.agateau.tinywheels.sound;

import com.agateau.tinywheels.SoundAtlas;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;

import java.util.Locale;

/**
 * Simulates the sound of a motor
 */
public class MotorSound {
    private static final float MIN_PITCH = 1f;
    private static final float MAX_PITCH = 3f;

    private static class SoundInfo {
        final Sound sound;
        long id = -1;

        SoundInfo(SoundAtlas atlas, String name) {
            sound = atlas.get(name);
        }

        void play(float volume, float pitch) {
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

    private final SoundInfo[] mEngine = new SoundInfo[5];

    public MotorSound(SoundAtlas atlas) {
        for (int i = 0; i < mEngine.length; ++i) {
            mEngine[i] = new SoundInfo(atlas, String.format(Locale.US, "engine-%d", i));
        }
    }

    public void play(float speed) {
        float pitch = Interpolation.pow2Out.apply(MIN_PITCH, MAX_PITCH, speed);
        float idx = speed * (mEngine.length - 1);
        for (int i = 0; i < mEngine.length; ++i) {
            float di = Math.abs(i - idx);
            float volume = Math.max(1 - di, 0);
            mEngine[i].play(volume, pitch);
        }
    }
}
