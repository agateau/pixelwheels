package com.agateau.tinywheels;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

import java.util.Locale;

/**
 * A component to play the racer audio
 */
class AudioComponent implements Racer.Component {
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

    private final Vehicle mVehicle;
    private final SoundInfo[] mEngine = new SoundInfo[5];

    public AudioComponent(SoundAtlas atlas, Vehicle vehicle) {
        mVehicle = vehicle;
        for (int i = 0; i < mEngine.length; ++i) {
            mEngine[i] = new SoundInfo(atlas, String.format(Locale.US, "engine-%d", i));
        }
    }

    @Override
    public void act(float delta) {
        float speed = mVehicle.getSpeed();
        float normSpeed = MathUtils.clamp(speed / 50, 0, 1);
        float pitch = Interpolation.pow2Out.apply(MIN_PITCH, MAX_PITCH, normSpeed);
        float idx = normSpeed * (mEngine.length - 1);
        for (int i = 0; i < mEngine.length; ++i) {
            float di = Math.abs(i - idx);
            float volume = Math.max(1 - di, 0);
            mEngine[i].play(volume, pitch);
        }
    }
}
