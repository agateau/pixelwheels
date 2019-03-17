/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.rewards;

import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.stats.GameStats;
import com.badlogic.gdx.utils.Array;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class RewardManagerTests {
    @Mock
    private GameStats.IO mStatsIO;

    @Rule
    public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Test
    public void testIsChampionshipUnlocked() {
        GameStats gameStats = new GameStats(mStatsIO);
        Array<Championship> championships = createChampionships();
        RewardManager manager = new RewardManager(gameStats, championships);
        manager.addRule(Reward.Category.CHAMPIONSHIP, championships.get(1).getId(), new RewardRule() {
            @Override
            public boolean hasBeenEarned(GameStats gameStats) {
                return false;
            }
        });
        manager.applyRules();
        assertThat(manager.isChampionshipUnlocked(championships.get(0)), is(true));
        assertThat(manager.isChampionshipUnlocked(championships.get(1)), is(false));
    }

    @Test
    public void testIsTrackUnlocked() {
        GameStats gameStats = new GameStats(mStatsIO);
        Array<Championship> championships = createChampionships();
        RewardManager manager = new RewardManager(gameStats, championships);
        manager.addRule(Reward.Category.CHAMPIONSHIP, championships.get(1).getId(), new RewardRule() {
            @Override
            public boolean hasBeenEarned(GameStats gameStats) {
                return false;
            }
        });
        manager.applyRules();
        assertThat(manager.isTrackUnlocked(championships.get(0).getTracks().get(0)), is(true));
        assertThat(manager.isTrackUnlocked(championships.get(1).getTracks().get(0)), is(false));
    }

    private Array<Championship> createChampionships() {
        Array<Championship> championships = new Array<Championship>();
        for (int c = 0; c < 2; ++c) {
            String id = "c" + String.valueOf(c);
            String name = "Champ" + String.valueOf(c);
            Championship championship = new Championship(id, name);
            championships.add(championship);

            for (int t = 0; t < 3; ++t) {
                String trackId = "t" + String.valueOf(t);
                String trackName = "Track" + String.valueOf(t);
                Track track = new Track(trackId, trackName);
                championship.addTrack(track);
            }
        }
        return championships;
    }
}
