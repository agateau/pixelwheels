package com.agateau.tinywheels;

import com.agateau.tinywheels.sound.AudioClipper;
import com.agateau.tinywheels.sound.AudioRenderer;
import com.agateau.tinywheels.sound.EngineSoundPlayer;
import com.agateau.tinywheels.sound.SoundPlayer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

/**
 * A component to play the racer audio
 */
class AudioComponent implements Racer.Component {
    private static final float FULL_VOLUME_DRIFT_DURATION = 0.6f;

    private final EngineSoundPlayer mEngineSoundPlayer;
    private final Sound mDriftingSound;
    private final Racer mRacer;
    private SoundPlayer mDriftingSoundPlayer;

    private float mDriftDuration = 0;

    public AudioComponent(SoundAtlas atlas, Racer racer) {
        mEngineSoundPlayer = new EngineSoundPlayer(atlas);
        mDriftingSound = atlas.get("drifting");
        mRacer = racer;
    }

    @Override
    public void act(float delta) {
        if (mRacer.getVehicle().isDrifting()) {
            mDriftDuration += delta;
        } else {
            mDriftDuration = 0;
        }
    }

    public void render(AudioRenderer renderer, AudioClipper clipper) {
        float speed = mRacer.getVehicle().getSpeed();
        float normSpeed = MathUtils.clamp(speed / 50, 0, 1);
        mEngineSoundPlayer.play(normSpeed);

        if (mDriftingSoundPlayer == null) {
            mDriftingSoundPlayer = renderer.getSoundPlayer(mDriftingSound);
        }

        if (mDriftDuration > 0) {
            float volume = MathUtils.clamp(mDriftDuration / FULL_VOLUME_DRIFT_DURATION, 0f, 1f)
                    * clipper.clip(mRacer);
            mDriftingSoundPlayer.setVolume(volume);
            if (!mDriftingSoundPlayer.isLooping()) {
                mDriftingSoundPlayer.loop();
            }
        } else {
            mDriftingSoundPlayer.stop();
        }
    }
}
