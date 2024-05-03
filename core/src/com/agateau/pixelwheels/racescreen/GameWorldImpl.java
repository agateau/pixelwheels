/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.pixelwheels.racescreen;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.bonus.Bonus;
import com.agateau.pixelwheels.bonus.BonusPool;
import com.agateau.pixelwheels.bonus.BonusSpot;
import com.agateau.pixelwheels.bonus.GunBonus;
import com.agateau.pixelwheels.bonus.MineBonus;
import com.agateau.pixelwheels.bonus.MissileBonus;
import com.agateau.pixelwheels.bonus.TurboBonus;
import com.agateau.pixelwheels.gameobject.GameObject;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.obstacles.ObstacleCreator;
import com.agateau.pixelwheels.obstacles.ObstacleDef;
import com.agateau.pixelwheels.obstacles.tiled.TiledObstacleCreator;
import com.agateau.pixelwheels.racer.AIPilot;
import com.agateau.pixelwheels.racer.LapPositionComponent;
import com.agateau.pixelwheels.racer.PlayerPilot;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racer.Vehicle;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.stats.GameStats;
import com.agateau.pixelwheels.stats.TrackStats;
import com.agateau.pixelwheels.vehicledef.VehicleCreator;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.utils.Assert;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import java.util.Comparator;
import java.util.Scanner;

public class GameWorldImpl implements ContactListener, Disposable, GameWorld {
    private static final Racer.RecordRanks DEBUG_RECORD_RANKS = parseFinishedOverlayDebugScreen();

    private final PwGame mGame;
    private Track mTrack;
    private final CountDown mCountDown;

    private final World mBox2DWorld;
    private float mTimeAccumulator = 0;

    @SuppressWarnings("rawtypes")
    private final Array<BonusPool> mBonusPools = new Array<>();

    private final Array<Racer> mRacers = new Array<>();
    private final Array<Racer> mPlayerRacers = new Array<>();
    private State mState = GameWorld.State.COUNTDOWN;

    private final Array<GameObject> mActiveGameObjects = new Array<>();

    private final PerformanceCounter mBox2DPerformanceCounter;
    private final PerformanceCounter mGameObjectPerformanceCounter;

    GameWorldImpl(PwGame game, GameInfo gameInfo, PerformanceCounters performanceCounters) {
        mGame = game;
        mBox2DWorld = new World(new Vector2(0, 0), true);
        mBox2DWorld.setContactListener(this);
        mTrack = gameInfo.getTrack();
        mTrack.init();
        mCountDown = new CountDown(this, game.getAudioManager(), game.getAssets().soundAtlas);

        mBox2DPerformanceCounter = performanceCounters.add("- box2d");
        mGameObjectPerformanceCounter = performanceCounters.add("- g.o");
        setupRacers(gameInfo.getEntrants());
        setupObstacles();
        setupBonusSpots();
        setupBonusPools();
    }

    @Override
    public Track getTrack() {
        return mTrack;
    }

    @Override
    public World getBox2DWorld() {
        return mBox2DWorld;
    }

    @Override
    public Racer getPlayerRacer(int playerId) {
        return mPlayerRacers.get(playerId);
    }

    @Override
    public Array<Racer> getPlayerRacers() {
        return mPlayerRacers;
    }

