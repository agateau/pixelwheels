package com.agateau.tinywheels;

import com.agateau.tinywheels.map.Track;

public class QuickRaceGameInfo extends GameInfo {
    private Track mTrack;

    @Override
    public Track getTrack() {
        return mTrack;
    }

    public void setTrack(Track track) {
        mTrack = track;
    }
}
