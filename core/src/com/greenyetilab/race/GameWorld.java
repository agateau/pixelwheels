package com.greenyetilab.race;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;

/**
 * Contains all the information and objects running in the world
 */
public class GameWorld implements ContactListener, Disposable {
    public enum State {
        RUNNING,
        BROKEN,
        FINISHED
    }

    private static final float TIME_STEP = 1f/60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private final RaceGame mGame;
    private final MapInfo mMapInfo;
    private final HudBridge mHudBridge;

    private final World mBox2DWorld;
    private float mTimeAccumulator = 0;

    private Racer mPlayerRacer;
    private State mState = State.RUNNING;

    private Vector2[] mSkidmarks = new Vector2[20];
    private int mSkidmarksIndex = 0;
    private final Array<GameObject> mActiveGameObjects = new Array<GameObject>();

    private float mBottomVisibleY = 0;
    private float mTopVisibleY = 0;

    private final PerformanceCounter mBox2DPerformanceCounter;
    private final PerformanceCounter mGameObjectPerformanceCounter;

    public GameWorld(RaceGame game, MapInfo mapInfo, HudBridge hudBridge, PerformanceCounters performanceCounters) {
        mGame = game;
        mBox2DWorld = new World(new Vector2(0, 0), true);
        mBox2DWorld.setContactListener(this);
        mMapInfo = mapInfo;
        mHudBridge = hudBridge;

        mBox2DPerformanceCounter = performanceCounters.add("- box2d");
        mGameObjectPerformanceCounter = performanceCounters.add("- g.o");
        setupRacers();
        setupOutsideWalls();
        setupRoadBorders();
    }

    public MapInfo getMapInfo() {
        return mMapInfo;
    }

    public World getBox2DWorld() {
        return mBox2DWorld;
    }

    public Vehicle getPlayerVehicle() {
        return mPlayerRacer.getVehicle();
    }

    public int getScore() {
        return mPlayerRacer.getScore();
    }

    public HudBridge getHudBridge() {
        return mHudBridge;
    }

    public void showScoreIndicator(int delta, float worldX, float worldY) {
        Vector2 pos = mHudBridge.toHudCoordinate(worldX, worldY);
        Actor indicator = ScoreIndicator.create(mGame.getAssets(), delta, pos.x, pos.y);
        mHudBridge.getStage().addActor(indicator);
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

    public void setVisibleSection(float bottom, float top) {
        mBottomVisibleY = bottom;
        mTopVisibleY = top;
    }

    public float getTopVisibleY() {
        return mTopVisibleY;
    }

    public float getBottomVisibleY() {
        return mBottomVisibleY;
    }

    public void act(float delta) {
        mBox2DPerformanceCounter.start();
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(delta, 0.25f);
        mTimeAccumulator += frameTime;
        while (mTimeAccumulator >= TIME_STEP) {
            mBox2DWorld.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            mTimeAccumulator -= TIME_STEP;
        }
        mBox2DPerformanceCounter.stop();

        float outOfSightLimit = mBottomVisibleY - Constants.VIEWPORT_POOL_RECYCLE_HEIGHT;
        mGameObjectPerformanceCounter.start();
        for (int idx = mActiveGameObjects.size - 1; idx >= 0; --idx) {
            GameObject obj = mActiveGameObjects.get(idx);
            if (!obj.act(delta)) {
                if (obj == mPlayerRacer) {
                    setState(GameWorld.State.BROKEN);
                }
                mActiveGameObjects.removeIndex(idx);
                continue;
            }
            if (obj.getY() < outOfSightLimit && obj instanceof DisposableWhenOutOfSight) {
                ((DisposableWhenOutOfSight)obj).dispose();
                mActiveGameObjects.removeIndex(idx);
            }
        }
        mGameObjectPerformanceCounter.stop();
    }

    private void setupRacers() {
        VehicleFactory factory = new VehicleFactory(mGame.getAssets(), this);
        Assets assets = mGame.getAssets();

        final int PLAYER_RANK = 1;
        final float startAngle = 90;
        int rank = 1;
        Array<Vector2> positions = mMapInfo.findStartTilePositions();
        positions.reverse();

        for (Vector2 position : positions) {
            Racer racer;
            if (PLAYER_RANK == rank) {
                mPlayerRacer = new Racer(this, factory.create("player", position.x, position.y, startAngle));
                mPlayerRacer.setPilot(new PlayerPilot(assets, this, mPlayerRacer));
                racer = mPlayerRacer;
            } else {
                racer = new Racer(this, factory.create("enemy", position.x, position.y, startAngle));
                Pilot pilot;
                if (mPlayerRacer == null) {
                    pilot = new BasicPilot(mMapInfo, racer.getVehicle(), racer.getHealthComponent());
                } else {
                    pilot = new TrackingPilot(racer, mPlayerRacer);
                }
                racer.setPilot(pilot);
            }
            addGameObject(racer);
            ++rank;
        }
    }

    private void setupOutsideWalls() {
        float mapWidth = mMapInfo.getMapWidth();
        float mapHeight = mMapInfo.getMapHeight();
        float wallSize = 1;
        Body body;
        int mask = CollisionCategories.RACER | CollisionCategories.RACER_BULLET
                        | CollisionCategories.AI_VEHICLE | CollisionCategories.FLAT_AI_VEHICLE
                        | CollisionCategories.GIFT;
        // bottom
        body = Box2DUtils.createStaticBox(mBox2DWorld, 0, -wallSize, mapWidth, wallSize);
        Box2DUtils.setCollisionInfo(body, CollisionCategories.WALL, mask);
        // left
        body = Box2DUtils.createStaticBox(mBox2DWorld, -wallSize, 0, wallSize, mapHeight);
        Box2DUtils.setCollisionInfo(body, CollisionCategories.WALL, mask);
        // right
        body = Box2DUtils.createStaticBox(mBox2DWorld, mapWidth, 0, wallSize, mapHeight);
        Box2DUtils.setCollisionInfo(body, CollisionCategories.WALL, mask);
    }

    private void setupRoadBorders() {
        for (MapObject object : mMapInfo.getBordersLayer().getObjects()) {
            Body body = Box2DUtils.createStaticBodyForMapObject(mBox2DWorld, object);
            Box2DUtils.setCollisionInfo(body, CollisionCategories.WALL,
                    CollisionCategories.RACER | CollisionCategories.AI_VEHICLE | CollisionCategories.GIFT);
        }
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
        Object userA = contact.getFixtureA().getBody().getUserData();
        Object userB = contact.getFixtureB().getBody().getUserData();
        if (userA instanceof Collidable) {
            ((Collidable) userA).preSolve(contact, contact.getFixtureB(), oldManifold);
        }
        if (userB instanceof Collidable) {
            ((Collidable) userB).preSolve(contact, contact.getFixtureA(), oldManifold);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Object userA = contact.getFixtureA().getBody().getUserData();
        Object userB = contact.getFixtureB().getBody().getUserData();
        if (userA instanceof Collidable) {
            ((Collidable) userA).postSolve(contact, contact.getFixtureB(), impulse);
        }
        if (userB instanceof Collidable) {
            ((Collidable) userB).postSolve(contact, contact.getFixtureA(), impulse);
        }
    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        mState = state;
    }

    @Override
    public void dispose() {
        mMapInfo.dispose();
    }

}
