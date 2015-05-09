package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * A generic short-animation game object
 */
public class AnimationObject extends GameObjectAdapter implements Pool.Poolable, Disposable {
    private static float MULTI_DELAY = 0.25f;
    private static float MULTI_DENSITY = 2;
    private static ReflectionPool<AnimationObject> sPool = new ReflectionPool<AnimationObject>(AnimationObject.class);
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
    public void act(float delta) {
        mTime += delta;
        if (mTime < 0) {
            return;
        }
        if (mAnimation.isAnimationFinished(mTime)) {
            setFinished(true);
        }
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (mTime < 0) {
            return;
        }
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

    @Override
    public HealthComponent getHealthComponent() {
        return null;
    }

    public static AnimationObject create(Animation animation, float posX, float posY) {
        return create(animation, posX, posY, 0);
    }
    public static AnimationObject create(Animation animation, float posX, float posY, float delay) {
        AnimationObject obj = sPool.obtain();
        obj.mTime = -delay;
        obj.mAnimation = animation;
        obj.mPosX = posX;
        obj.mPosY = posY;
        obj.setFinished(false);
        return obj;
    }

    public static void createMulti(GameWorld gameWorld, Animation animation, float posX, float posY, float width, float height) {
        int count = (int)(height * width / MULTI_DENSITY);
        float delay = 0;
        for (int i = 0; i < count; ++i, delay += MULTI_DELAY) {
            float dx = MathUtils.random(-width / 2, width / 2);
            float dy = MathUtils.random(-height / 2, height / 2);
            GameObject obj = AnimationObject.create(animation, posX + dx, posY + dy, delay);
            gameWorld.addGameObject(obj);
        }
    }
}
