package com.greenyetilab.race;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;

/**
 * The map of the current game
 */
public class MapInfo implements Disposable {
    private final TiledMap mMap;

    public MapInfo(TiledMap map) {
        mMap = map;
    }

    public TiledMap getMap() {
        return mMap;
    }

    @Override
    public void dispose() {
        mMap.dispose();
    }
}
