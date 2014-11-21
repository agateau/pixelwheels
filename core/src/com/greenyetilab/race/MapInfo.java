package com.greenyetilab.race;

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
    }

    public String getTitle() {
        return mFileName;
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
    }
}
