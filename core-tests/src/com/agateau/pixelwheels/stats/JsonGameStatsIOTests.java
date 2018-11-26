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

import com.badlogic.gdx.files.FileHandle;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class JsonGameStatsIOTests {
    @Rule
    public TemporaryFolder mTemporaryFolder = new TemporaryFolder();

    @Test
    public void testNoRecords() {
        JsonGameStatsIO io = new JsonGameStatsIO(new FileHandle("/doesnotexist"));
        GameStats stats = new GameStats(io);
        assertTrue(stats.mTrackStats.isEmpty());
    }

    @Test
    public void testIO() {
        FileHandle testFile = new FileHandle(mTemporaryFolder.getRoot() + "/io.json");
        assertTrue(!testFile.exists());

        JsonGameStatsIO io = new JsonGameStatsIO(testFile);
        GameStats gameStats = new GameStats(io);
        gameStats.addTrack("t");
        TrackStats stats = gameStats.getTrackStats("t");
        addResult(stats, 12);
        addResult(stats, 14);
        addResult(stats, 10);
        assertTrue(testFile.exists());

        GameStats gameStats2 = new GameStats(io);
        assertTrue(gameStats2.mTrackStats.containsKey("t"));
        assertThat(gameStats2.mTrackStats.size(), is(1));
        TrackStats stats2 = gameStats2.getTrackStats("t");
        checkRecords(stats2, 0, 10);
        checkRecords(stats2, 1, 12);
        checkRecords(stats2, 2, 14);
    }

    private void checkRecords(TrackStats stats, int rank, float expectedLap) {
        float expectedTotal = expectedLap * 3;
        ArrayList<TrackResult> results = stats.get(TrackStats.ResultType.LAP);
        assertThat(results.get(rank).racer, is("bob"));
        assertThat(results.get(rank).value, is(expectedLap));

        results = stats.get(TrackStats.ResultType.TOTAL);
        assertThat(results.get(rank).racer, is("bob"));
        assertThat(results.get(rank).value, is(expectedTotal));
    }

    private void addResult(TrackStats stats, float value) {
        TrackResult lapResult = new TrackResult("bob", value);
        TrackResult totalResult = new TrackResult("bob", value * 3);
        stats.addResult(TrackStats.ResultType.LAP, lapResult);
        stats.addResult(TrackStats.ResultType.TOTAL, totalResult);
    }
}
