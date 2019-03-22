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

public interface GameStats {
    public enum Event {
        MISSILE_HIT
    }

    public interface Listener {
        void onChanged();
    }

    void setListener(Listener listener);

    TrackStats addTrack(String trackId);

    TrackStats getTrackStats(String trackId);

    int getBestChampionshipRank(Championship championship);

    void onChampionshipFinished(Championship championship, int rank);

    void recordEvent(Event event);

    int getEventCount(Event event);

    void save();
}
