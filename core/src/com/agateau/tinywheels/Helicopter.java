package com.agateau.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
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
    private static final Vector2 BODY_CENTER = new Vector2(30, (111 - 35));
    private static final float PROPELLER_SPEED = -720;

    private enum State {
        ARRIVING,
        RECOVERING,
        LEAVING
    }
    private static final ReflectionPool<Helicopter> sPool = new ReflectionPool<Helicopter>(Helicopter.class);

    private FrameBuffer mFrameBuffer;
    private SpriteBatch mFrameBufferBatch;
    private TextureRegion mBodyRegion;
    private TextureRegion mPropellerRegion;
    private TextureRegion mPropellerTopRegion;
    private final Vector2 mPosition = new Vector2();
    private final Vector2 mStartPosition = new Vector2();
    private final Vector2 mEndPosition = new Vector2();
    private final Vector2 mLeavePosition = new Vector2();
    private float mTime;
    private State mState;

    public static Helicopter create(Assets assets, MapInfo mapInfo, Vector2 vehiclePosition) {
        Helicopter object = sPool.obtain();
        object.setFinished(false);

        float height = Constants.UNIT_FOR_PIXEL * assets.helicopterBody.getRegionHeight();
        float mapHeight = mapInfo.getMapHeight() * mapInfo.getTileHeight();

        object.mBodyRegion = assets.helicopterBody;
        object.mPropellerRegion = assets.helicopterPropeller;
        object.mPropellerTopRegion = assets.helicopterPropellerTop;
        object.mPosition.set(vehiclePosition.x, -height);
        object.mStartPosition.set(object.mPosition);
        object.mEndPosition.set(vehiclePosition);
        object.mLeavePosition.set(vehiclePosition.x, mapHeight);
        object.mTime = 0;
        object.mState = State.ARRIVING;

        if (object.mFrameBuffer == null) {
            int bufferWidth = object.mPropellerRegion.getRegionWidth();
            int bufferHeight = object.mPropellerRegion.getRegionHeight() / 2 + (int)BODY_CENTER.y;
            object.mFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, bufferWidth, bufferHeight, false /* hasDepth */);
            object.mFrameBufferBatch = new SpriteBatch();
            object.mFrameBufferBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, bufferWidth, bufferHeight));
        }

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
            batch.end();
            updateFrameBuffer();
            batch.begin();
            Color old = batch.getColor();
            batch.setColor(0, 0, 0, SHADOW_ALPHA);
            float offset = SHADOW_OFFSET * Constants.UNIT_FOR_PIXEL;
            drawFrameBuffer(batch, offset);
            batch.setColor(old);
        } else if (zIndex == Constants.Z_FLYING) {
            drawFrameBuffer(batch, 0);
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void updateFrameBuffer() {
        final float w = mBodyRegion.getRegionWidth();
        final float h = mBodyRegion.getRegionHeight();

        float propellerW = mPropellerRegion.getRegionWidth();
        float propellerH = mPropellerRegion.getRegionHeight();

        float propellerTopW = mPropellerTopRegion.getRegionWidth();
        float propellerTopH = mPropellerTopRegion.getRegionHeight();

        mFrameBuffer.begin();
        mFrameBufferBatch.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mFrameBufferBatch.draw(
                mBodyRegion,
                propellerW / 2 - BODY_CENTER.x, 0,
                w, h);

        mFrameBufferBatch.draw(
                mPropellerRegion,
                0, BODY_CENTER.y - propellerH / 2, // position
                propellerW / 2, propellerH / 2, // origin
                propellerW, propellerH, // size
                1, 1, // scale
                (mTime * PROPELLER_SPEED) % 360);

        mFrameBufferBatch.draw(
                mPropellerTopRegion,
                propellerW / 2 - propellerTopW / 2, BODY_CENTER.y - propellerTopH / 2,
                propellerTopW, propellerTopH);

        mFrameBufferBatch.end();
        mFrameBuffer.end();
    }

    private void drawFrameBuffer(Batch batch, float offset) {
        final float U = Constants.UNIT_FOR_PIXEL;
        int w = mFrameBuffer.getWidth();
        int h = mFrameBuffer.getHeight();
        Texture texture = mFrameBuffer.getColorBufferTexture();
        batch.draw(texture,
                // dst
                getX() - w * U / 2 + offset, getY() - BODY_CENTER.y * U - offset, w * U, h * U,
                // src
                0, 0, w, h,
                // flips
                false, true);
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
