/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.tinywheels;

import com.agateau.tinywheels.bonus.BonusPool;
import com.agateau.tinywheels.bonus.BonusSpot;
import com.agateau.tinywheels.bonus.GunBonus;
import com.agateau.tinywheels.bonus.MineBonus;
import com.agateau.tinywheels.bonus.TurboBonus;
import com.agateau.tinywheels.gameobjet.GameObject;
import com.agateau.tinywheels.map.Track;
import com.agateau.tinywheels.racer.AIPilot;
import com.agateau.tinywheels.racer.LapPositionComponent;
import com.agateau.tinywheels.racer.PlayerPilot;
import com.agateau.tinywheels.racer.Racer;
import com.agateau.tinywheels.racer.Vehicle;
import com.agateau.tinywheels.racescreen.Collidable;
import com.agateau.tinywheels.racescreen.CollisionCategories;
import com.agateau.tinywheels.racescreen.CountDown;
import com.agateau.tinywheels.sound.AudioManager;
import com.agateau.tinywheels.utils.Box2DUtils;
import com.agateau.tinywheels.vehicledef.VehicleCreator;
import com.agateau.tinywheels.vehicledef.VehicleDef;
import com.agateau.utils.Assert;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import com.badlogic.gdx.utils.Sort;

import java.util.Comparator;

/**
 * Contains all the information and objects running in the world
 */
public class GameWorld implements ContactListener, Disposable {
    public enum State {
        COUNTDOWN,
        RUNNING,
        FINISHED
    }

    public static final float BOX2D_TIME_STEP = 1f/60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private final TwGame mGame;
    private Track mTrack;
    private final CountDown mCountDown;

    private final World mBox2DWorld;
    private float mTimeAccumulator = 0;

    private Array<BonusPool> mBonusPools = new Array<BonusPool>();

    private final Array<Racer> mRacers = new Array<Racer>();
    private final Array<Racer> mPlayerRacers = new Array<Racer>();
    private State mState = State.COUNTDOWN;

    private final Array<GameObject> mActiveGameObjects = new Array<GameObject>();

    private final PerformanceCounter mBox2DPerformanceCounter;
    private final PerformanceCounter mGameObjectPerformanceCounter;

    public GameWorld(TwGame game, GameInfo gameInfo, PerformanceCounters performanceCounters) {
        mGame = game;
        mBox2DWorld = new World(new Vector2(0, 0), true);
        mBox2DWorld.setContactListener(this);
        mTrack = gameInfo.getTrack();
        mTrack.init();
        mCountDown = new CountDown(this);

        mBox2DPerformanceCounter = performanceCounters.add("- box2d");
        mGameObjectPerformanceCounter = performanceCounters.add("- g.o");
        setupRacers(gameInfo.getEntrants());
        setupRoadBorders();
        setupBonusSpots();
        setupBonusPools();
    }

    public Track getTrack() {
        return mTrack;
    }

    public World getBox2DWorld() {
        return mBox2DWorld;
    }

    public Racer getPlayerRacer(int playerId) {
        return mPlayerRacers.get(playerId);
    }

    public Array<Racer> getPlayerRacers() {
        return mPlayerRacers;
    }

    public Array<Racer> getRacers() {
        return mRacers;
    }

    public Array<BonusPool> getBonusPools() {
        return mBonusPools;
    }

    public Vehicle getPlayerVehicle(int id) {
        return mPlayerRacers.get(id).getVehicle();
    }

    public Array<GameObject> getActiveGameObjects() {
        return  mActiveGameObjects;
    }

    public void addGameObject(GameObject object) {
        mActiveGameObjects.add(object);
    }

    public int getPlayerRank(int playerId) {
        Racer racer = mPlayerRacers.get(playerId);
        return getRacerRank(racer);
    }

    public CountDown getCountDown() {
        return mCountDown;
    }

    public int getRacerRank(Racer racer) {
        for (int idx = mRacers.size - 1; idx >= 0; --idx) {
            if (mRacers.get(idx) == racer) {
                return idx + 1;
            }
        }
        return -1;
    }

    /**
     * Normalized rank goes from 0 to 1, where 0 is for the first racer and 1 is for the last one
     * If there is only one racer, returns 0
     */
    public float getRacerNormalizedRank(Racer racer) {
        if (mRacers.size == 1) {
            return 0;
        }
        return (getRacerRank(racer) - 1) / (float)(mRacers.size - 1);
    }

    /**
     * Sort racers, listing racers which have driven the longest first,
     * so it returns 1 if racer1 has driven less than racer2
     */
    private static Comparator<Racer> sRacerComparator = new Comparator<Racer>() {
        @Override
        public int compare(Racer racer1, Racer racer2) {
            LapPositionComponent c1 = racer1.getLapPositionComponent();
            LapPositionComponent c2 = racer2.getLapPositionComponent();
            if (!c1.hasFinishedRace() && c2.hasFinishedRace()) {
                return 1;
            }
            if (c1.hasFinishedRace() && !c2.hasFinishedRace()) {
                return -1;
            }
            if (c1.getLapCount() < c2.getLapCount()) {
                return 1;
            }
            if (c1.getLapCount() > c2.getLapCount()) {
                return -1;
            }
            float d1 = c1.getLapDistance();
            float d2 = c2.getLapDistance();
            return Float.compare(d2, d1);
        }
    };

