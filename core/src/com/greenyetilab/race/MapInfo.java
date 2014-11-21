package com.greenyetilab.race;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by aurelien on 21/11/14.
 */
public class MapInfo {
    private TiledMap mMap;
    private final String mFileName;
    private float mBestTime = 0;

    MapInfo(String filename) {
        mFileName = filename;
        Preferences prefs = Gdx.app.getPreferences("com.greenyetilab.race");
        mBestTime = prefs.getFloat("best/" + mFileName, 0);
    }

    public String getTitle() {
        String title = mFileName.replace(".tmx", "");
        String first = title.substring(0, 1);
        first = first.toUpperCase();
        title = first + title.substring(1);
        return title;
    }

    public TiledMap getMap() {
        if (mMap == null) {
            mMap = new TmxMapLoader().load(mFileName);
        }
        return mMap;
    }

    public float getBestTime() {
        return mBestTime;
    }

    public void setBestTime(float value) {
        mBestTime = value;
        Preferences prefs = Gdx.app.getPreferences("com.greenyetilab.race");
        prefs.putFloat("best/" + mFileName, mBestTime);
        prefs.flush();
    }
}
