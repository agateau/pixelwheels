package com.agateau.tinywheels;

import com.agateau.tinywheels.map.Track;
import com.agateau.tinywheels.vehicledef.VehicleDef;
import com.badlogic.gdx.utils.Array;

public class QuickRaceGameInfo extends GameInfo {
    private Track mTrack;

    public static class Builder extends GameInfo.Builder<QuickRaceGameInfo> {
        private Track mTrack;

        public Builder(Array<VehicleDef> vehicleDefs, GameInfoConfig gameInfoConfig) {
            super(vehicleDefs, gameInfoConfig);
        }

        public void setTrack(Track track) {
            mTrack = track;
            mGameInfoConfig.track = mTrack.getId();
            mGameInfoConfig.flush();
        }

        @Override
        public QuickRaceGameInfo build() {
            QuickRaceGameInfo gameInfo = new QuickRaceGameInfo();
            gameInfo.mTrack = mTrack;
            createEntrants(gameInfo);
            return gameInfo;
        }
    }

    @Override
    public Track getTrack() {
        return mTrack;
    }
}
