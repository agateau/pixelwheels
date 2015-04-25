package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.greenyetilab.utils.log.NLog;

/**
 * A racer
 */
public class Racer extends GameObjectAdapter implements Collidable, Disposable {
    private final GameWorld mGameWorld;
    private final Vehicle mVehicle;
    private final VehicleRenderer mVehicleRenderer;
    private final HealthComponent mHealthComponent = new HealthComponent();
    private final GroundCollisionHandlerComponent mGroundCollisionHandlerComponent;

    private Pilot mPilot;
    private int mLapCount = 0;
    private final LapPosition mLapPosition = new LapPosition();
    private boolean mHasFinishedRace = false;
    private Bonus mBonus;
    private boolean mMustSelectBonus = false;

    public Racer(GameWorld gameWorld, Vehicle vehicle) {
        mGameWorld = gameWorld;
        mHealthComponent.setInitialHealth(Constants.PLAYER_HEALTH);

        mVehicle = vehicle;
        mVehicle.setUserData(this);
        mVehicle.setCollisionInfo(CollisionCategories.RACER,
                CollisionCategories.WALL
                | CollisionCategories.RACER | CollisionCategories.RACER_BULLET
                | CollisionCategories.FLAT_OBJECT);

        mVehicleRenderer = new VehicleRenderer(mVehicle, mHealthComponent);
        mGroundCollisionHandlerComponent = new GroundCollisionHandlerComponent(mVehicle, mHealthComponent);
    }

    public void setPilot(Pilot pilot) {
        mPilot = pilot;
    }

    public Vehicle getVehicle() {
        return mVehicle;
    }

    public int getLapCount() {
        return mLapCount;
    }

    public float getLapDistance() {
        return mLapPosition.getLapDistance();
    }

    public boolean hasFinishedRace() {
        return mHasFinishedRace;
    }

    public Bonus getBonus() {
        return mBonus;
    }

    public void spin() {
        mVehicle.getBody().applyAngularImpulse(GamePlay.spinImpulse, true);
        for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
            info.wheel.disableGripFor(GamePlay.spinDuration / 10f);
        }
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        Object other = otherFixture.getBody().getUserData();
        if (other instanceof BonusSpot) {
            BonusSpot spot = (BonusSpot)other;
            spot.pickBonus();
            if (mBonus == null) {
                // Do not call selectBonus() from here: it would make it harder for bonus code to
                // create Box2D bodies: since we are in the collision handling code, the physic
                // engine is locked so they would have to delay such creations.
                mMustSelectBonus = true;
            }
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {
    }

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {
    }

    @Override
    public void dispose() {
        mVehicle.dispose();
    }

    @Override
    public void act(float delta) {
        if (mMustSelectBonus) {
            mMustSelectBonus = false;
            selectBonus();
        }
        if (!mHasFinishedRace) {
            updatePosition();
        }
        mVehicle.act(delta);
        if (mHasFinishedRace) {
            mVehicle.setAccelerating(false);
        } else {
            mPilot.act(delta);
        }
        mGroundCollisionHandlerComponent.act(delta);
        if (!mHealthComponent.act(delta)) {
            setFinished(true);
        }
    }

    private void selectBonus() {
        Array<BonusPool> pools = mGameWorld.getBonusPools();
        int idx = MathUtils.random(pools.size - 1);
        BonusPool pool = pools.get(idx);
        mBonus = pool.obtain();
        mBonus.onPicked(this);
    }

    public void triggerBonus() {
        if (mBonus == null) {
            return;
        }
        mBonus.trigger();
        mBonus = null;
    }

    /**
     * Can be called by a bonus when some event caused it to trigger itself
     */
    public void resetBonus() {
        mBonus = null;
    }

    private void updatePosition() {
        final int oldSectionId = mLapPosition.getSectionId();
        final MapInfo mapInfo = mGameWorld.getMapInfo();
        final float PFU = 1 / Constants.UNIT_FOR_PIXEL;
        final int pixelX = (int)(PFU * mVehicle.getX());
        final int pixelY = (int)(PFU * mVehicle.getY());
        final LapPosition pos = mapInfo.getLapPositionTable().get(pixelX, pixelY);
        if (pos == null) {
            NLog.e("No LapPosition at pixel " + pixelX + " x " + pixelY);
            return;
        }
        mLapPosition.copy(pos);
        if (mLapPosition.getSectionId() == 0 && oldSectionId > 1) {
            ++mLapCount;
            if (mLapCount > mapInfo.getTotalLapCount()) {
                --mLapCount;
                mHasFinishedRace = true;
            }
        } else if (mLapPosition.getSectionId() > 1 && oldSectionId == 0) {
            --mLapCount;
        }
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        mVehicleRenderer.draw(batch, zIndex);
    }

    @Override
    public float getX() {
        return mVehicle.getX();
    }

    @Override
    public float getY() {
        return mVehicle.getY();
    }

    @Override
    public HealthComponent getHealthComponent() {
        return mHealthComponent;
    }

    public VehicleRenderer getVehicleRenderer() {
        return mVehicleRenderer;
    }
}
