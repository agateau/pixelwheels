package com.agateau.tinywheels;

import com.agateau.tinywheels.map.Championship;
import com.agateau.tinywheels.map.Track;
import com.agateau.utils.Assert;

public class ChampionshipGameInfo extends GameInfo {
    public Championship championship;
    private int mTrackIndex = 0;

    void restart() {
        mTrackIndex = 0;
    }

    public boolean isLastTrack() {
        return mTrackIndex == this.championship.getTracks().size - 1;
    }

    public void selectNextTrack() {
        Assert.check(!isLastTrack(), "Can't select past the last track");
        mTrackIndex++;
    }

    @Override
    public Track getTrack() {
        return this.championship.getTracks().get(mTrackIndex);
    }
}
