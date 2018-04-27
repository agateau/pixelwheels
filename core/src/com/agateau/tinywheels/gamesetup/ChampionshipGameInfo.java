package com.agateau.tinywheels.gamesetup;

import com.agateau.tinywheels.GameConfig;
import com.agateau.tinywheels.map.Championship;
import com.agateau.tinywheels.map.Track;
import com.agateau.tinywheels.vehicledef.VehicleDef;
import com.agateau.utils.Assert;
import com.badlogic.gdx.utils.Array;

public class ChampionshipGameInfo extends GameInfo {
    private Championship mChampionship;
    private int mTrackIndex = 0;

    public static class Builder extends GameInfo.Builder<ChampionshipGameInfo> {
        private Championship mChampionship;

        public Builder(Array<VehicleDef> vehicleDefs, GameConfig gameConfig) {
            super(vehicleDefs, gameConfig);
        }

        public void setChampionship(Championship championship) {
            mChampionship = championship;
            mGameConfig.championship = mChampionship.getId();
            mGameConfig.flush();
        }

        @Override
        public ChampionshipGameInfo build() {
            ChampionshipGameInfo gameInfo = new ChampionshipGameInfo();
            gameInfo.mChampionship = mChampionship;
            createEntrants(gameInfo);
            return gameInfo;
        }
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
}
