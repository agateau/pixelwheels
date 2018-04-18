package com.agateau.tinywheels;

import com.agateau.tinywheels.map.Track;

public class QuickRaceGameInfo extends GameInfo {
    private Track mTrack;

    public QuickRaceGameInfo(GameConfig config, GameInfoConfig gameInfoConfig) {
        super(config, gameInfoConfig);
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
