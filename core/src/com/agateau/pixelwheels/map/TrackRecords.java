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

import java.util.ArrayList;

public class TrackRecords {
    private static final int RECORD_COUNT = 3;
    private final TrackStats mStats;
    private final ArrayList<TrackResult> mResults = new ArrayList<TrackResult>(RECORD_COUNT);

    TrackRecords(TrackStats stats) {
        mStats = stats;
    }

    public ArrayList<TrackResult> getResults() {
        return mResults;
    }

    public int addResult(TrackResult result) {
        if (mResults.isEmpty()) {
            mResults.add(result);
            mStats.save();
            return 0;
        }
        return -1;
    }
}
