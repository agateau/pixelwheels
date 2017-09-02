package com.agateau.tinywheels;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

import static com.agateau.tinywheels.DrawUtils.SHADOW_ALPHA;

/**
 * The rescue helicopter which comes to pick up fallen vehicles
 */

public class Helicopter extends GameObjectAdapter implements Pool.Poolable, Disposable {
    private static final float ARRIVING_DURATION = 2;
    private static final float SHADOW_OFFSET = 80;

    private enum State {
        ARRIVING,
        RECOVERING,
        LEAVING
    }
    private static final ReflectionPool<Helicopter> sPool = new ReflectionPool<Helicopter>(Helicopter.class);

    private Animation mAnimation;
    private final Vector2 mPosition = new Vector2();
    private final Vector2 mStartPosition = new Vector2();
    private final Vector2 mEndPosition = new Vector2();
    private final Vector2 mLeavePosition = new Vector2();
    private float mTime;
    private State mState;

    public static Helicopter create(Assets assets, MapInfo mapInfo, Vector2 vehiclePosition) {
        Helicopter object = sPool.obtain();
        object.setFinished(false);

        float height = Constants.UNIT_FOR_PIXEL * assets.helicopter.getKeyFrame(0).getRegionHeight();
        float mapHeight = mapInfo.getMapHeight() * mapInfo.getTileHeight();

        object.mAnimation = assets.helicopter;
        object.mPosition.set(vehiclePosition.x, -height);
        object.mStartPosition.set(object.mPosition);
        object.mEndPosition.set(vehiclePosition);
        object.mLeavePosition.set(vehiclePosition.x, mapHeight);
        object.mTime = 0;
        object.mState = State.ARRIVING;

        return object;
    }

    @Override
    public void reset() {

    }

    @Override
    public void dispose() {
        sPool.free(this);
    }

    public boolean isReadyToRecover() {
        return mState == State.RECOVERING;
    }

    public void leave() {
        mTime = 0;
        mState = State.LEAVING;
        mStartPosition.set(mPosition);
        mEndPosition.set(mLeavePosition);
    }

    public void setEndPosition(Vector2 position) {
        mEndPosition.set(position);
    }

    public void setPosition(Vector2 position) {
        mPosition.set(position);
    }

    @Override
    public void act(float delta) {
        mTime += delta;
        switch (mState) {
        case ARRIVING:
            actArriving();
            break;
        case RECOVERING:
            break;
        case LEAVING:
            actLeaving();
            break;
        }
    }

    private void actArriving() {
        if (mTime > ARRIVING_DURATION) {
            mState = State.RECOVERING;
            return;
        }
        float progress = mTime / ARRIVING_DURATION;
        mPosition.x = MathUtils.lerp(mStartPosition.x, mEndPosition.x, progress);
        mPosition.y = MathUtils.lerp(mStartPosition.y, mEndPosition.y, progress);
    }

    private void actLeaving() {
        if (mTime > ARRIVING_DURATION) {
            setFinished(true);
            return;
        }
        float progress = mTime / ARRIVING_DURATION;
        mPosition.x = MathUtils.lerp(mStartPosition.x, mEndPosition.x, progress);
        mPosition.y = MathUtils.lerp(mStartPosition.y, mEndPosition.y, progress);
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_SHADOWS) {
            TextureRegion region = mAnimation.getKeyFrame(mTime);
            float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
            float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();

            Color old = batch.getColor();
            batch.setColor(0, 0, 0, SHADOW_ALPHA);
            float offset = SHADOW_OFFSET * Constants.UNIT_FOR_PIXEL;
            batch.draw(region, mPosition.x - w / 2 + offset, mPosition.y - h / 2 + offset, w, h);
            batch.setColor(old);

        } else if (zIndex == Constants.Z_FLYING) {
            TextureRegion region = mAnimation.getKeyFrame(mTime);
            float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
            float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
            batch.draw(region, mPosition.x - w / 2, mPosition.y - h / 2, w, h);
        }
    }

    @Override
    public float getX() {
        return mPosition.x;
    }

    @Override
    public float getY() {
        return mPosition.y;
    }
}
