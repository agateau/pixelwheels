package com.agateau.tinywheels;

import com.agateau.tinywheels.map.Championship;

public class ChampionshipGameInfo extends GameInfo {
    public Championship championship;
    // TODO: make track private, and use different implementations of getTrack()
    private int mTrackIndex = 0;

    void restart() {
        mTrackIndex = 0;
        this.setTrack(this.championship.getTracks().get(mTrackIndex));
    }

    public boolean isLastTrack() {
        return mTrackIndex == this.championship.getTracks().size - 1;
    }

    public void selectNextTrack() {
        mTrackIndex++;
        this.setTrack(this.championship.getTracks().get(mTrackIndex));
    }
}
