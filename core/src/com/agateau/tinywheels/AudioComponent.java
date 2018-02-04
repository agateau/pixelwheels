/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.tinywheels;

import com.agateau.tinywheels.gameobjet.AudioClipper;
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
    private static final float MIN_IMPACT_SPEED = 3;

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
        if (mRacer.getVehicle().getSpeed() > MIN_IMPACT_SPEED) {
            mJustCollided = true;
        }
    }

    @Override
    public void dispose() {
        mEngineSoundPlayer.stop();
        for (SoundPlayer soundPlayer : mSoundPlayers) {
            soundPlayer.stop();
        }
    }
}
