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
package com.agateau.tinywheels;

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

    static public class GameModeConfig {
        public String map;
    }

    public boolean fullscreen = false;
    public boolean rotateCamera = true;
    public boolean audio = true;
    public String input;
    public String onePlayerVehicle;
    public String[] multiPlayerVehicles = new String[2];

    public GameModeConfig onePlayer = new GameModeConfig();
    public GameModeConfig multiPlayer = new GameModeConfig();

    private Preferences mPreferences;
    private ArrayList<WeakReference<ChangeListener>> mListeners = new ArrayList<WeakReference<ChangeListener>>();

    GameConfig() {
        mPreferences = Gdx.app.getPreferences("com.agateau.tinywheels");
        rotateCamera = mPreferences.getBoolean(PrefConstants.ROTATE_SCREEN_ID, true);
        fullscreen = mPreferences.getBoolean(PrefConstants.FULLSCREEN, false);
        audio = mPreferences.getBoolean(PrefConstants.AUDIO, true);

        input = mPreferences.getString(PrefConstants.INPUT, PrefConstants.INPUT_DEFAULT);
        onePlayerVehicle = mPreferences.getString(PrefConstants.ONEPLAYER_VEHICLE_ID);
        for (int idx = 0; idx < multiPlayerVehicles.length; ++idx) {
            multiPlayerVehicles[idx] = mPreferences.getString(PrefConstants.MULTIPLAYER_VEHICLE_ID_PREFIX + String.valueOf(idx));
        }

        onePlayer.map = mPreferences.getString(PrefConstants.ONEPLAYER_MAP_ID);

        multiPlayer.map = mPreferences.getString(PrefConstants.MULTIPLAYER_MAP_ID);
    }

    public void addListener(ChangeListener listener) {
        mListeners.add(new WeakReference<ChangeListener>(listener));
    }

    public void flush() {
        mPreferences.putBoolean(PrefConstants.ROTATE_SCREEN_ID, rotateCamera);
        mPreferences.putBoolean(PrefConstants.FULLSCREEN, fullscreen);
        mPreferences.putBoolean(PrefConstants.AUDIO, audio);
        mPreferences.putString(PrefConstants.INPUT, input);
        mPreferences.putString(PrefConstants.ONEPLAYER_VEHICLE_ID, onePlayerVehicle);
        for (int idx = 0; idx < multiPlayerVehicles.length; ++idx) {
            mPreferences.putString(PrefConstants.MULTIPLAYER_VEHICLE_ID_PREFIX + String.valueOf(idx), multiPlayerVehicles[idx]);
        }

        mPreferences.putString(PrefConstants.ONEPLAYER_MAP_ID, onePlayer.map);
        mPreferences.putString(PrefConstants.MULTIPLAYER_MAP_ID, multiPlayer.map);

        mPreferences.flush();
        for (WeakReference<ChangeListener> listenerRef : mListeners) {
            ChangeListener listener = listenerRef.get();
            if (listener != null) {
                listener.change();
            }
        }
    }
}
