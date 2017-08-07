package com.agateau.tinywheels;

import com.agateau.utils.Assert;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * The map of the current game
 */
public class MapInfo implements Disposable {
    private static final int CELL_ID_ROW_STRIDE = 10000;

    private static class WaypointInfo implements Comparable {
        float lapDistance;
        Vector2 waypoint;

        @Override
        public int compareTo(Object o) {
            WaypointInfo other = (WaypointInfo)o;
            if (lapDistance < other.lapDistance) {
                return -1;
            } else if (lapDistance == other.lapDistance) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    private final String mId;
    private final String mMapName;

    private TiledMap mMap;
    private float[] mMaxSpeedForTileId;
    private int mStartTileId = -1;
    private Array<TiledMapTileLayer> mBackgroundLayers;
    private Array<TiledMapTileLayer> mForegroundLayers;
    private MapLayer mBordersLayer;
    private float mTileWidth;
    private float mTileHeight;
    private Array<WaypointInfo> mWaypointInfos = new Array<WaypointInfo>();
    private LapPositionTable mLapPositionTable;
    private Color mBackgroundColor;

    public MapInfo(String id, String name) {
        mId = id;
        mMapName = name;
    }

    public void init() {
        if (mMap != null) {
            return;
        }
        AtlasTmxMapLoader loader = new AtlasTmxMapLoader();
        mMap = loader.load("maps/" + mId + ".tmx");
        mMaxSpeedForTileId = computeMaxSpeedForTileId();
        findSpecialTileIds();
        findLayers();

        mTileWidth = Constants.UNIT_FOR_PIXEL * mBackgroundLayers.get(0).getTileWidth();
        mTileHeight = Constants.UNIT_FOR_PIXEL * mBackgroundLayers.get(0).getTileHeight();

        mLapPositionTable = LapPositionTableIO.load(mMap);
        readWaypoints();

        String bgColorText = mMap.getProperties().get("backgroundcolor", "#808080", String.class);
        bgColorText = bgColorText.substring(1); // Skip leading '#'
        mBackgroundColor = Color.valueOf(bgColorText);
    }

    private void findLayers() {
        mBackgroundLayers = findLayersMatching("bg");
        Assert.check(mBackgroundLayers.size > 0, "No background layers found");

        mForegroundLayers = findLayersMatching("fg");

        mBordersLayer = mMap.getLayers().get("Borders");
        Assert.check(mBordersLayer != null, "No \"Borders\" layer found");
    }

    private Array<TiledMapTileLayer> findLayersMatching(String match) {
        Array<TiledMapTileLayer> array = new Array<TiledMapTileLayer>();
        for (int idx = 0; idx < mMap.getLayers().getCount(); ++idx) {
            MapLayer layer = mMap.getLayers().get(idx);
            if (layer.getName().startsWith(match)) {
                array.add((TiledMapTileLayer)layer);
            }
        }
        return array;
    }

    public String getId() {
        return mId;
    }

    public Color getBackgroundColor() {
        return mBackgroundColor;
    }

    public int getTotalLapCount() {
        return 3;
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
        return mTileWidth * mBackgroundLayers.get(0).getWidth();
    }

    public float getMapHeight() {
        return mTileHeight * mBackgroundLayers.get(0).getHeight();
    }

    public MapLayer getBordersLayer() {
        return mBordersLayer;
    }

    public LapPositionTable getLapPositionTable() {
        return mLapPositionTable;
    }

    public int[] getExtraBackgroundLayerIndexes() {
        int[] indexes = new int[mBackgroundLayers.size - 1];
        for (int idx = 1; idx < mBackgroundLayers.size; ++idx) {
            indexes[idx - 1] = idx;
        }
        return indexes;
    }

    public int[] getForegroundLayerIndexes() {
        int[] indexes = new int[mForegroundLayers.size];
        // Foreground layers are just after background layers
        int start = mBackgroundLayers.size;
        for (int idx = 0; idx < mForegroundLayers.size; ++idx) {
            indexes[idx] = start + idx;
        }
        return indexes;
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
            if (MapUtils.getBooleanProperty(properties, "start", false)) {
                mStartTileId = tile.getId();
            }
        }
        Assert.check(mStartTileId != -1, "No start id");
    }

