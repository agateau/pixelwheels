package com.agateau.tinywheels;

import com.agateau.tinywheels.sound.EngineSound;
import com.badlogic.gdx.math.MathUtils;

/**
 * A component to play the racer audio
 */
class AudioComponent implements Racer.Component {

    private final EngineSound mEngineSound;
    private final Vehicle mVehicle;

    public AudioComponent(SoundAtlas atlas, Vehicle vehicle) {
        mEngineSound = new EngineSound(atlas);
        mVehicle = vehicle;
    }

    @Override
    public void act(float delta) {
        float speed = mVehicle.getSpeed();
        float normSpeed = MathUtils.clamp(speed / 50, 0, 1);
        mEngineSound.play(normSpeed);
    }
}
