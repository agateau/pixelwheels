package com.agateau.tinywheels;

import com.agateau.tinywheels.sound.AudioClipper;
import com.agateau.tinywheels.sound.AudioRenderer;
import com.agateau.tinywheels.sound.EngineSound;
import com.agateau.tinywheels.sound.SoundPlayer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

/**
 * A component to play the racer audio
 */
class AudioComponent implements Racer.Component {
    private static final float FULL_VOLUME_DRIFT_DURATION = 0.6f;

    private final EngineSound mEngineSound;
    private final Sound mDriftingSound;
    private final Racer mRacer;
    private SoundPlayer mDriftingSoundPlayer;

    private float mDriftDuration = 0;
    private long mDriftingSoundId = -1;

    public AudioComponent(SoundAtlas atlas, Racer racer) {
        mEngineSound = new EngineSound(atlas);
        mDriftingSound = atlas.get("drifting");
        mRacer = racer;
    }

    @Override
    public void act(float delta) {
        float speed = mRacer.getVehicle().getSpeed();
        float normSpeed = MathUtils.clamp(speed / 50, 0, 1);
        mEngineSound.play(normSpeed);

        if (mRacer.getVehicle().isDrifting()) {
            mDriftDuration += delta;
        } else {
            mDriftDuration = 0;
        }
    }

    public void render(AudioRenderer renderer, AudioClipper clipper) {
        if (mDriftingSoundPlayer == null) {
            mDriftingSoundPlayer = renderer.getSoundPlayer(mDriftingSound);
        }
        if (mDriftDuration > 0) {
            float volume = MathUtils.clamp(mDriftDuration / FULL_VOLUME_DRIFT_DURATION, 0f, 1f)
                    * clipper.clip(mRacer);
            if (mDriftingSoundPlayer.isLooping()) {
                mDriftingSoundPlayer.setVolume(volume);
            } else {
                mDriftingSoundPlayer.loop(volume);
            }
        } else {
            mDriftingSoundPlayer.stop();
        }
    }
}
