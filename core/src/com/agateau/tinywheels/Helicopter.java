/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
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

import static com.agateau.tinywheels.BodyRegionDrawer.SHADOW_ALPHA;

/**
 * The rescue helicopter which comes to pick up fallen vehicles
 */

public class Helicopter extends GameObjectAdapter implements Pool.Poolable, Disposable {
    private static final float SHADOW_OFFSET = 80;
    private static final Vector2 BODY_CENTER = new Vector2(30, (111 - 35));
    private static final float PROPELLER_SPEED = -720;
    private static final float MAX_SPEED = 200 * 3.6f;
    private static final float MIN_SPEED = 100 * 3.6f;
    private static final float SLOW_DOWN_DISTANCE = 200 * Constants.UNIT_FOR_PIXEL;

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
    private float mAngle;
    private final Vector2 mStartPosition = new Vector2();
    private float mStartAngle;
    private final Vector2 mEndPosition = new Vector2();
    private float mEndAngle;
    private final Vector2 mLeavePosition = new Vector2();
    private float mTime;
    private State mState;

    public static Helicopter create(Assets assets, MapInfo mapInfo, Vector2 vehiclePosition, float vehicleAngle) {
        Helicopter object = sPool.obtain();
        object.setFinished(false);

        float height = Constants.UNIT_FOR_PIXEL * assets.helicopterBody.getRegionHeight();
        float mapHeight = mapInfo.getMapHeight() * mapInfo.getTileHeight();

        object.mBodyRegion = assets.helicopterBody;
        object.mPropellerRegion = assets.helicopterPropeller;
        object.mPropellerTopRegion = assets.helicopterPropellerTop;
        object.mPosition.set(vehiclePosition.x, -height);
        object.mAngle = 0;
        object.mStartPosition.set(object.mPosition);
        object.mStartAngle = 90;
        object.mEndPosition.set(vehiclePosition);
        object.mEndAngle = vehicleAngle;
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
        mEndAngle = 90;
    }

    public void setEndPosition(Vector2 position) {
        mEndPosition.set(position);
    }

    public void setPosition(Vector2 position) {
        mPosition.set(position);
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    @Override
    public void act(float delta) {
        mTime += delta;
        updateFrameBuffer();
        switch (mState) {
        case ARRIVING:
            actArriving(delta);
            break;
        case RECOVERING:
            break;
        case LEAVING:
            actLeaving(delta);
            break;
        }
    }

    private void actArriving(float delta) {
        if (mPosition.epsilonEquals(mEndPosition, 2 * Constants.UNIT_FOR_PIXEL)) {
            mState = State.RECOVERING;
            return;
        }
        moveTowardEndPosition(delta);
    }

    private void actLeaving(float delta) {
        if (mPosition.epsilonEquals(mEndPosition, 2 * Constants.UNIT_FOR_PIXEL)) {
            setFinished(true);
            return;
        }
        moveTowardEndPosition(delta);
    }

    private Vector2 mDelta = new Vector2();
    private void moveTowardEndPosition(float delta) {
        mDelta.set(mEndPosition).sub(mPosition);
        float speed;
        float distance = mDelta.len();
        float progress = 0;
        if (distance > SLOW_DOWN_DISTANCE) {
            speed = MAX_SPEED;
        } else {
            progress = 1 - distance / SLOW_DOWN_DISTANCE;
            speed = MathUtils.lerp(MAX_SPEED, MIN_SPEED, progress);
        }
        mDelta.limit(delta * speed * Constants.UNIT_FOR_PIXEL);
        mPosition.add(mDelta);
        mAngle = MathUtils.lerp(mStartAngle, mEndAngle, progress);
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_SHADOWS) {
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
                getX() - w * U / 2 + offset, getY() - BODY_CENTER.y * U - offset,
                // origin
                w * U / 2, BODY_CENTER.y * U,
                // dst size
                w * U, h * U,
                // scale
                1, 1,
                // rotation
                mAngle - 90,
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
