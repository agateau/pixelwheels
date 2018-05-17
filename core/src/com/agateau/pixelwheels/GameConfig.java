/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels;

import com.agateau.pixelwheels.gamesetup.GameMode;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * The game configuration
 */
public class GameConfig {
    interface ChangeListener {
        void change();
    }

    public boolean fullscreen = false;
    public boolean rotateCamera = true;
    public boolean audio = true;
    public String[] inputs = new String[Constants.MAX_PLAYERS];

    public GameMode gameMode = GameMode.QUICK_RACE;
    public final String[] vehicles = new String[Constants.MAX_PLAYERS];
    public String track;
    public String championship;

    private final Preferences mPreferences;
    private ArrayList<WeakReference<ChangeListener>> mListeners = new ArrayList<WeakReference<ChangeListener>>();

    public GameConfig() {
        mPreferences = Gdx.app.getPreferences("pixelwheels.conf");

        load();
    }

    private void load() {
        rotateCamera = mPreferences.getBoolean(PrefConstants.ROTATE_SCREEN, true);
        fullscreen = mPreferences.getBoolean(PrefConstants.FULLSCREEN, false);
        audio = mPreferences.getBoolean(PrefConstants.AUDIO, true);

        try {
            this.gameMode = GameMode.valueOf(mPreferences.getString(PrefConstants.GAME_MODE));
        } catch (IllegalArgumentException e) {
            // Nothing to do, fallback to default value
        }

        for (int idx = 0; idx < Constants.MAX_PLAYERS; ++idx) {
            this.inputs[idx] = mPreferences.getString(PrefConstants.INPUT_PREFIX + String.valueOf(idx), PrefConstants.INPUT_DEFAULT);
            this.vehicles[idx] = mPreferences.getString(PrefConstants.VEHICLE_ID_PREFIX + String.valueOf(idx));
        }

        this.track = mPreferences.getString(PrefConstants.TRACK_ID);
        this.championship = mPreferences.getString(PrefConstants.CHAMPIONSHIP_ID);
    }

    public void addListener(ChangeListener listener) {
        mListeners.add(new WeakReference<ChangeListener>(listener));
    }

    public void flush() {
        mPreferences.putBoolean(PrefConstants.ROTATE_SCREEN, rotateCamera);
        mPreferences.putBoolean(PrefConstants.FULLSCREEN, fullscreen);
        mPreferences.putBoolean(PrefConstants.AUDIO, audio);

        mPreferences.putString(PrefConstants.GAME_MODE, this.gameMode.toString());
        for (int idx = 0; idx < this.vehicles.length; ++idx) {
            mPreferences.putString(PrefConstants.VEHICLE_ID_PREFIX + String.valueOf(idx),
                    this.vehicles[idx]);
            mPreferences.putString(PrefConstants.INPUT_PREFIX + String.valueOf(idx),
                    this.inputs[idx]);
        }

        mPreferences.putString(PrefConstants.TRACK_ID, this.track);
        mPreferences.putString(PrefConstants.CHAMPIONSHIP_ID, this.championship);

        mPreferences.flush();

        for (WeakReference<ChangeListener> listenerRef : mListeners) {
            ChangeListener listener = listenerRef.get();
            if (listener != null) {
                listener.change();
            }
        }
    }

    public Preferences getPreferences() {
        return mPreferences;
    }

    public String getInputPrefix(int idx) {
        // Include inputs[idx] to ensure there are no configuration clashes when switching
        // between input handlers
        return PrefConstants.INPUT_PREFIX + String.valueOf(idx) + "." + inputs[idx] + ".";
    }
}
