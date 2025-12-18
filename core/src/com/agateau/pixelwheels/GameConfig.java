/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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

import com.agateau.pixelwheels.gameinput.GameInputHandler;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactories;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactory;
import com.agateau.pixelwheels.gamesetup.Difficulty;
import com.agateau.pixelwheels.gamesetup.GameMode;
import com.agateau.utils.Assert;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import java.util.LinkedHashMap;
import java.util.Map;

/** The game configuration */
public class GameConfig {
    public static final float HUD_ZOOM_AUTO = 0;

    /**
     * Config keys are packed in groups. After modifying keys, one must call @ref GameConfig.flush()
     * with the appropriate group for the keys. This is used by ChangeListener to know whether they
     * should act or if they can ignore the change.
     */
    public enum ConfigGroup {
        INPUT,
        LANGUAGE,
        OTHER,
    }

    public interface ChangeListener {
        void onGameConfigChanged(ConfigGroup group);
    }

    public boolean fullscreen = false;
    public boolean headingUpCamera = false;

    public boolean playSoundFx = true;
    public boolean playMusic = true;
    public String languageId = "";
    public float hudZoom = HUD_ZOOM_AUTO;

    public GameMode gameMode = GameMode.QUICK_RACE;
    public final String[] vehicles = new String[Constants.MAX_PLAYERS];
    public String track;
    public String championship;
    public Difficulty difficulty = Difficulty.EASY;

    private final String[] mPlayerInputFactoryIds = new String[Constants.MAX_PLAYERS];
    private final GameInputHandler[] mPlayerInputHandlers =
            new GameInputHandler[Constants.MAX_PLAYERS];

    private final Preferences mPreferences;
    private final DelayedRemovalArray<ChangeListener> mListeners = new DelayedRemovalArray<>();

    GameConfig() {
        this(Gdx.app.getPreferences(Constants.CONFIG_FILENAME));
    }

    GameConfig(Preferences preferences) {
        mPreferences = preferences;
        load();
        addListener(
                group -> {
                    if (group == ConfigGroup.INPUT) {
                        setupInputHandlers();
                    }
                });
    }

    private void load() {
        fullscreen = mPreferences.getBoolean(PrefConstants.FULLSCREEN, false);
        headingUpCamera = mPreferences.getBoolean(PrefConstants.HEADING_UP_CAMERA, false);
        playSoundFx = mPreferences.getBoolean(PrefConstants.SOUND_FX, true);
        playMusic = mPreferences.getBoolean(PrefConstants.MUSIC, true);

        try {
            this.gameMode = GameMode.valueOf(mPreferences.getString(PrefConstants.GAME_MODE));
        } catch (IllegalArgumentException e) {
            // Nothing to do, fallback to default value
        }
        try {
            this.difficulty = Difficulty.valueOf(mPreferences.getString(PrefConstants.DIFFICULTY));
        } catch (IllegalArgumentException e) {
            // Nothing to do, fallback to default value
        }

        for (int idx = 0; idx < Constants.MAX_PLAYERS; ++idx) {
            mPlayerInputFactoryIds[idx] =
                    mPreferences.getString(
                            PrefConstants.INPUT_PREFIX + idx, PrefConstants.INPUT_DEFAULT);
            this.vehicles[idx] = mPreferences.getString(PrefConstants.VEHICLE_ID_PREFIX + idx);
        }

        this.track = mPreferences.getString(PrefConstants.TRACK_ID);
        this.championship = mPreferences.getString(PrefConstants.CHAMPIONSHIP_ID);

        this.languageId = mPreferences.getString(PrefConstants.LANGUAGE_ID);
        this.hudZoom = mPreferences.getFloat(PrefConstants.HUD_ZOOM, HUD_ZOOM_AUTO);

        setupInputHandlers();
    }

    public void addListener(ChangeListener listener) {
        mListeners.add(listener);
    }

    public void flush(ConfigGroup group) {
        switch (group) {
            case INPUT:
                for (int idx = 0; idx < this.vehicles.length; ++idx) {
                    mPreferences.putString(
                            PrefConstants.INPUT_PREFIX + idx, mPlayerInputFactoryIds[idx]);
                }
                break;
            case LANGUAGE:
                mPreferences.putString(PrefConstants.LANGUAGE_ID, this.languageId);
                break;
            case OTHER:
                mPreferences.putBoolean(PrefConstants.FULLSCREEN, fullscreen);
                mPreferences.putBoolean(PrefConstants.HEADING_UP_CAMERA, headingUpCamera);
                mPreferences.putBoolean(PrefConstants.SOUND_FX, playSoundFx);
                mPreferences.putBoolean(PrefConstants.MUSIC, playMusic);

                mPreferences.putString(PrefConstants.GAME_MODE, this.gameMode.toString());
                mPreferences.putString(PrefConstants.DIFFICULTY, this.difficulty.toString());
                for (int idx = 0; idx < this.vehicles.length; ++idx) {
                    mPreferences.putString(
                            PrefConstants.VEHICLE_ID_PREFIX + idx, this.vehicles[idx]);
                }

                mPreferences.putString(PrefConstants.TRACK_ID, this.track);
                mPreferences.putString(PrefConstants.CHAMPIONSHIP_ID, this.championship);
                mPreferences.putFloat(PrefConstants.HUD_ZOOM, this.hudZoom);
                break;
        }

        mPreferences.flush();

        mListeners.begin();
        for (ChangeListener listener : mListeners) {
            listener.onGameConfigChanged(group);
        }
        mListeners.end();
    }

