package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.greenyetilab.utils.log.NLog;

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
    private final TextureRegion mGunRegion;
    private Racer mRacer;

    private final Timer.Task mTask = new Timer.Task() {
        @Override
        public void run() {
            Vehicle vehicle = mRacer.getVehicle();
            mGameWorld.addGameObject(Bullet.create(mAssets, mGameWorld, mRacer, vehicle.getX(), vehicle.getY(), vehicle.getAngle()));
            if (!isScheduled()) {
                GunBonus.this.remove();
            }
        }
    };

    private final Renderer mBonusRenderer = new Renderer() {
        @Override
        public void draw(Batch batch, int zIndex) {
            Vehicle vehicle = mRacer.getVehicle();
            Body body = vehicle.getBody();
            Vector2 center = body.getPosition();
            float angle = body.getAngle() * MathUtils.radiansToDegrees;
            float x = center.x;
            float y = center.y;
            float w = Constants.UNIT_FOR_PIXEL * mGunRegion.getRegionWidth();
            float h = Constants.UNIT_FOR_PIXEL * mGunRegion.getRegionHeight();
            batch.draw(mGunRegion,
                    x - w / 2, y - h / 2, // pos
                    w / 2, h / 2, // origin
                    w, h, // size
                    1, 1, // scale
                    angle);
        }
    };

    public GunBonus(Pool pool, Assets assets, GameWorld gameWorld) {
        mPool = pool;
        mAssets = assets;
        mGameWorld = gameWorld;
        mGunRegion = assets.gun;
    }

    @Override
    public void reset() {

    }

    @Override
    public TextureRegion getIconRegion() {
        return mAssets.findRegion("hud-fire");
    }

    @Override
    public void onPicked(Racer racer) {
        mRacer = racer;
        mRacer.getVehicleRenderer().addRenderer(mBonusRenderer);
    }

    @Override
    public void trigger() {
        Timer.schedule(mTask, 0, SHOOT_INTERVAL, SHOOT_COUNT);
    }

    private void remove() {
        mRacer.getVehicleRenderer().removeRenderer(mBonusRenderer);
        mPool.free(this);
    }
}
