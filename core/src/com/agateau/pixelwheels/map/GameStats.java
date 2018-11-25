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
package com.agateau.pixelwheels.map;

import java.util.HashMap;

public class GameStats {
    private final IO mIO;
    private final HashMap<String, TrackStats> mTrackStats = new HashMap<String, TrackStats>();

    interface IO {
        void setGameStats(GameStats gameStats);
        void load();
        void save();
    }

    public GameStats(IO io) {
        mIO = io;
        mIO.setGameStats(this);
        mIO.load();
    }

    public TrackStats getTrackStats(String trackId) {
        return mTrackStats.get(trackId);
    }

    public void addTrack(String trackId) {
        mTrackStats.put(trackId, new TrackStats(mIO));
    }

    void save() {
        mIO.save();
    }
}
