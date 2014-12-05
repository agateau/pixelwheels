package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.greenyetilab.utils.TilePolygons;
import com.greenyetilab.utils.log.NLog;

import java.util.Map;

/**
 * Contains all the information and objects running in the world
 */
public class GameWorld {
    private static final float TIME_STEP = 1f/60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private final MapInfo mMapInfo;
    private final TiledMap mMap;
    private final World mBox2DWorld;
    private final RaceGame mGame;
    private float mTimeAccumulator = 0;

    private Vehicle mVehicle;

    private Vector2[] mSkidmarks = new Vector2[4000];
    private int mSkidmarksIndex = 0;

    public GameWorld(RaceGame game, MapInfo mapInfo) {
        mGame = game;
        mBox2DWorld = new World(new Vector2(0, 0), true);
        mMapInfo = mapInfo;
        mMap = mMapInfo.getMap();
        setupCar();
        setupOutsideWalls();
        setupWallsLayer();
        /*
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        float tileWidth = Constants.UNIT_FOR_PIXEL * layer.getTileWidth();
        float tileHeight = Constants.UNIT_FOR_PIXEL * layer.getTileHeight();
        setupRock(tileWidth * 9, tileHeight * 4, tileWidth * 2, tileHeight);
        setupRock(tileWidth * 11, tileHeight * 4, tileWidth, tileHeight);
        setupRock(tileWidth * 10, tileHeight * 5, tileWidth, tileHeight);
        setupRock(tileWidth * 11, tileHeight * 5, tileWidth, tileHeight);
        */
    }

    public TiledMap getMap() {
        return mMapInfo.getMap();
    }

    public MapInfo getMapInfo() {
        return mMapInfo;
    }

    public World getBox2DWorld() {
        return mBox2DWorld;
    }

    public Vehicle getVehicle() {
        return mVehicle;
    }

    public Vector2[] getSkidmarks() {
        return mSkidmarks;
    }

    public void act(float delta) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(delta, 0.25f);
        mTimeAccumulator += frameTime;
        while (mTimeAccumulator >= TIME_STEP) {
            mBox2DWorld.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            mTimeAccumulator -= TIME_STEP;
        }

        mVehicle.act(delta);
    }

    private void setupCar() {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        Vector2 position = findStartTilePosition(layer);
        assert(position != null);

        // Car
        TextureRegion carRegion = mGame.getAssets().car;
        TextureRegion wheelRegion = mGame.getAssets().wheel;
        mVehicle = new Vehicle(carRegion, this, position);

        // Wheels
        final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
        final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 46f;

        float wheelW = Constants.UNIT_FOR_PIXEL * wheelRegion.getRegionWidth();
        float rightX = Constants.UNIT_FOR_PIXEL * carRegion.getRegionWidth() / 2 - wheelW / 2 + 0.05f;
        float leftX = -rightX;
        float rearY = Constants.UNIT_FOR_PIXEL * -carRegion.getRegionHeight() / 2 + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE;

        Vehicle.WheelInfo info;
        info = mVehicle.addWheel(wheelRegion, leftX, frontY);
        info.steeringFactor = 1;
        info = mVehicle.addWheel(wheelRegion, rightX, frontY);
        info.steeringFactor = 1;
        info = mVehicle.addWheel(wheelRegion, leftX, rearY);
        info.wheel.setCanDrift(true);
        info = mVehicle.addWheel(wheelRegion, rightX, rearY);
        info.wheel.setCanDrift(true);
    }

    private Vector2 findStartTilePosition(TiledMapTileLayer layer) {
        for (int ty=0; ty < layer.getHeight(); ++ty) {
            for (int tx=0; tx < layer.getWidth(); ++tx) {
                TiledMapTileLayer.Cell cell = layer.getCell(tx, ty);
                TiledMapTile tile = cell.getTile();
                if (tile.getProperties().containsKey("start")) {
                    float tw = Constants.UNIT_FOR_PIXEL * layer.getTileWidth();
                    float th = Constants.UNIT_FOR_PIXEL * layer.getTileHeight();
                    return new Vector2(tx * tw + tw / 2, ty * th);
                }
            }
        }
        NLog.e("No Tile with 'start' property found");
        return null;
    }

    private void setupOutsideWalls() {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        float mapWidth = Constants.UNIT_FOR_PIXEL * layer.getWidth() * layer.getTileWidth();
        float mapHeight = Constants.UNIT_FOR_PIXEL * layer.getHeight() * layer.getTileHeight();
        float wallSize = 1;
        // bottom
        createWall(0, -wallSize, mapWidth, wallSize);
        // top
        createWall(0, mapHeight, mapWidth, wallSize);
        // left
        createWall(-wallSize, 0, wallSize, mapHeight);
        // right
        createWall(mapWidth, 0, wallSize, mapHeight);
    }

    private void setupWallsLayer() {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get("Walls");
        if (layer == null) {
            return;
        }

        Map<Integer, TilePolygons> polygonsForTile = TilePolygons.readTiledMap(mMapInfo.getFile());

        final float tileWidth = Constants.UNIT_FOR_PIXEL * layer.getTileWidth();
        final float tileHeight = Constants.UNIT_FOR_PIXEL * layer.getTileHeight();
        for (int ty = 0; ty < layer.getHeight(); ++ty) {
            for (int tx = 0; tx < layer.getWidth(); ++tx) {
                TiledMapTileLayer.Cell cell = layer.getCell(tx, ty);
                if (cell == null) {
                    continue;
                }
                int id = cell.getTile().getId();
                TilePolygons polygons = polygonsForTile.get(id);
                if (polygons != null) {
                    createPolygonBody(tx * tileWidth, ty * tileHeight, tileWidth, tileHeight, polygons);
                }
            }
        }
    }

    private void createWall(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width / 2, y + height / 2);
        Body body = mBox2DWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        body.createFixture(shape, 1);
    }

    private void createPolygonBody(float x, float y, float width, float height, TilePolygons polygons) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = mBox2DWorld.createBody(bodyDef);

        polygons.createBodyShapes(body, width, height);
    }

    private void setupRock(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x + width / 2, y + height / 2);
        Body body = mBox2DWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        body.createFixture(shape, 3);
    }

    public TiledMapTile getTileAt(Vector2 pos) {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        float tileW = Constants.UNIT_FOR_PIXEL * layer.getTileWidth();
        float tileH = Constants.UNIT_FOR_PIXEL * layer.getTileHeight();

        int tx = MathUtils.floor(pos.x / tileW);
        int ty = MathUtils.floor(pos.y / tileH);
        TiledMapTileLayer.Cell cell = layer.getCell(tx, ty);
        return cell == null ? null : cell.getTile();
    }

    public void addSkidmarkAt(Vector2 position) {
        Vector2 pos = mSkidmarks[mSkidmarksIndex];
        if (pos == null) {
            pos = new Vector2();
            mSkidmarks[mSkidmarksIndex] = pos;
        }
        pos.x = position.x;
        pos.y = position.y;
        mSkidmarksIndex = (mSkidmarksIndex + 1) % mSkidmarks.length;
    }

}
