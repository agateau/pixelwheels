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

import com.agateau.tinywheels.gameobjet.GameObjectAdapter;
import com.agateau.tinywheels.gameobjet.AudioClipper;
import com.agateau.tinywheels.sound.AudioManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * A racer
 */
public class Racer extends GameObjectAdapter implements Collidable, Disposable {
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final Vehicle mVehicle;
    private final VehicleRenderer mVehicleRenderer;
    private final GroundCollisionHandlerComponent mGroundCollisionHandlerComponent;
    private final SpinningComponent mSpinningComponent;
    private final LapPositionComponent mLapPositionComponent;
    private final AudioComponent mAudioComponent;
    private final Array<Component> mComponents = new Array<Component>();
    private final Array<Collidable> mCollidableComponents = new Array<Collidable>();

    private Pilot mPilot;

    // State
    private Bonus mBonus;

    interface Component {
        void act(float delta);
    }

    private class PilotSupervisorComponent implements Component {
        @Override
        public void act(float delta) {
            if (mLapPositionComponent.hasFinishedRace() || mSpinningComponent.isActive()
                    || mGroundCollisionHandlerComponent.getState() != GroundCollisionHandlerComponent.State.NORMAL) {
                mVehicle.setAccelerating(false);
            } else {
                mPilot.act(delta);
            }
        }
    }

    public Racer(Assets assets, AudioManager audioManager, GameWorld gameWorld, Vehicle vehicle) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mLapPositionComponent = new LapPositionComponent(gameWorld.getMapInfo(), vehicle);
        mSpinningComponent = new SpinningComponent(vehicle);

        mVehicle = vehicle;
        mVehicle.setRacer(this);
        mVehicle.setCollisionInfo(CollisionCategories.RACER,
                CollisionCategories.WALL
                | CollisionCategories.RACER | CollisionCategories.RACER_BULLET
                | CollisionCategories.FLAT_OBJECT);

        mVehicleRenderer = new VehicleRenderer(assets, mVehicle);
        mGroundCollisionHandlerComponent = new GroundCollisionHandlerComponent(
                assets,
                mGameWorld,
                this,
                mLapPositionComponent);

        PilotSupervisorComponent supervisorComponent = new PilotSupervisorComponent();

        mAudioComponent = new AudioComponent(mAssets.soundAtlas, audioManager, this);

        addComponent(mLapPositionComponent);
        addComponent(mVehicle);
        addComponent(mGroundCollisionHandlerComponent);
        addComponent(mSpinningComponent);
        addComponent(supervisorComponent);
        addComponent(new BonusSpotHitComponent(this));
        addComponent(mAudioComponent);

        if (GamePlay.instance.createSpeedReport) {
            Probe probe = new Probe("speed.dat");
            mVehicle.setProbe(probe);
            addComponent(probe);
        }
    }

    private void addComponent(Component component) {
        mComponents.add(component);
        if (component instanceof Collidable) {
            mCollidableComponents.add((Collidable)component);
        }
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

    public void spin() {
        if (mSpinningComponent.isActive()) {
            return;
        }
        mSpinningComponent.start();
        looseBonus();
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
        mAudioComponent.onCollision();
        if (other instanceof Racer) {
            contact.setEnabled(false);
            Racer racer2 = (Racer)other;
            Body body1 = getVehicle().getBody();
            Body body2 = racer2.getVehicle().getBody();
            float x1 = body1.getWorldCenter().x;
            float y1 = body1.getWorldCenter().y;
            float x2 = body2.getWorldCenter().x;
            float y2 = body2.getWorldCenter().y;
            final float k = 4;
            body1.applyLinearImpulse(k * (x1 - x2), k * (y1 - y2), x1, y1, true);
            body2.applyLinearImpulse(k * (x2 - x1), k * (y2 - y1), x2, y2, true);
        }

        for (Collidable collidable : mCollidableComponents) {
            collidable.preSolve(contact, otherFixture, oldManifold);
        }
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

        mBonus = pool.obtain();
        mBonus.onPicked(this);
    }

    public void triggerBonus() {
        if (mBonus == null) {
            return;
        }
        mBonus.trigger();
    }

    /**
     * Called by bonuses when they are done
     */
    public void resetBonus() {
        mBonus = null;
    }

    /**
     * Called when something bad happens to the racer, causing her to loose her bonus
     */
    public void looseBonus() {
        if (mBonus != null) {
            mBonus.onOwnerHit();
        }
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        mVehicleRenderer.draw(batch, zIndex);
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
}
