package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.greenyetilab.utils.Assert;

/**
 * The map of the current game
 */
public class MapInfo implements Disposable {
    private static abstract class Zone {
        private Vector2 mWaypoint = null;

        abstract boolean contains(float x, float y);

        void setWaypoint(Vector2 waypoint) {
            mWaypoint = waypoint;
        }

        Vector2 getWaypoint() {
            return mWaypoint;
        }
    }

    private static class RectangleZone extends Zone {
        private Rectangle mRectangle;
        public RectangleZone(Rectangle rectangle) {
            mRectangle = rectangle;
        }
        @Override
        public boolean contains(float x, float y) {
            return mRectangle.contains(x, y);
        }
    }

    private static class PolygonZone extends Zone {
        private Polygon mPolygon;
        public PolygonZone(Polygon polygon) {
            mPolygon = polygon;
        }
        @Override
        public boolean contains(float x, float y) {
            return mPolygon.contains(x, y);
        }
    }

    private final TiledMap mMap;
    private final float[] mMaxSpeedForTileId;
    private int mStartTileId = -1;
    private final TiledMapTileLayer mGroundLayer;
    private final MapLayer mBordersLayer;
    private final float mTileWidth;
    private final float mTileHeight;
    private final Array<Zone> mZones = new Array<Zone>();
    private final LapPositionTable mLapPositionTable;
    private final Color mBackgroundColor;

    public MapInfo(TiledMap map) {
        mMap = map;
        mMaxSpeedForTileId = computeMaxSpeedForTileId();
        findSpecialTileIds();

        mGroundLayer = (TiledMapTileLayer)mMap.getLayers().get(0);
        mBordersLayer = mMap.getLayers().get("Borders");
        assert mBordersLayer != null;

        mTileWidth = Constants.UNIT_FOR_PIXEL * mGroundLayer.getTileWidth();
        mTileHeight = Constants.UNIT_FOR_PIXEL * mGroundLayer.getTileHeight();

        readZones();
        mLapPositionTable = LapPositionTableIO.load(map);

        String bgColorText = map.getProperties().get("backgroundcolor", "#808080", String.class);
        bgColorText = bgColorText.substring(1); // Skip leading '#'
        mBackgroundColor = Color.valueOf(bgColorText);
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

    public MapLayer getBordersLayer() {
        return mBordersLayer;
    }

    public LapPositionTable getLapPositionTable() {
        return mLapPositionTable;
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

    private void readZones() {
        final float U = Constants.UNIT_FOR_PIXEL;
        MapLayer zoneLayer = mMap.getLayers().get("Zones");
        Assert.check(zoneLayer != null, "No Zones layer");
        MapLayer waypointsLayer = mMap.getLayers().get("Waypoints");
        Assert.check(waypointsLayer != null, "No Waypoints layer");

        int zoneCount = zoneLayer.getObjects().getCount();
        int waypointCount = waypointsLayer.getObjects().getCount();
        Assert.check(waypointCount == zoneCount, "zoneCount != waypointCount (" + zoneCount + " != " + waypointCount + ")");

        for (int pos = 0;; ++pos) {
            String name = String.valueOf(pos);
            MapObject object = zoneLayer.getObjects().get(name);
            if (object == null) {
                break;
            }
            Zone zone;
            if (object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
                rectangle.x *= U;
                rectangle.y *= U;
                rectangle.width *= U;
                rectangle.height *= U;
                zone = new RectangleZone(rectangle);
            } else if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject)object).getPolygon();
                float[] vertices = polygon.getTransformedVertices().clone();
                for (int i =0; i < vertices.length; ++i) {
                    vertices[i] *= U;
                }
                zone = new PolygonZone(new Polygon(vertices));
            } else {
                throw new RuntimeException("Unsupported object type " + object.getClass().getSimpleName());
            }
            mZones.add(zone);
        }
        Assert.check(mZones.size == zoneCount, "zoneCount != mZones.size (" + zoneCount + " != " + mZones.size + ")");

        for (MapObject object : waypointsLayer.getObjects()) {
            if (!(object instanceof EllipseMapObject)) {
                throw new RuntimeException("Waypoints layer should contains only ellipses. " + object + " is not an ellipse.");
            }
            Ellipse ellipse = ((EllipseMapObject)object).getEllipse();
            Vector2 waypoint = new Vector2(ellipse.x * U, ellipse.y * U);
            int zoneIdx;
            for (zoneIdx = 0; zoneIdx < zoneCount; ++zoneIdx) {
                if (mZones.get(zoneIdx).contains(waypoint.x, waypoint.y)) {
                    break;
                }
            }
            Assert.check(zoneIdx != zoneCount, "Could not find a zone containing waypoint " + waypoint);
            --zoneIdx;
            if (zoneIdx == -1) {
                zoneIdx = zoneCount - 1;
            }
            mZones.get(zoneIdx).setWaypoint(waypoint);
        }

        // Sanity check
        for (int zoneIdx = 0; zoneIdx < zoneCount; ++zoneIdx) {
            Zone zone = mZones.get(zoneIdx);
            if (zone.getWaypoint() == null) {
                throw new RuntimeException("Zone " + zoneIdx + " has no waypoints");
            }
        }
    }

    public Vector2 getWaypoint(float x, float y) {
        for(Zone zone : mZones) {
            if (zone.contains(x, y)) {
                return zone.getWaypoint();
            }
        }
        throw new RuntimeException("No waypoint at " + x + "x" + y);
    }
}
