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

public class TrackStats {

    private class TrackResults {
        final TrackRecords lapRecords = new TrackRecords(TrackStats.this);
        final TrackRecords totalRecords = new TrackRecords(TrackStats.this);

        TrackRecords get(ResultType resultType) {
            return resultType == ResultType.LAP ? lapRecords : totalRecords;
        }
    }

    private final IO mIO;
    private final HashMap<String, TrackResults> mTrackResults = new HashMap<String, TrackResults>();

    interface IO {
        void load(TrackStats trackStats);
        void save(TrackStats trackStats);
    }

    public enum ResultType {
        LAP,
        TOTAL
    }

    public TrackStats(IO io) {
        mIO = io;
    }

    TrackRecords getRecords(String trackId, ResultType resultType) {
        return mTrackResults.get(trackId).get(resultType);
    }

    public void addTrack(String trackId) {
        mTrackResults.put(trackId, new TrackResults());
    }

    void save() {
        mIO.save(this);
    }
}
