package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;

/**
 * A gun bonus
 */
public class GunBonus implements Bonus, Pool.Poolable {
    private static final float SHOOT_INTERVAL = 0.1f;
    private static final int SHOOT_COUNT = 10;

    public static class Pool extends BonusPool {
        public Pool(Assets assets, GameWorld gameWorld) {
            super(assets, gameWorld);
        }

        @Override
        protected Bonus newObject() {
            return new GunBonus(this, mAssets, mGameWorld);
        }
    }

    private final Pool mPool;
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private Racer mRacer;

    private final Timer.Task mTask = new Timer.Task() {
        @Override
        public void run() {
            Vehicle vehicle = mRacer.getVehicle();
            mGameWorld.addGameObject(Bullet.create(mAssets, mGameWorld, mRacer, vehicle.getX(), vehicle.getY(), vehicle.getAngle()));
            if (!isScheduled()) {
                mPool.free(GunBonus.this);
            }
        }
    };

    public GunBonus(Pool pool, Assets assets, GameWorld gameWorld) {
        mPool = pool;
        mAssets = assets;
        mGameWorld = gameWorld;
    }

    @Override
    public void reset() {

    }

    @Override
    public TextureRegion getIconRegion() {
        return mAssets.findRegion("hud-fire");
    }

    @Override
    public void trigger(Racer racer) {
        mRacer = racer;
        Timer.schedule(mTask, 0, SHOOT_INTERVAL, SHOOT_COUNT);
    }
}
