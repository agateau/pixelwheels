/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
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
package com.agateau.pixelwheels.racescreen;

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.debug.Debug;
import com.agateau.pixelwheels.debug.DebugShapeMap;
import com.agateau.pixelwheels.gameobjet.GameObject;
import com.agateau.pixelwheels.map.MapUtils;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racer.Vehicle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;

/**
 * Responsible for rendering the game world
 */
public class GameRenderer {
    private static final float MAX_CAMERA_DELTA = 50;
    private static final float MAX_ZOOM_DELTA = 0.4f;
    private static final float MIN_ZOOM = 0.6f;
    private static final float MAX_ZOOM = 2.1f;
    private static final float MAX_ZOOM_SPEED = 75f;
    private static final float IMMEDIATE = -1;

    private final Track mTrack;
    private final OrthogonalTiledMapRenderer mRenderer;
    private final Box2DDebugRenderer mDebugRenderer;
    private final Batch mBatch;
    private final OrthographicCamera mCamera;
    private final ShapeRenderer mShapeRenderer = new ShapeRenderer();
    private final GameWorld mWorld;
    private final Racer mRacer;
    private final float mMapWidth;
    private final float mMapHeight;

    private int[] mBackgroundLayerFirstIndexes = { 0 };
    private int[] mExtraBackgroundLayerIndexes;
    private int[] mForegroundLayerIndexes;
    private Vehicle mVehicle;

    private int mScreenX;
    private int mScreenY;
    private int mScreenWidth;
    private int mScreenHeight;
    private PerformanceCounter mTilePerformanceCounter;
    private PerformanceCounter mGameObjectPerformanceCounter;

    private static class CameraInfo {
        float viewportWidth;
        float viewportHeight;
        Vector2 position = new Vector2();
        float zoom = 1;
    }
    private CameraInfo mCameraInfo = new CameraInfo();
    private CameraInfo mNextCameraInfo = new CameraInfo();

    public GameRenderer(GameWorld world, Racer racer, Batch batch, PerformanceCounters counters) {
        mDebugRenderer = new Box2DDebugRenderer();
        mWorld = world;
        mRacer = racer;
        mVehicle = racer.getVehicle();

        mTrack = mWorld.getTrack();
        mMapWidth = mTrack.getMapWidth();
        mMapHeight = mTrack.getMapHeight();

        mExtraBackgroundLayerIndexes = mTrack.getExtraBackgroundLayerIndexes();
        mForegroundLayerIndexes = mTrack.getForegroundLayerIndexes();

        mBatch = batch;
        mCamera = new OrthographicCamera();
        mRenderer = new OrthogonalTiledMapRenderer(mTrack.getMap(), Constants.UNIT_FOR_PIXEL, mBatch);

        mTilePerformanceCounter = counters.add("- tiles");
        mGameObjectPerformanceCounter = counters.add("- g.o.");

        mDebugRenderer.setDrawVelocities(Debug.instance.drawVelocities);
    }

    public void setScreenRect(int x, int y, int width, int height) {
        mScreenX = x;
        mScreenY = y;
        mScreenWidth = width;
        mScreenHeight = height;
    }

    public void onAboutToStart() {
        updateCamera(IMMEDIATE);
    }

