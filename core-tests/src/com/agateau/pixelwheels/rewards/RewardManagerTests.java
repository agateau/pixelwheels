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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.stats.GameStatsImpl;
import com.agateau.utils.CollectionUtils;
import com.badlogic.gdx.utils.Array;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@RunWith(JUnit4.class)
public class RewardManagerTests {
    @Mock private GameStatsImpl.IO mStatsIO;

    @Rule public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Test
    public void testIsChampionshipUnlocked() {
        GameStats gameStats = new GameStatsImpl(mStatsIO);
        Array<Championship> championships = createChampionships();
        RewardManager manager = new RewardManager(gameStats);
        final Championship championship1 = championships.get(0);
        final Championship championship2 = championships.get(1);
        manager.addRule(Reward.get(championship1), RewardManager.ALWAYS_UNLOCKED);
        manager.addRule(
                Reward.get(championship2),
                new RewardRule() {
                    @Override
                    public boolean hasBeenUnlocked(GameStats gameStats) {
                        return false;
                    }

                    @Override
                    public String getUnlockText(GameStats gameStats) {
                        return "";
                    }
                });
        assertThat(manager.isChampionshipUnlocked(championship1), is(true));
        assertThat(manager.isChampionshipUnlocked(championship2), is(false));
    }

    @Test
    public void testIsTrackUnlocked() {
        GameStats gameStats = new GameStatsImpl(mStatsIO);
        Array<Championship> championships = createChampionships();
        RewardManager manager = new RewardManager(gameStats);
        final Championship championship1 = championships.get(0);
        final Championship championship2 = championships.get(1);
        manager.addRule(Reward.get(championship1), RewardManager.ALWAYS_UNLOCKED);
        manager.addRule(
                Reward.get(championship2),
                new RewardRule() {
                    @Override
                    public boolean hasBeenUnlocked(GameStats gameStats) {
                        return false;
                    }

                    @Override
                    public String getUnlockText(GameStats gameStats) {
                        return "";
                    }
                });
        assertThat(manager.isTrackUnlocked(championship1.getTracks().get(0)), is(true));
        assertThat(manager.isTrackUnlocked(championship2.getTracks().get(0)), is(false));
    }

    @Test
    public void testGetUnlockedRewards() {
        // GIVEN a RewardManager with 2 championships, ch2 is locked
        GameStats gameStats = new GameStatsImpl(mStatsIO);
        Array<Championship> championships = createChampionships();
        RewardManager manager = new RewardManager(gameStats);
        final Championship ch1 = championships.get(0);
        final Championship ch2 = championships.get(1);
        manager.addRule(Reward.get(ch1), RewardManager.ALWAYS_UNLOCKED);
        manager.addRule(
                Reward.get(ch2),
                new RewardRule() {
                    @Override
                    public boolean hasBeenUnlocked(GameStats gameStats) {
                        return gameStats.getBestChampionshipRank(ch1) <= 2;
                    }

                    @Override
                    public String getUnlockText(GameStats gameStats) {
                        return "";
                    }
                });

        manager.markAllUnlockedRewardsSeen();

        // THEN unlocked rewards contains only ch1
        Reward ch1Reward = Reward.get(ch1);
        Reward ch2Reward = Reward.get(ch2);
        assertThat(manager.getUnlockedRewards(), is(CollectionUtils.newSet(ch1Reward)));

        // AND new unlocked rewards is empty
        assertThat(manager.getUnseenUnlockedRewards().isEmpty(), is(true));

        // WHEN I unlock ch2
        gameStats.onChampionshipFinished(ch1, 2);

        // THEN unlocked rewards contains ch1 and ch2
        assertThat(manager.getUnlockedRewards(), is(CollectionUtils.newSet(ch1Reward, ch2Reward)));

        // AND new unlocked rewards contains ch2
        Set<Reward> unseenUnlockedRewards = manager.getUnseenUnlockedRewards();
        assertThat(unseenUnlockedRewards, is(CollectionUtils.newSet(ch2Reward)));

        // WHEN I mark new unlocked rewards as old
        manager.markAllUnlockedRewardsSeen();

        // THEN new unlocked rewards is empty
        assertThat(manager.getUnseenUnlockedRewards().isEmpty(), is(true));

        // AND previously retrieved unseen unlocked rewards set still contains ch2
        assertThat(unseenUnlockedRewards, is(CollectionUtils.newSet(ch2Reward)));
    }

    private static Array<Championship> createChampionships() {
        Array<Championship> championships = new Array<>();
        for (int c = 0; c < 2; ++c) {
            String id = "c" + (c + 1);
            String name = "Champ" + (c + 1);
            Championship championship = new Championship(id, name);
            championships.add(championship);

            for (int t = 0; t < 3; ++t) {
                String trackId = "t" + (t + 1);
                String trackName = "Track" + (t + 1);
                championship.addTrack(trackId, trackName);
            }
        }
        return championships;
    }
}
