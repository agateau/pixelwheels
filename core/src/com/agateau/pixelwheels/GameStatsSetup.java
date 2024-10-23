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
package com.agateau.pixelwheels;

import com.agateau.pixelwheels.gamesetup.Difficulty;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.stats.TrackStats;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;

/** Helper class to load default records in GameStats */
class GameStatsSetup {
    static void loadDefaultRecords(GameStats gameStats, Array<Championship> championships) {
        for (Championship championship : championships) {
            for (Track track : championship.getTracks()) {
                for (Difficulty difficulty : Difficulty.values()) {
                    TrackStats trackStats = gameStats.getTrackStats(difficulty, track);
                    // FIXME: get default records per difficulty
                    loadDefaultRecordsForTrack(trackStats, track);
                }
            }
        }
    }

    private static void loadDefaultRecordsForTrack(TrackStats trackStats, Track track) {
        for (TrackStats.ResultType resultType : TrackStats.ResultType.values()) {
            ArrayList<Float> defaultRecords = track.getDefaultTrackRecords(resultType);
            for (float record : defaultRecords) {
                trackStats.addResult(resultType, TrackStats.DEFAULT_RECORD_VEHICLE, record);
            }
        }
    }
}