    private TiledMapTile getTopTileAt(Array<TiledMapTileLayer> layers, float x, float y) {
        int tx = MathUtils.floor(x / mTileWidth);
        int ty = MathUtils.floor(y / mTileHeight);
        for (int idx = layers.size - 1; idx >= 0; idx--) {
            TiledMapTileLayer.Cell cell = layers.get(idx).getCell(tx, ty);
            if (cell != null) {
                return cell.getTile();
            }
        }
        return null;
    }

    /**
     * Returns a "cell id" for the given screen coordinates
     * A cell id is a long representing the combination of x and y in map coordinates
     */
    public long getCellIdAt(float x, float y) {
        int tx = MathUtils.floor(x / mTileWidth);
        int ty = MathUtils.floor(y / mTileHeight);
        return ty * CELL_ID_ROW_STRIDE + tx;
    }

    public float getMaxSpeedAt(Vector2 pos) {
        return getMaxSpeedAt(pos.x, pos.y);
    }

    public float getMaxSpeedAt(float x, float y) {
        TiledMapTile tile = getTopTileAt(mBackgroundLayers, x, y);
        if (tile == null) {
            return 1.0f;
        }
        return mMaxSpeedForTileId[tile.getId()];
    }

    @Override
    public void dispose() {
        mMap.dispose();
        mMap = null;
    }

    public Array<Vector2> findStartTilePositions() {
        Array<Vector2> lst = new Array<Vector2>();
        TiledMapTileLayer groundLayer = mBackgroundLayers.get(0);
        for (int ty = 0; ty < groundLayer.getHeight(); ++ty) {
            for (int tx = 0; tx < groundLayer.getWidth(); ++tx) {
                TiledMapTileLayer.Cell cell = groundLayer.getCell(tx, ty);
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

    public Array<Vector2> findBonusSpotPositions() {
        final float U = Constants.UNIT_FOR_PIXEL;
        MapLayer layer = mMap.getLayers().get("BonusSpots");
        Assert.check(layer != null, "No BonusSpots layer");
        Array<Vector2> lst = new Array<Vector2>();

        for (MapObject object : layer.getObjects()) {
            if (!(object instanceof EllipseMapObject)) {
                throw new RuntimeException("BonusSpots layer should contains only ellipses. " + object + " is not an ellipse.");
            }
            Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
            Vector2 pos = new Vector2(ellipse.x * U, ellipse.y * U);
            lst.add(pos);
        }
        return lst;
    }

    private void readWaypoints() {
        final float U = Constants.UNIT_FOR_PIXEL;
        MapLayer layer = mMap.getLayers().get("Waypoints");
        Assert.check(layer != null, "No Waypoints layer");

        for (MapObject object : layer.getObjects()) {
            Assert.check(object instanceof EllipseMapObject, "Waypoints layer should contains only ellipses. " + object + " is not an ellipse.");
            Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
            final LapPosition pos = mLapPositionTable.get((int) ellipse.x, (int) ellipse.y);
            WaypointInfo info = new WaypointInfo();
            info.waypoint = new Vector2(ellipse.x * U, ellipse.y * U);
            info.lapDistance = pos.getLapDistance();
            mWaypointInfos.add(info);
        }
        mWaypointInfos.sort();
    }

    public Vector2 getWaypoint(float lapDistance) {
        int nextIdx = 0;
        for (int idx = 0; idx < mWaypointInfos.size; ++idx) {
            if (lapDistance < mWaypointInfos.get(idx).lapDistance) {
                nextIdx = idx;
                break;
            }
        }
        // Target the waypoint after the next one to produce smoother moves
        nextIdx = (nextIdx + 1) % mWaypointInfos.size;
        return mWaypointInfos.get(nextIdx).waypoint;
    }
}
