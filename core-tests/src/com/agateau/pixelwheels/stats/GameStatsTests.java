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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class GameStatsTests {
    @Mock
    private GameStats.IO mStatsIO;

    @Rule
    public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Test
    public void testInit() {
        final String trackId = "t";
        GameStats stats = new GameStats(mStatsIO);
        TrackStats trackStats = stats.getTrackStats(trackId);
        assertThat(trackStats, is(not(nullValue())));

        TrackStats trackStats2 = stats.getTrackStats(trackId);
        assertThat(trackStats, is(trackStats2));
    }

    @Test
    public void testOnChampionshipFinished() {
        final String championshipId1 = "c1";
        final String championshipId2 = "c2";
        GameStats stats = new GameStats(mStatsIO);
        stats.onChampionshipFinished(championshipId1, 4);
        stats.onChampionshipFinished(championshipId1, 3);
        stats.onChampionshipFinished(championshipId2, 2);
        stats.onChampionshipFinished(championshipId2, 4);

        assertThat(stats.mBestChampionshipRank.get(championshipId1), is(3));
        assertThat(stats.mBestChampionshipRank.get(championshipId2), is(2));
    }
}