    @Override
    public Array<Racer> getRacers() {
        return mRacers;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<BonusPool> getBonusPools() {
        return mBonusPools;
    }

    @Override
    public Array<GameObject> getActiveGameObjects() {
        return mActiveGameObjects;
    }

    @Override
    public void addGameObject(GameObject object) {
        mActiveGameObjects.add(object);
    }

    @Override
    public CountDown getCountDown() {
        return mCountDown;
    }

    @Override
    public int getRacerRank(Racer wantedRacer) {
        int rank = 1;
        for (Racer racer : mRacers) {
            if (racer != wantedRacer && sRacerComparator.compare(racer, wantedRacer) < 0) {
                // racer is in front of wantedRacer
                rank += 1;
            }
        }
        return rank;
    }

    /**
     * Normalized rank goes from 0 to 1, where 0 is for the first racer and 1 is for the last one If
     * there is only one racer, returns 0
     */
    @Override
    public float getRacerNormalizedRank(Racer racer) {
        if (mRacers.size == 1) {
            return 0;
        }
        return (getRacerRank(racer) - 1) / (float) (mRacers.size - 1);
    }

    @Override
    public GameStats getGameStats() {
        return mGame.getGameStats();
    }

    /**
     * Sort racers, listing racers which have driven the longest first, so it returns 1 if racer1
     * has driven less than racer2
     */
    private static final Comparator<Racer> sRacerComparator =
            (racer1, racer2) -> -Racer.compareRaceDistances(racer1, racer2);

    @Override
    public void act(float delta) {
        // limit speed one before another, propotional to the distance between them
        final int distancePerLap = mTrack.getLapPositionTable().getSectionCount();
        mRacers.sort(sRacerComparator);
        Racer racerBehind = null;
        for (int i = mRacers.size - 1; i >= 0; i--) {
            final Racer racer = mRacers.get(i);
            if (racer.getLapPositionComponent().hasFinishedRace()) {
                break; // this racer has finished, all racers ahead of it must have finished too
            }
            if (racerBehind == null) {
                racer.getVehicle().setSpeedLimiter(1);
            } else {
                final LapPositionComponent p1 = racer.getLapPositionComponent();
                final LapPositionComponent p2 = racerBehind.getLapPositionComponent();
                final float ddiff =
                        (p1.getLapCount() - p2.getLapCount()) * distancePerLap
                                + p1.getLapDistance()
                                - p2.getLapDistance();
                assert ddiff >= 0;
                if (ddiff > distancePerLap) {
                    // more than one lap, spare the aheader for speed limit
                    racer.getVehicle().setSpeedLimiter(1);
                } else {
                    racer.getVehicle()
                            .setSpeedLimiter(
                                    racerBehind.getVehicle().getSpeedLimiter()
                                            * Math.max(
                                                    GamePlay.instance.extremeSpeedLimiter,
                                                    1.0f - ddiff / distancePerLap));
                }
            }
            racerBehind = racer;
        }

        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(delta, 0.25f);
        mTimeAccumulator += frameTime;
        while (mTimeAccumulator >= GameWorld.BOX2D_TIME_STEP) {
            mCountDown.act(GameWorld.BOX2D_TIME_STEP);

            mBox2DPerformanceCounter.start();
            mBox2DWorld.step(
                    GameWorld.BOX2D_TIME_STEP,
                    GameWorld.VELOCITY_ITERATIONS,
                    GameWorld.POSITION_ITERATIONS);
            mBox2DPerformanceCounter.stop();

            mGameObjectPerformanceCounter.start();
            for (int idx = mActiveGameObjects.size - 1; idx >= 0; --idx) {
                GameObject obj = mActiveGameObjects.get(idx);
                obj.act(GameWorld.BOX2D_TIME_STEP);
                if (obj.isFinished()) {
                    mActiveGameObjects.removeIndex(idx);
                    if (obj instanceof Disposable) {
                        ((Disposable) obj).dispose();
                    }
                }
            }
            mGameObjectPerformanceCounter.stop();

            mTimeAccumulator -= GameWorld.BOX2D_TIME_STEP;
        }

        if (haveAllRacersFinished()) {
            setState(GameWorld.State.FINISHED);
        }
    }

    private boolean haveAllRacersFinished() {
        if (DEBUG_RECORD_RANKS != null && mState == State.RUNNING) {
            mRacers.shuffle();
            return true;
        }
        for (Racer racer : mPlayerRacers) {
            if (!racer.getLapPositionComponent().hasFinishedRace()) {
                return false;
            }
        }
        return true;
    }

    private void onFinished() {
        TrackStats stats = mGame.getGameStats().getTrackStats(mTrack);
        for (int idx = 0; idx < mRacers.size; ++idx) {
            Racer racer = mRacers.get(idx);
            racer.markRaceFinished();
            GameInfo.Entrant entrant = racer.getEntrant();

            int points = mRacers.size - idx;
            entrant.addPoints(points);

            LapPositionComponent lapPositionComponent = racer.getLapPositionComponent();
            if (DEBUG_RECORD_RANKS == null) {
                entrant.addRaceTime(lapPositionComponent.getTotalTime());

                if (entrant.isPlayer()) {
                    Racer.RecordRanks ranks = racer.getRecordRanks();
                    String vehicleId = racer.getVehicle().getId();
                    ranks.lapRecordRank =
                            stats.addResult(
                                    TrackStats.ResultType.LAP,
                                    vehicleId,
                                    lapPositionComponent.getBestLapTime());
                    ranks.totalRecordRank =
                            stats.addResult(
                                    TrackStats.ResultType.TOTAL,
                                    vehicleId,
                                    lapPositionComponent.getTotalTime());
                }
            } else {
                float totalTime = 92.621f + (idx + 1) * 33.123f;
                lapPositionComponent.fakeCompletion(totalTime);
                entrant.addRaceTime(totalTime);
                if (entrant.isPlayer()) {
                    Racer.RecordRanks ranks = racer.getRecordRanks();
                    ranks.lapRecordRank = DEBUG_RECORD_RANKS.lapRecordRank;
                    ranks.totalRecordRank = DEBUG_RECORD_RANKS.totalRecordRank;
                }
            }
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
            Assert.check(
                    idx < positions.size, "Too many entrants (" + idx + "/" + positions.size + ")");
            GameInfo.Entrant entrant = entrants.get(idx);
            VehicleDef vehicleDef = assets.findVehicleDefById(entrant.getVehicleId());
            Vehicle vehicle = creator.create(vehicleDef, positions.get(idx), startAngle);
            Racer racer = new Racer(assets, audioManager, this, vehicle, entrant);
            if (entrant.isPlayer()) {
                GameInfo.Player player = (GameInfo.Player) entrant;
                PlayerPilot pilot =
                        new PlayerPilot(assets, this, racer, mGame.getConfig(), player.getIndex());
                racer.setPilot(pilot);
                mPlayerRacers.add(racer);
            } else {
                racer.setPilot(new AIPilot(this, mTrack, racer));
            }
            addGameObject(racer);
            mRacers.add(racer);
        }
    }

    private void setupObstacles() {
        ObstacleCreator creator = new ObstacleCreator();
        for (ObstacleDef def : mGame.getAssets().obstacleDefs) {
            creator.addObstacleDef(def);
        }

        for (MapObject object : mTrack.getObstacleObjects()) {
            creator.create(this, mGame.getAssets(), object);
        }

        TiledObstacleCreator.createObstacles(this, mTrack.getMap());
    }

    private void setupBonusSpots() {
        for (Vector2 pos : mTrack.findBonusSpotPositions()) {
            BonusSpot spot =
                    new BonusSpot(mGame.getAssets(), mGame.getAudioManager(), this, pos.x, pos.y);
            addGameObject(spot);
        }
    }

    private static <T extends Bonus> boolean isBonusClassAllowed(Class<T> bonusClass) {
        if (Constants.DEBUG_BONUSES == null) {
            return true;
        }
        String className = bonusClass.getSimpleName();
        for (String name : Constants.DEBUG_BONUSES) {
            if (className.equals(name)) {
                NLog.d("%s: true", className);
                return true;
            }
        }
        NLog.d("%s: false", className);
        return false;
    }

    private void setupBonusPools() {
        // Important: do not allow acceleration bonuses like the Turbo when ranked first, otherwise
        // getting a best score becomes too random.
        if (isBonusClassAllowed(GunBonus.class)) {
            addPool(GunBonus.class, new float[] {0.2f, 1.0f, 1.0f});
        }
        if (isBonusClassAllowed(MineBonus.class)) {
            addPool(MineBonus.class, new float[] {2.0f, 1.0f, 0.5f, 0f});
        }
        if (isBonusClassAllowed(TurboBonus.class)) {
            addPool(TurboBonus.class, new float[] {0f, 1.0f, 2.0f});
        }
        if (isBonusClassAllowed(MissileBonus.class)) {
            addPool(MissileBonus.class, new float[] {0.2f, 1.0f, 1.0f});
        }
    }

    private <T extends Bonus> void addPool(Class<T> bonusClass, float[] counts) {
        BonusPool<T> pool =
                new BonusPool<>(bonusClass, mGame.getAssets(), this, mGame.getAudioManager());
        pool.setCounts(counts);
        mBonusPools.add(pool);
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

    @Override
    public State getState() {
        return mState;
    }

    @Override
    public void startRace() {
        Assert.check(
                mState == GameWorld.State.COUNTDOWN,
                "startRace called while not in countdown state");
        setState(GameWorld.State.RUNNING);
    }

    @Override
    public void setState(State state) {
        if (mState == state) {
            return;
        }
        mState = state;
        if (mState == GameWorld.State.FINISHED) {
            onFinished();
        }
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

    void forgetTrack() {
        mTrack = null;
    }

    public static Racer.RecordRanks parseFinishedOverlayDebugScreen() {
        Scanner scanner = new Scanner(Constants.DEBUG_SCREEN);
        scanner.useDelimiter(":");
        if (!scanner.hasNext()) {
            return null;
        }
        if (!"FinishedOverlay".equals(scanner.next())) {
            return null;
        }
        Racer.RecordRanks ranks = new Racer.RecordRanks();
        if (scanner.hasNextInt()) {
            ranks.lapRecordRank = scanner.nextInt();
            if (scanner.hasNextInt()) {
                ranks.totalRecordRank = scanner.nextInt();
            }
        }
        return ranks;
    }
}
