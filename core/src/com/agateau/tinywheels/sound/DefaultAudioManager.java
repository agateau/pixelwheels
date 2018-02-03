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
package com.agateau.tinywheels.sound;

import com.badlogic.gdx.audio.Sound;

/**
 * Default implementation of AudioManager
 */
public class DefaultAudioManager implements AudioManager {
    @Override
    public void play(Sound sound, float volume) {
        sound.play(volume);
    }

    @Override
    public SoundPlayer createSoundPlayer(Sound sound) {
        return new DefaultSoundPlayer(sound);
    }
}