    public GameInputHandler getPlayerInputHandler(int index) {
        Assert.check(
                index < mPlayerInputHandlers.length,
                "Not enough input handlers for index " + index);
        return mPlayerInputHandlers[index];
    }

    public GameInputHandlerFactory getPlayerInputHandlerFactory(int idx) {
        String factoryId = mPlayerInputFactoryIds[idx];
        return GameInputHandlerFactories.getFactoryById(factoryId);
    }

    public void setPlayerInputHandlerFactory(int idx, GameInputHandlerFactory factory) {
        mPlayerInputFactoryIds[idx] = factory.getId();
    }

    public void savePlayerInputHandlerConfig(int index) {
        Assert.check(
                index < mPlayerInputHandlers.length,
                "Not enough input handlers for index " + index);
        GameInputHandler handler = mPlayerInputHandlers[index];
        if (handler == null) {
            return;
        }
        String prefix = getInputPrefix(index);
        handler.saveConfig(mPreferences, prefix);
        mPreferences.flush();
    }

    private String getInputPrefix(int idx) {
        // Include the factory id to ensure there are no configuration clashes when switching
        // between input handlers
        return PrefConstants.INPUT_PREFIX + idx + "." + mPlayerInputFactoryIds[idx] + ".";
    }

    private static String simpleString(Object object) {
        String address = Integer.toHexString(object.hashCode());
        return object.getClass().getSimpleName() + '@' + address;
    }

    /**
     * Initialize mPlayerInputHandlers. It does so by getting a *copy* of the input handler pointers
     * and dispatching them to mPlayerInputHandlers.
     */
    private void setupInputHandlers() {
        NLog.i("");

        // Create a map mapping an input handler factory ID to a *copy* of its handler array.
        //
        // It's a copy so that code can take handlers from the array without affecting the original.
        //
        // The map is a LinkedHashMap so that iterating on the entries follows the order factories
        // are listed inside GameInputHandlerFactories, ensuring factories listed early are picked
        // first when selecting a default handler.
        LinkedHashMap<String, Array<GameInputHandler>> inputHandlersByIds = new LinkedHashMap<>();
        for (GameInputHandlerFactory factory : GameInputHandlerFactories.getAvailableFactories()) {
            inputHandlersByIds.put(factory.getId(), new Array<>(factory.getAllHandlers()));
        }

        for (int idx = 0; idx < Constants.MAX_PLAYERS; ++idx) {
            mPlayerInputHandlers[idx] = null;
            String id = mPlayerInputFactoryIds[idx];
            GameInputHandler inputHandler = null;
            if (!"".equals(id)) {
                inputHandler = popInputHandler(inputHandlersByIds.get(id));
                if (inputHandler != null) {
                    NLog.i(
                            "P%d: loading config for %s (%s)",
                            idx + 1, id, simpleString(inputHandler));
                    inputHandler.loadConfig(mPreferences, getInputPrefix(idx), idx);
                } else {
                    NLog.e("P%d: not enough input handlers for id '%s'", idx + 1, id);
                }
            }
            if (inputHandler == null) {
                // We haven't found an input handler for this player, fall back to the first
                // available handler.
                NLog.i(
                        "P%d: no predefined config, or predefined config not available. Looking for a fallback.",
                        idx + 1);
                for (Map.Entry<String, Array<GameInputHandler>> entry :
                        inputHandlersByIds.entrySet()) {
                    inputHandler = popInputHandler(entry.getValue());
                    if (inputHandler != null) {
                        id = entry.getKey();
                        NLog.i("P%d: using %s (%s)", idx + 1, id, simpleString(inputHandler));
                        break;
                    }
                }
            }
            Assert.check(
                    inputHandler != null,
                    "Player %d: No input handler available for id '%s'",
                    idx + 1,
                    id);
            mPlayerInputHandlers[idx] = inputHandler;
        }
    }

    private static GameInputHandler popInputHandler(Array<GameInputHandler> inputHandlers) {
        if (inputHandlers != null && !inputHandlers.isEmpty()) {
            return inputHandlers.removeIndex(0);
        }
        return null;
    }
}
