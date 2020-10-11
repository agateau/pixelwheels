package com.agateau.pixelwheels.gamesetup;

import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.utils.Assert;
import com.badlogic.gdx.utils.Array;

public class ChampionshipGameInfo extends GameInfo {
    private Championship mChampionship;
    private int mTrackIndex = 0;

    public ChampionshipGameInfo() {
        super(GameType.CHAMPIONSHIP);
    }

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

    public boolean isFirstTrack() {
        return mTrackIndex == 0;
    }

    public boolean isLastTrack() {
        return mTrackIndex == mChampionship.getTracks().size - 1;
    }

    public void selectNextTrack() {
        Assert.check(!isLastTrack(), "Can't select past the last track");
        mTrackIndex++;
    }

    public Championship getChampionship() {
        return mChampionship;
    }

    @Override
    public Track getTrack() {
        return mChampionship.getTracks().get(mTrackIndex);
    }
}
