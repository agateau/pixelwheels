package com.agateau.tinywheels;

import com.agateau.tinywheels.map.Championship;
import com.agateau.tinywheels.map.Track;
import com.agateau.tinywheels.vehicledef.VehicleDef;
import com.agateau.utils.Assert;
import com.badlogic.gdx.utils.Array;

public class ChampionshipGameInfo extends GameInfo {
    private Championship mChampionship;
    private int mTrackIndex = 0;

    public ChampionshipGameInfo(Array<VehicleDef> vehicleDefs, GameInfoConfig gameInfoConfig) {
        super(vehicleDefs, gameInfoConfig);
    }

    void restart() {
        mTrackIndex = 0;
    }

    public boolean isLastTrack() {
        return mTrackIndex == mChampionship.getTracks().size - 1;
    }

    public void selectNextTrack() {
        Assert.check(!isLastTrack(), "Can't select past the last track");
        mTrackIndex++;
    }

    @Override
    public Track getTrack() {
        return mChampionship.getTracks().get(mTrackIndex);
    }

    public Championship getChampionship() {
        return mChampionship;
    }

    public void setChampionship(Championship championship) {
        mChampionship = championship;
        flush();
    }

    @Override
    protected void flush() {
        if (mChampionship != null) {
            mGameInfoConfig.championship = mChampionship.getId();
        }
        super.flush();
    }
}
