package com.greenyetilab.tinywheels;

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

    public boolean rotateCamera = true;
    public String input;
    public String onePlayerVehicle;
    public String twoPlayersVehicle1;
    public String twoPlayersVehicle2;

    public GameModeConfig onePlayer = new GameModeConfig();
    public GameModeConfig multiPlayer = new GameModeConfig();

    private Preferences mPreferences;
    private ArrayList<WeakReference<ChangeListener>> mListeners = new ArrayList<WeakReference<ChangeListener>>();

    GameConfig() {
        mPreferences = Gdx.app.getPreferences("com.greenyetilab.tinywheels");
        rotateCamera = mPreferences.getBoolean(PrefConstants.ROTATE_SCREEN_ID, true);

        input = mPreferences.getString(PrefConstants.INPUT, PrefConstants.INPUT_DEFAULT);
        onePlayerVehicle = mPreferences.getString(PrefConstants.ONEPLAYER_VEHICLE_ID);
        twoPlayersVehicle1 = mPreferences.getString(PrefConstants.MULTIPLAYER_VEHICLE_ID1);
        twoPlayersVehicle2 = mPreferences.getString(PrefConstants.MULTIPLAYER_VEHICLE_ID2);

        onePlayer.map = mPreferences.getString(PrefConstants.ONEPLAYER_MAP_ID);

        multiPlayer.map = mPreferences.getString(PrefConstants.MULTIPLAYER_MAP_ID);
    }

    public void addListener(ChangeListener listener) {
        mListeners.add(new WeakReference<ChangeListener>(listener));
    }

    public void flush() {
        mPreferences.putBoolean(PrefConstants.ROTATE_SCREEN_ID, rotateCamera);
        mPreferences.putString(PrefConstants.INPUT, input);
        mPreferences.putString(PrefConstants.ONEPLAYER_VEHICLE_ID, onePlayerVehicle);
        mPreferences.putString(PrefConstants.MULTIPLAYER_VEHICLE_ID1, twoPlayersVehicle1);
        mPreferences.putString(PrefConstants.MULTIPLAYER_VEHICLE_ID2, twoPlayersVehicle2);

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
