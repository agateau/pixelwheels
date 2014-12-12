package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

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
    private final TiledMap mMap;
    private final float[] mMaxSpeedForTileId;
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

    public GameWorld(RaceGame game, TiledMap map) {
        mGame = game;
        mBox2DWorld = new World(new Vector2(0, 0), true);
        mBox2DWorld.setContactListener(this);
        mMap = map;
        mMaxSpeedForTileId = computeMaxSpeedForTileId();
        setupSled();
        setupOutsideWalls();
        setupObjects();
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

    public TiledMap getMap() {
        return mMap;
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

    public boolean isVisible(float x, float y) {
        float dy = Math.abs(y - mVehicle.getY());
        return dy < Constants.VIEWPORT_WIDTH * 1.2;
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

        // FIXME: Use SnapshotArray if game objects ever gain access to removing items from mActiveGameObjects
        for (int idx = mActiveGameObjects.size - 1; idx >= 0; --idx) {
            GameObject obj = mActiveGameObjects.get(idx);
            if (!obj.act(delta)) {
                mActiveGameObjects.removeIndex(idx);
            }
        }
        checkChimneys();
    }

    private void setupSled() {
        Vector2 position = findStartTilePosition();
        assert(position != null);

        // Car
        TextureRegion carRegion = mGame.getAssets().atlas.findRegion("sled/sled");
        TextureRegion wheelRegion = mGame.getAssets().atlas.findRegion("sled/sled-ski");
        mVehicle = new PlayerVehicle(carRegion, this, position);
        mVehicle.setLimitAngle(true);
        //mVehicle.setCorrectAngle(true);

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
        //info.wheel.setCanDrift(true);
        info = mVehicle.addWheel(wheelRegion, rightX, rearY);
        //info.wheel.setCanDrift(true);

        addGameObject(mVehicle);
    }

    private Vector2 findStartTilePosition() {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get("Centers");
        int tx = 0;
        for (; tx < layer.getWidth(); ++tx) {
            TiledMapTileLayer.Cell cell = layer.getCell(tx, 0);
            if (cell != null) {
                break;
            }
        }
        float tw = Constants.UNIT_FOR_PIXEL * layer.getTileWidth();
        float th = Constants.UNIT_FOR_PIXEL * layer.getTileHeight();
        return new Vector2(tx * tw + tw / 2, th);
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

    private void setupObjects() {
        MapLayer layer = mMap.getLayers().get("Obstacles");
        if (layer == null) {
            return;
        }
        ObstacleCreator creator = new ObstacleCreator(this, mGame.getAssets());
        for (MapObject object : layer.getObjects()) {
            creator.create(object);
        }
    }

    public void addChimneyPos(float x, float y) {
        mChimneys.add(new Vector2(x, y));
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

    public float getMaxSpeedAt(Vector2 pos) {
        TiledMapTile tile = getTileAt(pos);
        if (tile == null) {
            return 1.0f;
        }
        return mMaxSpeedForTileId[tile.getId()];
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
