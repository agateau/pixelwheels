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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
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
        assertTrue(stats.mTrackStats.isEmpty());
    }

    @Test
    public void testIO() {
        Championship ch1 = new Championship("ch1", "champ1");
        Championship ch2 = new Championship("ch2", "champ2");
        ch1.addTrack("t", "track");
        Track track = ch1.getTracks().first();
        FileHandle testFile = new FileHandle(mTemporaryFolder.getRoot() + "/io.json");
        assertTrue(!testFile.exists());

        JsonGameStatsImplIO io = new JsonGameStatsImplIO(testFile);
        GameStats gameStats = new GameStatsImpl(io);
        TrackStats stats = gameStats.getTrackStats(track);
        addResult(stats, 12);
        addResult(stats, 14);
        addResult(stats, 10);
        gameStats.onChampionshipFinished(ch1, 1);
        gameStats.onChampionshipFinished(ch2, 2);
        gameStats.recordEvent(GameStats.Event.MISSILE_HIT);
        gameStats.recordEvent(GameStats.Event.MISSILE_HIT);
        assertTrue(testFile.exists());

        GameStatsImpl gameStats2 = new GameStatsImpl(io);
        assertTrue(gameStats2.mTrackStats.containsKey("t"));
        assertThat(gameStats2.mTrackStats.size(), is(1));
        TrackStats stats2 = gameStats2.getTrackStats(track);
        checkRecords(stats2, 0, 10);
        checkRecords(stats2, 1, 12);
        checkRecords(stats2, 2, 14);
        assertThat(gameStats2.getBestChampionshipRank(ch1), is(1));
        assertThat(gameStats2.getBestChampionshipRank(ch2), is(2));
        assertThat(gameStats2.getEventCount(GameStats.Event.MISSILE_HIT), is(2));
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
        TrackResult lapResult = new TrackResult("bob", value);
        TrackResult totalResult = new TrackResult("bob", value * 3);
        stats.addResult(TrackStats.ResultType.LAP, lapResult);
        stats.addResult(TrackStats.ResultType.TOTAL, totalResult);
    }
}
