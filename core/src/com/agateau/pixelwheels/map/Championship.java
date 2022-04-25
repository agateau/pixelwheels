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
package com.agateau.pixelwheels.map;

import static com.agateau.translations.Translator.trc;

import com.badlogic.gdx.utils.Array;

public class Championship {
    private final String mId;
    private final String mName;
    private final Array<Track> mTracks = new Array<>();

    public Championship(String id, String name) {
        mId = id;
        mName = name;
    }

    public Track addTrack(String trackId, String trackName) {
        Track track = new Track(this, trackId, trackName);
        mTracks.add(track);
        return track;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return trc(mName, "championship");
    }

    public Array<Track> getTracks() {
        return mTracks;
    }

    public String toString() {
        return "championship(" + mId + ")";
    }
}
