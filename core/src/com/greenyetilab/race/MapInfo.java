package com.greenyetilab.race;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.greenyetilab.utils.Assert;

/**
 * The map of the current game
 */
public class MapInfo implements Disposable {
    private final TiledMap mMap;
    private final float[] mMaxSpeedForTileId;
    private final IntArray mRoadTileIds = new IntArray();
    private int mStartTileId = -1;
    private final TiledMapTileLayer mGroundLayer;
    private final MapLayer mDirectionsLayer;
    private final TiledMapTileLayer mWallsLayer;
    private final MapLayer mBordersLayer;
    private final float mTileWidth;
    private final float mTileHeight;

    public MapInfo(TiledMap map) {
        mMap = map;
        mMaxSpeedForTileId = computeMaxSpeedForTileId();
        findSpecialTileIds();

        mGroundLayer = (TiledMapTileLayer)mMap.getLayers().get(0);
        mDirectionsLayer = mMap.getLayers().get("Directions");
        assert mDirectionsLayer != null;
        mWallsLayer = (TiledMapTileLayer)mMap.getLayers().get("Walls");
        mBordersLayer = mMap.getLayers().get("Borders");
        assert mBordersLayer != null;

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

    public float getMapWidth() {
        return mTileWidth * mGroundLayer.getWidth();
    }

    public float getMapHeight() {
        return mTileHeight * mGroundLayer.getHeight();
    }

    public int getMapTWidth() {
        return mGroundLayer.getWidth();
    }

    public int getMapTHeight() {
        return  mGroundLayer.getHeight();
    }

    public TiledMapTileLayer getGroundLayer() {
        return mGroundLayer;
    }

    public MapLayer getDirectionsLayer() {
        return mDirectionsLayer;
    }

    public TiledMapTileLayer getWallsLayer() {
        return mWallsLayer;
    }

    public MapLayer getBordersLayer() {
        return mBordersLayer;
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

    private void findSpecialTileIds() {
        TiledMapTileSet tileSet = mMap.getTileSets().getTileSet(0);
        for (TiledMapTile tile : tileSet) {
            MapProperties properties = tile.getProperties();
            if (MapUtils.getBooleanProperty(properties, "is_road", false)) {
                mRoadTileIds.add(tile.getId());
            }
            if (MapUtils.getBooleanProperty(properties, "start", false)) {
                mStartTileId = tile.getId();
            }
        }
        Assert.check(mRoadTileIds.size > 0, "No road tile ids");
        Assert.check(mStartTileId != -1, "No start id");
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

    public float getDirectionAt(float x, float y) {
        x /= Constants.UNIT_FOR_PIXEL;
        y /= Constants.UNIT_FOR_PIXEL;
        for (MapObject object : mDirectionsLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                if (((RectangleMapObject)object).getRectangle().contains(x, y)) {
                    return MapUtils.getFloatProperty(object.getProperties(), "direction", 90);
                }
            } else if (object instanceof PolygonMapObject) {
                if (((PolygonMapObject)object).getPolygon().contains(x, y)) {
                    return MapUtils.getFloatProperty(object.getProperties(), "direction", 90);
                }
            }
        }
        return 90;
    }
    @Override
    public void dispose() {
        mMap.dispose();
    }

    public Array<Vector2> findStartTilePositions() {
        Array<Vector2> lst = new Array<Vector2>();
        for (int ty = 0; ty < mGroundLayer.getHeight(); ++ty) {
            for (int tx = 0; tx < mGroundLayer.getWidth(); ++tx) {
                TiledMapTileLayer.Cell cell = mGroundLayer.getCell(tx, ty);
                if (cell == null) {
                    continue;
                }
                int tileId = cell.getTile().getId();
                if (tileId == mStartTileId) {
                    Vector2 pos = new Vector2(tx * mTileWidth + mTileWidth / 2, ty * mTileHeight);
                    lst.add(pos);
                }
            }
        }
        return lst;
    }

    public boolean isRoadTile(int tileId) {
        return mRoadTileIds.indexOf(tileId) != -1;
    }
}
