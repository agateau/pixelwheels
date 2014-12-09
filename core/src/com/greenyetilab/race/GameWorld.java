package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.greenyetilab.utils.TileCollisionBodyCreator;
import com.greenyetilab.utils.log.NLog;

/**
 * Contains all the information and objects running in the world
 */
public class GameWorld implements ContactListener {
    public enum State {
        RUNNING,
        BROKEN,
        FINISHED
    }

    private static final float TIME_STEP = 1f/60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float CHIMNEY_MAX_RADIUS2 = (float)Math.pow(15, 2);
    private static final float GIFT_INTERVAL = 0.2f;
    private final MapInfo mMapInfo;
    private final TiledMap mMap;
    private final World mBox2DWorld;
    private final RaceGame mGame;
    private float mTimeAccumulator = 0;

    private PlayerVehicle mVehicle;
    private State mState = State.RUNNING;

    private Vector2[] mSkidmarks = new Vector2[4000];
    private int mSkidmarksIndex = 0;
    private Array<Vector2> mChimneys = new Array<Vector2>();
    private Pool<Gift> mGiftPool = new ReflectionPool<Gift>(Gift.class);
    private Array<GameObject> mActiveGameObjects = new Array<GameObject>();

    public GameWorld(RaceGame game, MapInfo mapInfo) {
        mGame = game;
        mBox2DWorld = new World(new Vector2(0, 0), true);
        mBox2DWorld.setContactListener(this);
        mMapInfo = mapInfo;
        mMap = mMapInfo.getMap();
        //setupCar();
        setupSled();
        setupOutsideWalls();
        setupWallsLayer();
        findChimneys();
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

    public Array<GameObject> getActiveGameObjects() {
        return  mActiveGameObjects;
    }

    public void addGameObject(GameObject object) {
        mActiveGameObjects.add(object);
    }

    public void removeGameObject(GameObject object) {
        mActiveGameObjects.removeValue(object, true);
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
        checkChimneys();
        // FIXME: Use SnapshotArray if game objects ever gain access to removing items from mActiveGameObjects
        for (int idx = mActiveGameObjects.size - 1; idx >= 0; --idx) {
            GameObject obj = mActiveGameObjects.get(idx);
            if (!obj.act(delta)) {
                mActiveGameObjects.removeIndex(idx);
            }
        }
    }

    private void setupCar() {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        Vector2 position = findStartTilePosition(layer);
        assert(position != null);

        // Car
        TextureRegion carRegion = mGame.getAssets().car;
        TextureRegion wheelRegion = mGame.getAssets().wheel;
        mVehicle = new PlayerVehicle(carRegion, this, position);

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

    private void setupSled() {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        Vector2 position = findStartTilePosition(layer);
        assert(position != null);

        // Car
        TextureRegion carRegion = mGame.getAssets().atlas.findRegion("sled/sled");
        TextureRegion wheelRegion = mGame.getAssets().atlas.findRegion("sled/sled-ski");
        mVehicle = new PlayerVehicle(carRegion, this, position);
        mVehicle.setLimitAngle(true);
        mVehicle.setCorrectAngle(true);

        // Wheels
        final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
        final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 46f;

        float wheelW = Constants.UNIT_FOR_PIXEL * wheelRegion.getRegionWidth();
        float rightX = Constants.UNIT_FOR_PIXEL * carRegion.getRegionWidth() / 2 - wheelW / 2 + 0.05f;
        float leftX = -rightX;
        float rearY = Constants.UNIT_FOR_PIXEL * -carRegion.getRegionHeight() / 2 + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE + 0.2f;

        Vehicle.WheelInfo info;
        info = mVehicle.addWheel(wheelRegion, 0, frontY);
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
        Box2DUtils.createStaticBox(mBox2DWorld, 0, -wallSize, mapWidth, wallSize);
        // top
        Box2DUtils.createStaticBox(mBox2DWorld, 0, mapHeight, mapWidth, wallSize);
        // left
        Box2DUtils.createStaticBox(mBox2DWorld, -wallSize, 0, wallSize, mapHeight);
        // right
        Box2DUtils.createStaticBox(mBox2DWorld, mapWidth, 0, wallSize, mapHeight);
    }

    private void setupWallsLayer() {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get("Walls");
        if (layer == null) {
            return;
        }
        TileCollisionBodyCreator creator = TileCollisionBodyCreator.fromFileHandle(mMapInfo.getFile());
        creator.createCollisionBodies(mBox2DWorld, Constants.UNIT_FOR_PIXEL, layer);
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

    private void findChimneys() {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get("Walls");
        if (layer == null) {
            return;
        }
        float tw = Constants.UNIT_FOR_PIXEL * layer.getTileWidth();
        float th = Constants.UNIT_FOR_PIXEL * layer.getTileHeight();
        for (int ty=0; ty < layer.getHeight(); ++ty) {
            for (int tx=0; tx < layer.getWidth(); ++tx) {
                TiledMapTileLayer.Cell cell = layer.getCell(tx, ty);
                if (cell == null) {
                    continue;
                }
                TiledMapTile tile = cell.getTile();
                if (tile.getProperties().containsKey("chimney")) {
                    Vector2 pos = new Vector2(tx * tw + tw / 2, ty * th + th / 2);
                    mChimneys.add(pos);
                }
            }
        }
    }

    private void checkChimneys() {
        Vector2 vehiclePos = mVehicle.getPosition();
        for (int idx = mChimneys.size - 1; idx >= 0; --idx) {
            Vector2 pos = mChimneys.get(idx);
            float distance2 = pos.dst2(vehiclePos);
            if (distance2 < CHIMNEY_MAX_RADIUS2) {
                mChimneys.removeIndex(idx);
                int count = MathUtils.random(1, 4);
                for (int i = 0; i < count; ++i) {
                    Gift gift = mGiftPool.obtain();
                    gift.init(mGame, mVehicle, pos, i * GIFT_INTERVAL);
                    addGameObject(gift);
                }
            }
        }
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

    @Override
    public void beginContact(Contact contact) {
        Object userA = contact.getFixtureA().getBody().getUserData();
        Object userB = contact.getFixtureB().getBody().getUserData();
        if (userA instanceof Collidable) {
            ((Collidable) userA).beginContact(contact, contact.getFixtureB());
        }
        if (userB instanceof Collidable) {
            ((Collidable) userB).beginContact(contact, contact.getFixtureA());
        }
    }

    @Override
    public void endContact(Contact contact) {
        Object userA = contact.getFixtureA().getBody().getUserData();
        Object userB = contact.getFixtureB().getBody().getUserData();
        if (userA instanceof Collidable) {
            ((Collidable) userA).endContact(contact, contact.getFixtureB());
        }
        if (userB instanceof Collidable) {
            ((Collidable) userB).endContact(contact, contact.getFixtureA());
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        mState = state;
    }
}