    public void render(float delta) {
        Gdx.gl.glViewport(mScreenX, mScreenY, mScreenWidth, mScreenHeight);
        updateCamera(delta);
        updateMapRendererCamera();

        mTilePerformanceCounter.start();
        mBatch.disableBlending();
        mRenderer.render(mBackgroundLayerFirstIndexes);
        mBatch.enableBlending();
        if (mExtraBackgroundLayerIndexes.length > 0) {
            mRenderer.render(mExtraBackgroundLayerIndexes);
        }
        mTilePerformanceCounter.stop();

        mGameObjectPerformanceCounter.start();
        mBatch.begin();
        for (ZLevel z : ZLevel.values()) {
            for (GameObject object : mWorld.getActiveGameObjects()) {
                object.draw(mBatch, z);
            }

            if (z == ZLevel.OBSTACLES && mForegroundLayerIndexes.length > 0) {
                mGameObjectPerformanceCounter.stop();
                mTilePerformanceCounter.start();

                mBatch.end();
                mRenderer.render(mForegroundLayerIndexes);
                mBatch.begin();

                mTilePerformanceCounter.stop();
                mGameObjectPerformanceCounter.start();
            }
        }
        mGameObjectPerformanceCounter.stop();
        mBatch.end();

        if (Debug.instance.showDebugLayer) {
            mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            mShapeRenderer.setProjectionMatrix(mCamera.combined);
            if (Debug.instance.drawTileCorners) {
                mShapeRenderer.setColor(1, 1, 1, 1);
                float tileW = mTrack.getTileWidth();
                float tileH = mTrack.getTileHeight();
                for (float y = 0; y < mMapHeight; y += tileH) {
                    for (float x = 0; x < mMapWidth; x += tileW) {
                        mShapeRenderer.rect(x, y, Constants.UNIT_FOR_PIXEL, Constants.UNIT_FOR_PIXEL);
                    }
                }
            }
            mShapeRenderer.setColor(0, 0, 1, 1);
            mShapeRenderer.rect(mVehicle.getX(), mVehicle.getY(), Constants.UNIT_FOR_PIXEL, Constants.UNIT_FOR_PIXEL);
            mShapeRenderer.end();

            for (DebugShapeMap.Shape shape : DebugShapeMap.getMap().values()) {
                shape.draw(mShapeRenderer);
            }

            mShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            mShapeRenderer.setProjectionMatrix(mCamera.combined);
            mShapeRenderer.setColor(1, 0, 0, 1);
            MapUtils.renderObjectLayer(mShapeRenderer, mWorld.getTrack().getBordersLayer());
            mShapeRenderer.end();

            mDebugRenderer.render(mWorld.getBox2DWorld(), mCamera.combined);
        }
    }

    private static Vector2 sDelta = new Vector2();
    private void updateCamera(float delta) {
        boolean immediate = delta < 0;

        // Compute viewport size
        mNextCameraInfo.zoom = MathUtils.lerp(MIN_ZOOM, MAX_ZOOM, mVehicle.getSpeed() / MAX_ZOOM_SPEED);
        if (!immediate) {
            float zoomDelta = MAX_ZOOM_DELTA * delta;
            mNextCameraInfo.zoom = MathUtils.clamp(mNextCameraInfo.zoom,
                    mCameraInfo.zoom - zoomDelta, mCameraInfo.zoom + zoomDelta);
        }
        float viewportWidth = GamePlay.instance.viewportWidth * mNextCameraInfo.zoom;
        float viewportHeight = viewportWidth * mScreenHeight / mScreenWidth;
        mNextCameraInfo.viewportWidth = viewportWidth;
        mNextCameraInfo.viewportHeight = viewportHeight;

        // Compute pos
        float advance = Math.min(viewportWidth, viewportHeight) * Constants.CAMERA_ADVANCE_PERCENT;
        sDelta.set(advance, 0).rotate(mRacer.getCameraAngle()).add(mVehicle.getPosition()).sub(mCameraInfo.position);

        if (!immediate) {
            sDelta.limit(MAX_CAMERA_DELTA * delta);
        }
        mNextCameraInfo.position.set(mCameraInfo.position).add(sDelta);

        // Clamp camera to the limits of the track
        float minWidth = viewportWidth / 2;
        float minHeight = viewportHeight / 2;
        float maxWidth = mWorld.getTrack().getMapWidth() - viewportWidth / 2;
        float maxHeight = mWorld.getTrack().getMapHeight() - viewportHeight / 2;
        mNextCameraInfo.position.x = MathUtils.clamp(mNextCameraInfo.position.x, minWidth, maxWidth);
        mNextCameraInfo.position.y = MathUtils.clamp(mNextCameraInfo.position.y, minHeight, maxHeight);

        // Apply changes
        mCamera.viewportWidth = mNextCameraInfo.viewportWidth;
        mCamera.viewportHeight = mNextCameraInfo.viewportHeight;
        mCamera.position.set(mNextCameraInfo.position, 0);
        mCamera.update();

        // Swap instances
        CameraInfo tmp = mCameraInfo;
        mCameraInfo = mNextCameraInfo;
        mNextCameraInfo = tmp;
    }

    private void updateMapRendererCamera() {
        // Increase size of render view to make sure corners are correctly drawn
        float radius = (float) Math.hypot(mCamera.viewportWidth, mCamera.viewportHeight) * mCamera.zoom / 2;
        mRenderer.setView(mCamera.combined,
                mCamera.position.x - radius, mCamera.position.y - radius, radius * 2, radius * 2);
    }
}
