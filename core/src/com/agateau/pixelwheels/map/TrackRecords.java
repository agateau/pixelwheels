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
    private final GameStats mStats;
    private final ArrayList<TrackResult> mResults = new ArrayList<TrackResult>(RECORD_COUNT);

    TrackRecords(GameStats stats) {
        mStats = stats;
    }

    public ArrayList<TrackResult> getResults() {
        return mResults;
    }

    public int addResult(TrackResult result) {
        int rank = -1;
        // Insert result if it is better than an existing one
        for (int idx = 0; idx < mResults.size(); ++idx) {
            if (result.value < mResults.get(idx).value) {
                mResults.add(idx, result);
                if (mResults.size() > RECORD_COUNT) {
                    mResults.remove(RECORD_COUNT);
                }
                rank = idx;
                break;
            }
        }
        // If result is not better than existing ones but there is room at the end, append it
        if (rank == -1 && mResults.size() < RECORD_COUNT) {
            mResults.add(result);
            rank = mResults.size() - 1;
        }
        if (rank > -1) {
            mStats.save();
        }
        return rank;
    }
}
