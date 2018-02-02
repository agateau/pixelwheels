package com.agateau.tinywheels;

import com.agateau.tinywheels.sound.AudioClipper;
import com.agateau.tinywheels.sound.AudioManager;
import com.agateau.tinywheels.sound.EngineSoundPlayer;
import com.agateau.tinywheels.sound.SoundPlayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * A component to play the racer audio
 */
class AudioComponent implements Racer.Component, Disposable {
    private static final float FULL_VOLUME_DRIFT_DURATION = 0.6f;

    private final EngineSoundPlayer mEngineSoundPlayer;
    private final Racer mRacer;
    private final SoundPlayer mDriftingSoundPlayer;
    private final SoundPlayer mTurboSoundPlayer;
    private final SoundPlayer mCollisionSoundPlayer;
    private final Array<SoundPlayer> mSoundPlayers = new Array<SoundPlayer>();
    private float mDriftDuration = 0;
    private boolean mTurboTriggered = false;
    private boolean mJustCollided = false;

    public AudioComponent(SoundAtlas atlas, AudioManager audioManager, Racer racer) {
        mEngineSoundPlayer = new EngineSoundPlayer(atlas, audioManager);
        mDriftingSoundPlayer = audioManager.createSoundPlayer(atlas.get("drifting"));
        mTurboSoundPlayer = audioManager.createSoundPlayer(atlas.get("turbo"));
        mCollisionSoundPlayer = audioManager.createSoundPlayer(atlas.get("collision"));
        mSoundPlayers.addAll(mDriftingSoundPlayer, mTurboSoundPlayer, mCollisionSoundPlayer);
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

    public void render(AudioClipper clipper) {
        float speed = mRacer.getVehicle().getSpeed();
        float normSpeed = MathUtils.clamp(speed / 50, 0, 1);
        float maxVolume = clipper.clip(mRacer);
        mEngineSoundPlayer.play(normSpeed, maxVolume);

        if (mDriftDuration > 0) {
            float volume = MathUtils.clamp(mDriftDuration / FULL_VOLUME_DRIFT_DURATION, 0f, 1f) * maxVolume;
            mDriftingSoundPlayer.setVolume(volume);
            if (!mDriftingSoundPlayer.isLooping()) {
                mDriftingSoundPlayer.loop();
            }
        } else {
            mDriftingSoundPlayer.stop();
        }

        if (mTurboTriggered) {
            mTurboSoundPlayer.setVolume(maxVolume);
            mTurboSoundPlayer.play();
            mTurboTriggered = false;
        }

        if (mJustCollided) {
            mCollisionSoundPlayer.setVolume(maxVolume);
            if (!mCollisionSoundPlayer.isLooping()) {
                mCollisionSoundPlayer.loop();
            }
            mJustCollided = false;
        } else {
            mCollisionSoundPlayer.stop();
        }
    }

    public void triggerTurbo() {
        mTurboTriggered = true;
    }

    public void onCollision() {
        mJustCollided = true;
    }

    @Override
    public void dispose() {
        mEngineSoundPlayer.stop();
        for (SoundPlayer soundPlayer : mSoundPlayers) {
            soundPlayer.stop();
        }
    }
}
