package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

/**
 * A gun bonus
 */
public class GunBonus implements Bonus, Pool.Poolable {
    public static class Pool extends BonusPool {
        public Pool(Assets assets, GameWorld gameWorld) {
            super(assets, gameWorld);
        }

        @Override
        protected Bonus newObject() {
            return new GunBonus(mAssets, mGameWorld);
        }
    }

    private final Assets mAssets;
    private final GameWorld mGameWorld;

    public GunBonus(Assets assets, GameWorld gameWorld) {
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
        Vehicle vehicle = racer.getVehicle();
        mGameWorld.addGameObject(Bullet.create(mAssets, mGameWorld, racer, vehicle.getX(), vehicle.getY(), vehicle.getAngle()));
    }
}
