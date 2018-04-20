package com.agateau.tinywheels;

import com.agateau.tinywheels.map.Track;
import com.agateau.tinywheels.vehicledef.VehicleDef;
import com.badlogic.gdx.utils.Array;

public class QuickRaceGameInfo extends GameInfo {
    private Track mTrack;

    public QuickRaceGameInfo(Array<VehicleDef> vehicleDefs, GameInfoConfig gameInfoConfig) {
        super(vehicleDefs, gameInfoConfig);
    }

    @Override
    public Track getTrack() {
        return mTrack;
    }

    public void setTrack(Track track) {
        mTrack = track;
        flush();
    }

    @Override
    protected void flush() {
        if (mTrack != null) {
            mGameInfoConfig.track = mTrack.getId();
        }
        super.flush();
    }
}
