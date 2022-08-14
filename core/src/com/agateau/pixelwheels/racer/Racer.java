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
package com.agateau.pixelwheels.racer;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.bonus.Bonus;
import com.agateau.pixelwheels.bonus.BonusPool;
import com.agateau.pixelwheels.debug.Debug;
import com.agateau.pixelwheels.gameobjet.AudioClipper;
import com.agateau.pixelwheels.gameobjet.CellFrameBufferManager;
import com.agateau.pixelwheels.gameobjet.CellFrameBufferUser;
import com.agateau.pixelwheels.gameobjet.GameObjectAdapter;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.racescreen.Collidable;
import com.agateau.pixelwheels.racescreen.CollisionCategories;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.stats.GameStats;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/** A racer */
public class Racer extends GameObjectAdapter
        implements Collidable, Disposable, CellFrameBufferUser {
    private final GameWorld mGameWorld;
    private final Vehicle mVehicle;
    private final VehicleRenderer mVehicleRenderer;
    private final HoleHandlerComponent mHoleHandlerComponent;
    private final SpinningComponent mSpinningComponent;
    private final LapPositionComponent mLapPositionComponent;
    private final AudioComponent mAudioComponent;
    private final Array<Component> mComponents = new Array<>();
    private final Array<Collidable> mCollidableComponents = new Array<>();
    private final GameInfo.Entrant mEntrant;

    private Pilot mPilot;

    // State
    private Bonus mBonus;
    private final RecordRanks mRecordRanks = new RecordRanks();

    @Override
    public void init(CellFrameBufferManager manager) {
        mVehicleRenderer.init(manager);
    }

    @Override
    public void drawToCell(Batch batch, Rectangle viewBounds) {
        mVehicleRenderer.drawToCell(batch, viewBounds);
    }

    public static class RecordRanks {
        public int lapRecordRank = -1;
        public int totalRecordRank = -1;

        public boolean brokeRecord() {
            return lapRecordRank > -1 || totalRecordRank > -1;
        }
    }

    interface Component {
        void act(float delta);
    }

    private class PilotSupervisorComponent implements Component {
        @Override
        public void act(float delta) {
            if (mLapPositionComponent.hasFinishedRace()
                    || mSpinningComponent.isActive()
                    || mHoleHandlerComponent.getState() != HoleHandlerComponent.State.NORMAL) {
                mVehicle.setAccelerating(false);
                mVehicle.setBraking(false);
            } else {
                mPilot.act(delta);
            }
        }
    }

    public Racer(
            Assets assets,
            AudioManager audioManager,
            GameWorld gameWorld,
            Vehicle vehicle,
            GameInfo.Entrant entrant) {
        mGameWorld = gameWorld;
        mLapPositionComponent = new LapPositionComponent(gameWorld.getTrack(), vehicle);
        mSpinningComponent = new SpinningComponent(vehicle);

        mVehicle = vehicle;
        mVehicle.setRacer(this);
        mVehicle.setCollisionInfo(
                CollisionCategories.RACER,
                CollisionCategories.WALL
                        | CollisionCategories.RACER
                        | CollisionCategories.RACER_BULLET
                        | CollisionCategories.EXPLOSABLE);

        mEntrant = entrant;

        mVehicleRenderer = new VehicleRenderer(assets, mVehicle);
        mHoleHandlerComponent =
                new HoleHandlerComponent(assets, mGameWorld, this, mLapPositionComponent);

        PilotSupervisorComponent supervisorComponent = new PilotSupervisorComponent();

        mAudioComponent = new AudioComponent(assets.soundAtlas, audioManager, this);

        addComponent(mLapPositionComponent);
        addComponent(mVehicle);
        addComponent(mHoleHandlerComponent);
        addComponent(mSpinningComponent);
        addComponent(supervisorComponent);
        addComponent(new BonusSpotHitComponent(this));
        addComponent(mAudioComponent);

        if (Debug.instance.createSpeedReport) {
            Probe probe = new Probe("speed.dat");
            mVehicle.setProbe(probe);
            addComponent(probe);
        }
    }

    private void addComponent(Component component) {
        mComponents.add(component);
        if (component instanceof Collidable) {
            mCollidableComponents.add((Collidable) component);
        }
    }

    public RecordRanks getRecordRanks() {
        return mRecordRanks;
    }

    public GameInfo.Entrant getEntrant() {
        return mEntrant;
    }

    public Pilot getPilot() {
        return mPilot;
    }

    public void setPilot(Pilot pilot) {
        mPilot = pilot;
    }

    public Vehicle getVehicle() {
        return mVehicle;
    }

    public Bonus getBonus() {
        return mBonus;
    }

    public LapPositionComponent getLapPositionComponent() {
        return mLapPositionComponent;
    }

    public AudioComponent getAudioComponent() {
        return mAudioComponent;
    }

    public AudioManager getAudioManager() {
        return mAudioComponent.getAudioManager();
    }

    public void spin() {
        if (mSpinningComponent.isActive()) {
            return;
        }
        mSpinningComponent.start();
        looseBonus();
    }

    /**
     * Returns the angle the camera should use to follow the vehicle.
     *
     * <p>This is the same as Vehicle.getAngle() except when spinning, in which case we return the
     * original angle, to avoid too much camera shaking, especially when "rotate screen" option is
     * off.
     */
    public float getCameraAngle() {
        if (mSpinningComponent.isActive()) {
            return mSpinningComponent.getOriginalAngle();
        } else {
            return mVehicle.getAngle();
        }
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        for (Collidable collidable : mCollidableComponents) {
            collidable.beginContact(contact, otherFixture);
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {
        for (Collidable collidable : mCollidableComponents) {
            collidable.endContact(contact, otherFixture);
        }
    }

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {
        Object other = otherFixture.getBody().getUserData();
        if (other instanceof Racer) {
            contact.setEnabled(false);
            applySimplifiedRacerCollision((Racer) other);
        }

        for (Collidable collidable : mCollidableComponents) {
            collidable.preSolve(contact, otherFixture, oldManifold);
        }
    }

    /**
     * Simplifies collisions between vehicles to make the game easier to play: bump them but do not
     * change their direction
     */
    private final Vector2 mTmp = new Vector2();

    private void applySimplifiedRacerCollision(Racer other) {
        Body body1 = getVehicle().getBody();
        Body body2 = other.getVehicle().getBody();

        mTmp.set(body2.getLinearVelocity()).sub(body1.getLinearVelocity());
        float deltaV = mTmp.len();

        final float k =
                GamePlay.instance.simplifiedCollisionKFactor
                        * MathUtils.clamp(
                                deltaV / GamePlay.instance.simplifiedCollisionMaxDeltaV, 0, 1);
        mTmp.set(body2.getWorldCenter()).sub(body1.getWorldCenter()).nor().scl(k);

        body2.applyLinearImpulse(mTmp, body2.getWorldCenter(), true);
        mTmp.scl(-1);
        body1.applyLinearImpulse(mTmp, body1.getWorldCenter(), true);
    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {
        for (Collidable collidable : mCollidableComponents) {
            collidable.postSolve(contact, otherFixture, impulse);
        }
    }

    @Override
    public void dispose() {
        for (Racer.Component component : mComponents) {
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        }
    }

    @Override
    public void act(float delta) {
        for (Racer.Component component : mComponents) {
            component.act(delta);
        }

        if (mBonus != null) {
            mBonus.act(delta);
        }
    }

    @SuppressWarnings("rawtypes")
    public void selectBonus() {
        float normalizedRank = mGameWorld.getRacerNormalizedRank(this);

        Array<BonusPool> pools = mGameWorld.getBonusPools();
        float totalCount = 0;
        for (BonusPool pool : pools) {
            totalCount += pool.getCountForNormalizedRank(normalizedRank);
        }

        // To avoid allocating an array of the counts for each normalized rank, we subtract counts
        // from pick, until it is less than 0, at this point we are on the selected pool
        float pick = MathUtils.random(0f, totalCount);
        BonusPool pool = null;
        for (int idx = 0; idx < pools.size; ++idx) {
            pool = pools.get(idx);
            pick -= pool.getCountForNormalizedRank(normalizedRank);
            if (pick < 0) {
                break;
            }
        }
        if (pool == null) {
            pool = pools.get(pools.size - 1);
        }

        mBonus = (Bonus) pool.obtain();
        mBonus.onPicked(this);
        getGameStats().recordEvent(GameStats.Event.PICKED_BONUS);
    }

    public void triggerBonus() {
        if (mBonus == null) {
            return;
        }
        mBonus.trigger();
    }

    /** Called by bonuses when they are done */
    public void resetBonus() {
        mBonus = null;
    }

    /** Called when something bad happens to the racer, causing her to loose her bonus */
    public void looseBonus() {
        if (mBonus != null) {
            mBonus.onOwnerHit();
        }
    }

    @Override
    public void draw(Batch batch, ZLevel zLevel, Rectangle viewBounds) {
        mVehicleRenderer.draw(batch, zLevel, viewBounds);
    }

    @Override
    public void audioRender(AudioClipper clipper) {
        mAudioComponent.render(clipper);
    }

    @Override
    public float getX() {
        return mVehicle.getX();
    }

    @Override
    public float getY() {
        return mVehicle.getY();
    }

    public VehicleRenderer getVehicleRenderer() {
        return mVehicleRenderer;
    }

    public void markRaceFinished() {
        mLapPositionComponent.markRaceFinished();
    }

    @Override
    public String toString() {
        return "<racer pilot=" + mPilot + " vehicle=" + mVehicle + ">";
    }

    public GameStats getGameStats() {
        return mPilot.getGameStats();
    }
}
