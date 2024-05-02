/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.pixelwheels.racescreen;

import static com.agateau.pixelwheels.utils.BodyRegionDrawer.SHADOW_ALPHA;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.gameobject.AudioClipper;
import com.agateau.pixelwheels.gameobject.GameObjectAdapter;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.racer.HoleHandlerComponent;
import com.agateau.pixelwheels.racer.Vehicle;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.sound.SoundPlayer;
import com.agateau.pixelwheels.utils.OrientedPoint;
import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/** The rescue helicopter which comes to pick up fallen vehicles */
public class Helicopter extends GameObjectAdapter implements Pool.Poolable, Disposable {
    private static final float SHADOW_OFFSET = 80;
    private static final Vector2 BODY_CENTER = new Vector2(30, (111 - 35));
    private static final float PROPELLER_SPEED = -720;
    private static final float MAX_SPEED = 400 * Constants.UNIT_FOR_PIXEL;
    private static final float SLOW_DOWN_DISTANCE = 100 * Constants.UNIT_FOR_PIXEL;
    private static final float MIN_PITCH = 0.7f;
    private static final float MAX_PITCH = 1.3f;

    private static final float ANGLE_TOLERANCE = 4f;
    private static final float POSITION_TOLERANCE = 4 * Constants.UNIT_FOR_PIXEL;

    private enum State {
        ARRIVING,
        RECOVERING,
        LEAVING
    }

    private static final ReflectionPool<Helicopter> sPool = new ReflectionPool<>(Helicopter.class);

    private SoundPlayer mSoundPlayer;
    private FrameBuffer mFrameBuffer;
    private SpriteBatch mFrameBufferBatch;
    private TextureRegion mBodyRegion;
    private TextureRegion mPropellerRegion;
    private TextureRegion mPropellerTopRegion;
    private final Vector2 mPosition = new Vector2();
    private float mAngle;
    private final Vector2 mSpawnPosition = new Vector2();
    private final Vector2 mEndPosition = new Vector2();
    private float mEndAngle;
    private float mTime;
    private State mState;
    private float mFrameBufferRadiusU;

    private final Vector2 mTmpVec = new Vector2();

    private static void decideSpawnPosition(
            Assets assets, Track track, Helicopter helicopter, Vector2 vehiclePos) {
        float halfWidth = Constants.UNIT_FOR_PIXEL * assets.helicopterBody.getRegionWidth() / 2;
        float halfHeight = Constants.UNIT_FOR_PIXEL * assets.helicopterBody.getRegionHeight() / 2;
        float mapWidth = track.getMapWidth();
        float mapHeight = track.getMapHeight();

        float left = vehiclePos.x;
        float bottom = vehiclePos.y;
        float right = mapWidth - vehiclePos.x;
        float top = mapHeight - vehiclePos.y;

        // Start from left edge
        float startX = -halfWidth;
        float startY = vehiclePos.y;
        float distance = left;

        // Is right edge closer?
        if (distance > right) {
            startX = mapWidth + halfWidth;
            distance = right;
        }

        // Is bottom edge closer?
        if (distance > bottom) {
            startX = vehiclePos.x;
            startY = -halfHeight;
            distance = bottom;
        }

        // Is top edge closer?
        if (distance > top) {
            startX = vehiclePos.x;
            startY = mapHeight + halfHeight;
        }

        helicopter.mSpawnPosition.set(startX, startY);

        helicopter.mAngle =
                helicopter.mTmpVec.set(vehiclePos).sub(helicopter.mSpawnPosition).angleDeg();
        helicopter.mPosition.set(helicopter.mSpawnPosition);
    }

    public static Helicopter create(
            Assets assets,
            AudioManager audioManager,
            Track track,
            HoleHandlerComponent holeHandlerComponent) {
        Vehicle vehicle = holeHandlerComponent.getVehicle();
        Helicopter object = sPool.obtain();
        object.setFinished(false);

        if (object.mSoundPlayer == null) {
            object.mSoundPlayer =
                    audioManager.createSoundPlayer(assets.soundAtlas.get("helicopter"));
        }
        object.mBodyRegion = assets.helicopterBody;
        object.mPropellerRegion = assets.helicopterPropeller;
        object.mPropellerTopRegion = assets.helicopterPropellerTop;
        decideSpawnPosition(assets, track, object, vehicle.getPosition());

        object.mEndPosition.set(vehicle.getPosition());
        object.mEndAngle = vehicle.getAngle();

        object.mTime = 0;
        object.mState = State.ARRIVING;

        if (object.mFrameBuffer == null) {
            int bufferWidth = object.mPropellerRegion.getRegionWidth();
            int bufferHeight = object.mPropellerRegion.getRegionHeight() / 2 + (int) BODY_CENTER.y;
            object.mFrameBuffer =
                    new FrameBuffer(
                            Pixmap.Format.RGBA8888,
                            bufferWidth,
                            bufferHeight,
                            false /* hasDepth */);
            object.mFrameBufferBatch = new SpriteBatch();
            object.mFrameBufferBatch.setProjectionMatrix(
                    new Matrix4().setToOrtho2D(0, 0, bufferWidth, bufferHeight));

            object.mFrameBufferRadiusU =
                    Constants.UNIT_FOR_PIXEL
                            * (Vector2.len(bufferWidth / 2f, bufferHeight / 2f) + SHADOW_OFFSET);
        }

        return object;
    }

