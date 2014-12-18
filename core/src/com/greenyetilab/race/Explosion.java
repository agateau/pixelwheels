package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * Created by aurelien on 14/12/14.
 */
public class Explosion implements GameObject, Pool.Poolable, DisposableWhenOutOfSight {
    private static ReflectionPool<Explosion> sPool = new ReflectionPool<Explosion>(Explosion.class);
    private float mTime;
    private Animation mAnimation;
    private float mPosX;
    private float mPosY;

    @Override
    public void reset() {
    }

    @Override
    public void dispose() {
        sPool.free(this);
    }

    @Override
    public boolean act(float delta) {
        mTime += delta;
        boolean finished = mAnimation.isAnimationFinished(mTime);
        if (finished) {
            dispose();
        }
        return !finished;
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_OBSTACLES) {
            TextureRegion region = mAnimation.getKeyFrame(mTime);
            float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
            float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
            batch.draw(region, mPosX - w / 2, mPosY - h / 2 , w, h);
        }
    }

    @Override
    public float getX() {
        return mPosX;
    }

    @Override
    public float getY() {
        return mPosY;
    }

    public static Explosion create(Assets assets, float posX, float posY) {
        Explosion explosion = sPool.obtain();
        explosion.init(assets, posX, posY);
        return explosion;
    }

    private void init(Assets assets, float posX, float posY) {
        mTime = 0;
        mAnimation = assets.explosion;
        mPosX = posX;
        mPosY = posY;
    }
}
