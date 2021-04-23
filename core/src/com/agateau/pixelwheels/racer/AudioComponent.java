/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.pixelwheels.racer;

import com.agateau.pixelwheels.BodyIdentifier;
import com.agateau.pixelwheels.gameobjet.AudioClipper;
import com.agateau.pixelwheels.racescreen.Collidable;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.sound.EngineSoundPlayer;
import com.agateau.pixelwheels.sound.SoundAtlas;
import com.agateau.pixelwheels.sound.SoundPlayer;
import com.agateau.pixelwheels.sound.SoundSettings;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/** A component to play the racer audio */
class AudioComponent implements Racer.Component, Disposable, Collidable {
    private static final float FULL_VOLUME_DRIFT_DURATION = 0.6f;
    private static final float MIN_IMPACT_SPEED = 3;

    private static final float MIN_COLLISION_PITCH = 0.5f;
    private static final float MAX_COLLISION_PITCH = 2f;

    private static final float ICE_DRIFT_PITCH = 0.5f;

    private final AudioManager mAudioManager;
    private final EngineSoundPlayer mEngineSoundPlayer;
    private final Racer mRacer;
    private final SoundPlayer mDriftingSoundPlayer;
    private final SoundPlayer mTurboSoundPlayer;
    private final SoundPlayer mCollisionSoundPlayer;
    private final SoundPlayer mSplashSoundPlayer;
    private final Array<SoundPlayer> mSoundPlayers = new Array<>();
    private float mDriftDuration = 0;
    private boolean mTurboTriggered = false;
    private boolean mJustCollided = false;

    public AudioComponent(SoundAtlas atlas, AudioManager audioManager, Racer racer) {
        mAudioManager = audioManager;
        if (racer.getEntrant().isPlayer()) {
            mEngineSoundPlayer = new EngineSoundPlayer(atlas, audioManager);
        } else {
            mEngineSoundPlayer = null;
        }
        mDriftingSoundPlayer = audioManager.createSoundPlayer(atlas.get("drifting"));
        mTurboSoundPlayer = audioManager.createSoundPlayer(atlas.get("turbo"));
        mCollisionSoundPlayer = audioManager.createSoundPlayer(atlas.get("collision"));
        mSplashSoundPlayer = audioManager.createSoundPlayer(atlas.get("splash"));
        mSoundPlayers.addAll(mDriftingSoundPlayer, mTurboSoundPlayer, mCollisionSoundPlayer);
        mRacer = racer;
    }

    public AudioManager getAudioManager() {
        return mAudioManager;
    }

    @Override
    public void act(float delta) {
        if (mRacer.getVehicle().isDrifting() || mRacer.getVehicle().isIceDrifting()) {
            mDriftDuration += delta;
        } else {
            mDriftDuration = 0;
        }
    }

    public void render(AudioClipper clipper) {
        float speed = mRacer.getVehicle().getSpeed();
        float normSpeed = MathUtils.clamp(speed / 50, 0, 1);
        float maxVolume = SoundSettings.instance.engineVolume * clipper.clip(mRacer);
        if (mEngineSoundPlayer != null) {
            mEngineSoundPlayer.play(normSpeed, maxVolume);
        }

        if (mDriftDuration > 0) {
            float volume =
                    MathUtils.clamp(mDriftDuration / FULL_VOLUME_DRIFT_DURATION, 0f, 1f)
                            * maxVolume;
            mDriftingSoundPlayer.setPitch(
                    mRacer.getVehicle().isIceDrifting() ? ICE_DRIFT_PITCH : 1f);
            mDriftingSoundPlayer.setVolume(volume * SoundSettings.instance.driftVolume);
            if (!mDriftingSoundPlayer.isLooping()) {
                mDriftingSoundPlayer.loop();
            }
        } else {
            mDriftingSoundPlayer.stop();
        }

        if (mTurboTriggered) {
            mTurboSoundPlayer.setVolume(maxVolume * SoundSettings.instance.turboVolume);
            mTurboSoundPlayer.play();
            mTurboTriggered = false;
        }

        if (mJustCollided) {
            mCollisionSoundPlayer.setVolume(maxVolume);
            if (!mCollisionSoundPlayer.isLooping()) {
                float pitch = MathUtils.random(MIN_COLLISION_PITCH, MAX_COLLISION_PITCH);
                mCollisionSoundPlayer.setPitch(pitch);
                mCollisionSoundPlayer.loop();
            }
            mJustCollided = false;
        } else {
            mCollisionSoundPlayer.stop();
        }

        if (mRacer.getVehicle().isOnWater()) {
            mSplashSoundPlayer.setVolume(normSpeed * maxVolume);
            if (!mSplashSoundPlayer.isLooping()) {
                mSplashSoundPlayer.loop();
            }
        } else {
            mSplashSoundPlayer.stop();
        }
    }

    public void triggerTurbo() {
        mTurboTriggered = true;
    }

    private void onCollision() {
        if (mRacer.getVehicle().getSpeed() > MIN_IMPACT_SPEED) {
            mJustCollided = true;
        }
    }

    @Override
    public void dispose() {
        if (mEngineSoundPlayer != null) {
            mEngineSoundPlayer.stop();
        }
        for (SoundPlayer soundPlayer : mSoundPlayers) {
            soundPlayer.stop();
        }
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {}

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {}

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {
        Body otherBody = otherFixture.getBody();
        if (BodyIdentifier.isVehicle(otherBody) || BodyIdentifier.isWall(otherBody)) {
            onCollision();
        }
    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {}
}
