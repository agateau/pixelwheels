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
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@RunWith(JUnit4.class)
public class GameStatsImplTests {
    @Mock private GameStatsImpl.IO mStatsIO;

    @Rule public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Test
    public void testInit() {
        Championship ch = new Championship("ch1", "champ1");
        ch.addTrack("t", "Track");
        final Track track = ch.getTracks().first();
        GameStats stats = new GameStatsImpl(mStatsIO);
        TrackStats trackStats = stats.getTrackStats(track);
        assertThat(trackStats, is(not(nullValue())));

        TrackStats trackStats2 = stats.getTrackStats(track);
        assertThat(trackStats, is(trackStats2));
    }

    @Test
    public void testOnChampionshipFinished() {
        Championship ch1 = new Championship("ch1", "champ1");
        Championship ch2 = new Championship("ch2", "champ2");
        GameStatsImpl stats = new GameStatsImpl(mStatsIO);
        stats.onChampionshipFinished(ch1, 4);
        verify(mStatsIO).save(stats);

        stats.onChampionshipFinished(ch1, 3);
        stats.onChampionshipFinished(ch2, 2);
        stats.onChampionshipFinished(ch2, 4);

        assertThat(stats.getBestChampionshipRank(ch1), is(3));
        assertThat(stats.getBestChampionshipRank(ch2), is(2));
    }

    @Test
    public void testRecordIntEvent() {
        // GIVEN empty game stats
        GameStatsImpl stats = new GameStatsImpl(mStatsIO);

        // WHEN a new int event is recorded
        stats.recordIntEvent(GameStats.Event.LEAVING_ROAD, 30);

        // THEN the stats are saved
        verify(mStatsIO).save(stats);
    }

    @Test
    public void testRecordIntEventTwice() {
        // GIVEN empty game stats
        GameStats stats = new GameStatsImpl(mStatsIO);

        // WHEN two new int events are recorded
        stats.recordIntEvent(GameStats.Event.LEAVING_ROAD, 30);
        stats.recordIntEvent(GameStats.Event.LEAVING_ROAD, 20);

        // THEN the counter has the right value
        assertThat(stats.getEventCount(GameStats.Event.LEAVING_ROAD), is(50));
    }

    @Test
    public void testEventCountDoesNotOverflow() {
        // GIVEN game stats with an event almost at MAX_VALUE
        GameStats stats = new GameStatsImpl(mStatsIO);
        stats.recordIntEvent(GameStats.Event.MISSILE_HIT, Integer.MAX_VALUE - 3);

        // WHEN a new int event is recorded, which would make the counter larger than MAX_VALUE
        stats.recordIntEvent(GameStats.Event.MISSILE_HIT, 10);

        // THEN the counter is MAX_VALUE
        assertThat(stats.getEventCount(GameStats.Event.MISSILE_HIT), is(Integer.MAX_VALUE));
    }
}
