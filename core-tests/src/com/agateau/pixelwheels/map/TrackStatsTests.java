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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class TrackStatsTests {
    @Mock
    private TrackStats.IO mStatsIO;

    @Rule
    public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Test
    public void testInit() {
        final String trackId = "t";
        TrackStats stats = new TrackStats(mStatsIO);
        stats.addTrack(trackId);

        TrackRecords records;

        records = stats.getRecords(trackId, TrackStats.ResultType.LAP);
        assertThat(records.getResults().size(), is(0));
        records = stats.getRecords(trackId, TrackStats.ResultType.TOTAL);
        assertThat(records.getResults().size(), is(0));
    }

    @Test
    public void testAddResultCausesSaving() {
        final String trackId = "t";
        TrackStats stats = new TrackStats(mStatsIO);
        stats.addTrack(trackId);

        int row = stats.getRecords(trackId, TrackStats.ResultType.LAP).addResult(new TrackResult("bob", 12));
        assertThat(row, is(0));
        verify(mStatsIO).save(stats);
    }
}
