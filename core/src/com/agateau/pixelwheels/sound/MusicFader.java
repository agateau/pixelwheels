/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
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

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Timer;

/** Helper class to fade-out the currently playing music */
class MusicFader extends Timer.Task {
    private static final float FADEOUT_DURATION = 1;
    private static final float UPDATE_INTERVAL = 0.05f;
    private Music mMusic;

    @Override
    public void run() {
        float volume = mMusic.getVolume();
        volume -= UPDATE_INTERVAL / FADEOUT_DURATION;
        if (volume > 0) {
            mMusic.setVolume(volume);
            Timer.schedule(this, UPDATE_INTERVAL);
        } else {
            mMusic.stop();
            mMusic.dispose();
            mMusic = null;
        }
    }

    public void fadeOut(Music music) {
        if (music == null) {
            return;
        }
        if (mMusic != null) {
            mMusic.stop();
            mMusic.dispose();
        }
        mMusic = music;
        Timer.post(this);
    }
}
