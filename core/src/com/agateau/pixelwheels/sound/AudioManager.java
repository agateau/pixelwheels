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
package com.agateau.pixelwheels.sound;

import com.badlogic.gdx.audio.Sound;

/** Create audio objects */
public interface AudioManager {
    boolean areSoundFxMuted();

    void setSoundFxMuted(boolean muted);

    boolean isMusicMuted();

    void setMusicMuted(boolean muted);

    /** Basic method for simple sounds */
    void play(Sound sound, float volume);

    /** Create a SoundPlayer, for more advanced controls */
    SoundPlayer createSoundPlayer(Sound sound);

    void playMusic(String musicId);

    void fadeOutMusic();
}
