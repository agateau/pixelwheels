package com.greenyetilab.race;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by aurelien on 21/11/14.
 */
public class MapInfo {
    private TiledMap mMap;

    private final String mFileName;

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
}
