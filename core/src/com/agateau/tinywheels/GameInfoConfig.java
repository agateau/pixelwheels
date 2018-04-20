package com.agateau.tinywheels;

import com.badlogic.gdx.Preferences;

public class GameInfoConfig {
    private final Preferences mPreferences;
    private final String mPrefix;

    public final String[] vehicles = new String[2];
    public String track;
    public String championship;

    public GameInfoConfig(Preferences preferences, String prefix) {
        mPreferences = preferences;
        mPrefix = prefix;
    }

    public void load() {
        for (int idx = 0; idx < this.vehicles.length; ++idx) {
            this.vehicles[idx] = mPreferences.getString(mPrefix + PrefConstants.VEHICLE_ID_PREFIX + String.valueOf(idx));
        }

        this.track = mPreferences.getString(mPrefix + PrefConstants.TRACK_ID);
        this.championship = mPreferences.getString(mPrefix + PrefConstants.CHAMPIONSHIP_ID);
    }

    public void flush() {
        for (int idx = 0; idx < this.vehicles.length; ++idx) {
            mPreferences.putString(mPrefix + PrefConstants.VEHICLE_ID_PREFIX + String.valueOf(idx),
                    this.vehicles[idx]);
        }

        mPreferences.putString(mPrefix + PrefConstants.TRACK_ID, this.track);
        mPreferences.putString(mPrefix + PrefConstants.CHAMPIONSHIP_ID, this.championship);
        mPreferences.flush();
    }
}
