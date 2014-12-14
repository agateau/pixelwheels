package com.greenyetilab.race;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

/**
 * The map of the current game
 */
public class MapInfo implements Disposable {
    private final TiledMap mMap;
    private final float[] mMaxSpeedForTileId;
    private final TiledMapTileLayer mGroundLayer;
    private final float mTileWidth;
    private final float mTileHeight;

    public MapInfo(TiledMap map) {
        mMap = map;
        mMaxSpeedForTileId = computeMaxSpeedForTileId();

        mGroundLayer = (TiledMapTileLayer) mMap.getLayers().get(0);
        mTileWidth = Constants.UNIT_FOR_PIXEL * mGroundLayer.getTileWidth();
        mTileHeight = Constants.UNIT_FOR_PIXEL * mGroundLayer.getTileHeight();
    }

    public TiledMap getMap() {
        return mMap;
    }

    public float getTileWidth() {
        return mTileWidth;
    }

    public float getTileHeight() {
        return mTileHeight;
    }

    private float[] computeMaxSpeedForTileId() {
        TiledMapTileSet tileSet = mMap.getTileSets().getTileSet(0);
        int maxId = 0;
        for (TiledMapTile tile : tileSet) {
            maxId = Math.max(maxId, tile.getId());
        }
        float[] array = new float[maxId + 1];
        for (int id = 0; id < array.length; ++id) {
            TiledMapTile tile = tileSet.getTile(id);
            array[id] = tile == null ? 1f : MapUtils.getFloatProperty(tile.getProperties(), "max_speed", 1f);
        }
        return array;
    }

    public TiledMapTile getTileAt(Vector2 pos) {
        return  getTileAt(pos.x, pos.y);
    }

    public TiledMapTile getTileAt(float x, float y) {
        int tx = MathUtils.floor(x / mTileWidth);
        int ty = MathUtils.floor(y / mTileHeight);
        TiledMapTileLayer.Cell cell = mGroundLayer.getCell(tx, ty);
        return cell == null ? null : cell.getTile();
    }

    public float getMaxSpeedAt(Vector2 pos) {
        return getMaxSpeedAt(pos.x, pos.y);
    }

    public float getMaxSpeedAt(float x, float y) {
        TiledMapTile tile = getTileAt(x, y);
        if (tile == null) {
            return 1.0f;
        }
        return mMaxSpeedForTileId[tile.getId()];
    }

    @Override
    public void dispose() {
        mMap.dispose();
    }
}
