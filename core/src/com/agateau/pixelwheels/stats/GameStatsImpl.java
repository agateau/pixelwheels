/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.stats;

import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.agateau.utils.CollectionUtils;
import java.util.HashMap;

public class GameStatsImpl implements GameStats {
    private transient IO mIO;
    private transient Listener mListener;
    final HashMap<String, TrackStats> mTrackStats = new HashMap<>();
    final HashMap<String, Integer> mBestChampionshipRank = new HashMap<>();
    final HashMap<String, Integer> mEvents = new HashMap<>();

    public interface IO {
        void load(GameStatsImpl gameStats);

        void save(GameStatsImpl gameStats);
    }

    public GameStatsImpl(IO io) {
        setIO(io);
        mIO.load(this);
    }

    public void setIO(IO io) {
        mIO = io;
    }

    @Override
    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public TrackStats getTrackStats(Track track) {
        TrackStats stats = mTrackStats.get(track.getId());
        if (stats == null) {
            stats = new TrackStats(this);
            mTrackStats.put(track.getId(), stats);
        }
        return stats;
    }

    @Override
    public int getBestChampionshipRank(Championship championship) {
        //noinspection ConstantConditions
        return CollectionUtils.getOrDefault(
                mBestChampionshipRank, championship.getId(), Integer.MAX_VALUE);
    }

    @Override
    public void onChampionshipFinished(Championship championship, int rank) {
        Integer currentBest = mBestChampionshipRank.get(championship.getId());
        if (currentBest == null || currentBest > rank) {
            mBestChampionshipRank.put(championship.getId(), rank);
            save();
        }
    }

    @Override
    public void recordEvent(Event event) {
        recordIntEvent(event, 1);
    }

    @Override
    public void recordIntEvent(Event event, int value) {
        String id = event.toString();
        Integer count = mEvents.get(id);
        if (count == null) {
            count = 0;
        }
        int newCount = count + value;
        if (newCount < count) {
            // Do not wrap around
            newCount = Integer.MAX_VALUE;
        }
        mEvents.put(id, newCount);
        save();
    }

    @Override
    public int getEventCount(Event event) {
        //noinspection ConstantConditions
        return CollectionUtils.getOrDefault(mEvents, event.toString(), 0);
    }

    public void save() {
        if (mListener != null) {
            mListener.onChanged();
        }
        mIO.save(this);
    }
}