    public void act(float delta) {
        mCountDown.act(delta);
        mBox2DPerformanceCounter.start();
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(delta, 0.25f);
        mTimeAccumulator += frameTime;
        while (mTimeAccumulator >= BOX2D_TIME_STEP) {
            mBox2DWorld.step(BOX2D_TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            mTimeAccumulator -= BOX2D_TIME_STEP;
        }
        mBox2DPerformanceCounter.stop();

        mGameObjectPerformanceCounter.start();
        for (int idx = mActiveGameObjects.size - 1; idx >= 0; --idx) {
            GameObject obj = mActiveGameObjects.get(idx);
            obj.act(delta);
            if (obj.isFinished()) {
                mActiveGameObjects.removeIndex(idx);
                if (obj instanceof Disposable) {
                    ((Disposable) obj).dispose();
                }
            }
        }
        mGameObjectPerformanceCounter.stop();

        // Skip finished racers so that they keep the position they had when they crossed the finish
        // line, even if they continue a bit after it
        int fromIndex;
        for (fromIndex = 0; fromIndex < mRacers.size; ++fromIndex) {
            if (!mRacers.get(fromIndex).getLapPositionComponent().hasFinishedRace()) {
                break;
            }
        }
        Sort.instance().sort(mRacers.items, sRacerComparator, fromIndex, mRacers.size);

        boolean allFinished = true;
        for (Racer racer : mPlayerRacers) {
            if (!racer.getLapPositionComponent().hasFinishedRace()) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            setState(State.FINISHED);
        }
    }

    private void setupRacers(Array<GameInfo.Entrant> entrants) {
        VehicleCreator creator = new VehicleCreator(mGame.getAssets(), this);
        Assets assets = mGame.getAssets();

        final float startAngle = 90;
        Array<Vector2> positions = mTrack.findStartTilePositions();
        positions.reverse();

        AudioManager audioManager = mGame.getAudioManager();
        for (int idx = 0; idx < entrants.size; ++idx) {
            Assert.check(idx < positions.size, "Too many entrants");
            GameInfo.Entrant entrant = entrants.get(idx);
            VehicleDef vehicleDef = assets.findVehicleDefByID(entrant.vehicleId);
            Vehicle vehicle = creator.create(vehicleDef, positions.get(idx), startAngle);
            Racer racer = new Racer(assets, audioManager, this, vehicle);
            if (entrant instanceof GameInfo.Player) {
                GameInfo.Player player = (GameInfo.Player)entrant;
                racer.setPilot(new PlayerPilot(assets, this, racer, player.inputHandler));
                mPlayerRacers.add(racer);
            } else {
                racer.setPilot(new AIPilot(this, mTrack, racer));
            }
            addGameObject(racer);
            mRacers.add(racer);
        }
    }

    private void setupRoadBorders() {
        for (MapObject object : mTrack.getBorderObjects()) {
            Body body = Box2DUtils.createStaticBodyForMapObject(mBox2DWorld, object);
            Box2DUtils.setCollisionInfo(body, CollisionCategories.WALL,
                    CollisionCategories.RACER
                            | CollisionCategories.FLAT_OBJECT
                            | CollisionCategories.RACER_BULLET);
            Box2DUtils.setBodyRestitution(body, GamePlay.instance.borderRestitution / 10.0f);
        }
    }

    private void setupBonusSpots() {
        for (Vector2 pos : mTrack.findBonusSpotPositions()) {
            BonusSpot spot = new BonusSpot(mGame.getAssets(), mGame.getAudioManager(), this, pos.x, pos.y);
            addGameObject(spot);
        }
    }

    private void setupBonusPools() {
        mBonusPools.add(new GunBonus.Pool(mGame.getAssets(), this, mGame.getAudioManager()));
        mBonusPools.add(new MineBonus.Pool(mGame.getAssets(), this, mGame.getAudioManager()));
        mBonusPools.add(new TurboBonus.Pool(mGame.getAssets(), this, mGame.getAudioManager()));
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

    public void startRace() {
        setState(State.RUNNING);
    }

    private void setState(State state) {
        mState = state;
    }

    @Override
    public void dispose() {
        if (mTrack != null) {
            mTrack.dispose();
        }
        for (GameObject gameObject : mActiveGameObjects) {
            if (gameObject instanceof Disposable) {
                ((Disposable) gameObject).dispose();
            }
        }
        mActiveGameObjects.clear();
    }

    public void forgetTrack() {
        mTrack = null;
    }

}
