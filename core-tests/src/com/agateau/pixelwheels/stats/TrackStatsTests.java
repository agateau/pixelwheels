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
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@RunWith(JUnit4.class)
public class TrackStatsTests {
    @Mock private GameStats mStats;

    @Rule public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Test
    public void testInit() {
        TrackStats trackStats = new TrackStats(mStats);

        ArrayList<TrackResult> records;

        records = trackStats.get(TrackStats.ResultType.LAP);
        assertThat(records.size(), is(0));
        records = trackStats.get(TrackStats.ResultType.TOTAL);
        assertThat(records.size(), is(0));
    }

    @Test
    public void testAddResultCausesSaving() {
        TrackStats trackStats = new TrackStats(mStats);
        int row = trackStats.addResult(TrackStats.ResultType.LAP, new TrackResult("bob", 12));
        assertThat(row, is(0));
        verify(mStats).save();
    }

    @Test
    public void testAddResults() {
        TrackStats trackStats = new TrackStats(mStats);

        checkAddResult(trackStats, 12, 0); // 12
        checkAddResult(trackStats, 14, 1); // 12, 14
        checkAddResult(trackStats, 10, 0); // 10, 12, 14
        checkAddResult(trackStats, 20, -1); // 10, 12, 14
    }

    private void checkAddResult(TrackStats trackStats, float value, int expectedRank) {
        clearInvocations(mStats);
        int rank = trackStats.addResult(TrackStats.ResultType.LAP, new TrackResult("bob", value));
        assertThat(rank, is(expectedRank));
        if (rank >= 0) {
            verify(mStats).save();
        } else {
            verifyZeroInteractions(mStats);
        }
    }
}