    @Override
    public void reset() {}

    @Override
    public void dispose() {
        sPool.free(this);
        if (mSoundPlayer != null) {
            mSoundPlayer.stop();
        }
    }

    public void setDestination(OrientedPoint point) {
        mEndPosition.set(point.x, point.y);
        mEndAngle = AgcMathUtils.normalizeAngle(point.angle);
    }

    public void setDestination(Vector2 position, float angle) {
        mEndPosition.set(position);
        mEndAngle = AgcMathUtils.normalizeAngle(angle);
    }

    public float getAngle() {
        return mAngle;
    }

    public boolean isAtDestination() {
        return mPosition.epsilonEquals(mEndPosition, POSITION_TOLERANCE)
                && AgcMathUtils.anglesAreEqual(mAngle, mEndAngle, ANGLE_TOLERANCE);
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
                actRecovering(delta);
                break;
            case LEAVING:
                actLeaving(delta);
                break;
        }
    }

    @Override
    public void audioRender(AudioClipper clipper) {
        mSoundPlayer.setVolume(clipper.clip(this));
        float pitch = MathUtils.random(MIN_PITCH, MAX_PITCH);
        mSoundPlayer.setPitch(pitch);
        if (!mSoundPlayer.isLooping()) {
            mSoundPlayer.loop();
        }
    }

    private void actArriving(float delta) {
        moveTowardDestination(delta);
    }

    public void switchToRecoveringState() {
        mState = State.RECOVERING;
    }

    private void actRecovering(float delta) {
        moveTowardDestination(delta);
    }

    public void leave() {
        mTime = 0;
        mState = State.LEAVING;

        mTmpVec.set(mSpawnPosition).sub(mPosition);
        float angle = mTmpVec.angleDeg();
        setDestination(mSpawnPosition, angle);
    }

    private void actLeaving(float delta) {
        if (isAtDestination()) {
            setFinished(true);
            mSoundPlayer.stop();
            return;
        }
        moveTowardDestination(delta);
    }

    private float mSpeed = MAX_SPEED;

    private void moveTowardDestination(float delta) {
        // Update position
        mTmpVec.set(mEndPosition).sub(mPosition);
        float distance = mTmpVec.len();
        float k = MathUtils.clamp(distance / SLOW_DOWN_DISTANCE, 0.01f, 1);
        float speed = mSpeed;
        if (distance > SLOW_DOWN_DISTANCE) {
            if (speed < MAX_SPEED) {
                speed += Math.min(MAX_SPEED, speed + MAX_SPEED / 10);
            }
        } else {
            speed = MAX_SPEED * k;
        }
        mSpeed = speed;

        mTmpVec.nor().scl(mSpeed * delta);
        mPosition.add(mTmpVec);

        // Update angle
        float targetAngle = distance > 2 * SLOW_DOWN_DISTANCE ? mTmpVec.angleDeg() : mEndAngle;
        float angleDistance = AgcMathUtils.shortestAngleDelta(mAngle, targetAngle);
        float angularSpeed = angleDistance / delta / 10f;
        mAngle = AgcMathUtils.normalizeAngle(mAngle + angularSpeed * delta);
    }

    @Override
    public void draw(Batch batch, ZLevel zLevel, Rectangle viewBounds) {
        if (!AgcMathUtils.rectangleContains(viewBounds, getPosition(), mFrameBufferRadiusU)) {
            return;
        }
        if (zLevel == ZLevel.FG_LAYERS) {
            float old = batch.getPackedColor();
            batch.setColor(0, 0, 0, SHADOW_ALPHA);
            float offset = SHADOW_OFFSET * Constants.UNIT_FOR_PIXEL;
            drawFrameBuffer(batch, offset);
            batch.setPackedColor(old);
        } else if (zLevel == ZLevel.FLYING_HIGH) {
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

        mFrameBufferBatch.draw(mBodyRegion, propellerW / 2 - BODY_CENTER.x, 0, w, h);

        mFrameBufferBatch.draw(
                mPropellerRegion,
                0,
                BODY_CENTER.y - propellerH / 2, // position
                propellerW / 2,
                propellerH / 2, // origin
                propellerW,
                propellerH, // size
                1,
                1, // scale
                (mTime * PROPELLER_SPEED) % 360);

        mFrameBufferBatch.draw(
                mPropellerTopRegion,
                propellerW / 2 - propellerTopW / 2,
                BODY_CENTER.y - propellerTopH / 2,
                propellerTopW,
                propellerTopH);

        mFrameBufferBatch.end();
        mFrameBuffer.end();
    }

    private void drawFrameBuffer(Batch batch, float offset) {
        final float U = Constants.UNIT_FOR_PIXEL;
        int w = mFrameBuffer.getWidth();
        int h = mFrameBuffer.getHeight();
        Texture texture = mFrameBuffer.getColorBufferTexture();
        batch.draw(
                texture,
                // dst
                getX() - w * U / 2 + offset,
                getY() - BODY_CENTER.y * U - offset,
                // origin
                w * U / 2,
                BODY_CENTER.y * U,
                // dst size
                w * U,
                h * U,
                // scale
                1,
                1,
                // rotation
                mAngle - 90,
                // src
                0,
                0,
                w,
                h,
                // flips
                false,
                true);
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
