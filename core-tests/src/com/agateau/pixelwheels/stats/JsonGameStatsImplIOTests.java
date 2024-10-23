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
package com.agateau.pixelwheels.stats;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.agateau.pixelwheels.gamesetup.Difficulty;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JsonGameStatsImplIOTests {
    @Rule public TemporaryFolder mTemporaryFolder = new TemporaryFolder();

    @Test
    public void testNoRecords() {
        JsonGameStatsImplIO io = new JsonGameStatsImplIO(new FileHandle("/doesnotexist"));
        GameStatsImpl stats = new GameStatsImpl(io);
        for (HashMap<String, TrackStats> trackStats : stats.mTrackStatsByDifficulty.values()) {
            assertTrue(trackStats.isEmpty());
        }
    }

    @Test
    public void testIO() {
        Difficulty difficulty = Difficulty.EASY;

        // GIVEN 2 championships
        Championship ch1 = new Championship("ch1", "champ1");
        Championship ch2 = new Championship("ch2", "champ2");
        ch1.addTrack("t", "track");
        Track track = ch1.getTracks().first();

        // AND a JsonGameStatsImplIO instance working on a new json file
        FileHandle testFile = new FileHandle(mTemporaryFolder.getRoot() + "/io.json");
        assertFalse(testFile.exists());
        JsonGameStatsImplIO io = new JsonGameStatsImplIO(testFile);

        // AND an associated GameStats instance
        GameStats gameStats = new GameStatsImpl(io);

        // WHEN events are reported to the GameStatsImpl instance
        TrackStats stats = gameStats.getTrackStats(difficulty, track);
        addResult(stats, 12);
        addResult(stats, 14);
        addResult(stats, 10);
        gameStats.onChampionshipFinished(Difficulty.MEDIUM, ch1, 1);
        gameStats.onChampionshipFinished(Difficulty.EASY, ch2, 2);
        gameStats.recordEvent(GameStats.Event.MISSILE_HIT);
        gameStats.recordEvent(GameStats.Event.MISSILE_HIT);

        // THEN the json file has been created
        assertTrue(testFile.exists());

        // WHEN loading gamestats from the json file
        GameStatsImpl gameStats2 = new GameStatsImpl(io);

        // THEN it contains the reported events
        assertTrue(gameStats2.mTrackStatsByDifficulty.get(difficulty).containsKey("t"));
        assertThat(gameStats2.mTrackStatsByDifficulty.get(difficulty).size(), is(1));
        TrackStats stats2 = gameStats2.getTrackStats(difficulty, track);
        checkRecords(stats2, 0, 10);
        checkRecords(stats2, 1, 12);
        checkRecords(stats2, 2, 14);
        assertThat(gameStats2.getBestChampionshipRank(Difficulty.MEDIUM, ch1), is(1));
        assertThat(gameStats2.getBestChampionshipRank(Difficulty.EASY, ch2), is(2));
        assertThat(gameStats2.getEventCount(GameStats.Event.MISSILE_HIT), is(2));
    }

    @Test
    public void testDefaultRecordsAreNotSaved() {
        Difficulty difficulty = Difficulty.EASY;

        // GIVEN a championship
        Championship ch1 = new Championship("ch1", "champ1");
        // AND an associated track
        ch1.addTrack("t", "track");
        Track track = ch1.getTracks().first();

        // AND a JsonGameStatsImplIO instance working on a new json file
        FileHandle testFile = new FileHandle(mTemporaryFolder.getRoot() + "/io.json");
        assertFalse(testFile.exists());
        JsonGameStatsImplIO io = new JsonGameStatsImplIO(testFile);

        // AND an associated GameStats instance
        GameStats gameStats = new GameStatsImpl(io);

        // AND a default track record
        gameStats
                .getTrackStats(difficulty, track)
                .addResult(TrackStats.ResultType.LAP, TrackStats.DEFAULT_RECORD_VEHICLE, 12f);

        // WHEN a player stat is recorded
        TrackStats stats = gameStats.getTrackStats(difficulty, track);
        addResult(stats, 18);

        // THEN there are two lap stats
        assertEquals(stats.get(TrackStats.ResultType.LAP).size(), 2);

        // AND only the player stat is present in the json file
        assertTrue(testFile.exists());
        JsonParser parser = new JsonParser();

        // JSON structure: root / trackStats / $difficultyId / $trackName / lap / [results]
        JsonObject root = parser.parse(testFile.readString("UTF-8")).getAsJsonObject();
        JsonArray lapArray =
                root.getAsJsonObject("trackStats")
                        .getAsJsonObject(difficulty.name())
                        .getAsJsonObject(track.getId())
                        .getAsJsonArray("lap");
        assertEquals(lapArray.size(), 1);
    }

    private void checkRecords(TrackStats stats, int rank, float expectedLap) {
        float expectedTotal = expectedLap * 3;
        ArrayList<TrackResult> results = stats.get(TrackStats.ResultType.LAP);
        assertThat(results.get(rank).vehicle, is("bob"));
        assertThat(results.get(rank).value, is(expectedLap));

        results = stats.get(TrackStats.ResultType.TOTAL);
        assertThat(results.get(rank).vehicle, is("bob"));
        assertThat(results.get(rank).value, is(expectedTotal));
    }

    private void addResult(TrackStats stats, float value) {
        stats.addResult(TrackStats.ResultType.LAP, "bob", value);
        stats.addResult(TrackStats.ResultType.TOTAL, "bob", value * 3);
    }
}
