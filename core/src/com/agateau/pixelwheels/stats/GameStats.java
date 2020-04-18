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

public interface GameStats {
    enum Event {
        MISSILE_HIT,
        LEAVING_ROAD,
        PICKED_BONUS
    }

    interface Listener {
        void onChanged();
    }

    void setListener(Listener listener);

    TrackStats getTrackStats(Track track);

    /** Returns the best rank obtained, or Integer.MAX_VALUE if never raced */
    int getBestChampionshipRank(Championship championship);

    void onChampionshipFinished(Championship championship, int rank);

    void recordEvent(Event event);

    void recordIntEvent(Event event, int value);

    int getEventCount(Event event);

    void save();
}
