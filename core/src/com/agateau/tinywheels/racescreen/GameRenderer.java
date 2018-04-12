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
package com.agateau.tinywheels.racescreen;

import com.agateau.tinywheels.Constants;
import com.agateau.tinywheels.GameConfig;
import com.agateau.tinywheels.GamePlay;
import com.agateau.tinywheels.GameWorld;
import com.agateau.tinywheels.debug.Debug;
import com.agateau.tinywheels.debug.DebugShapeMap;
import com.agateau.tinywheels.gameobjet.GameObject;
import com.agateau.tinywheels.map.Track;
import com.agateau.tinywheels.map.MapUtils;
import com.agateau.tinywheels.racer.Vehicle;
import com.agateau.utils.GylMathUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;

/**
 * Responsible for rendering the game world
 */
public class GameRenderer {
    private GameConfig mGameConfig;

    private final Track mTrack;
    private final OrthogonalTiledMapRenderer mRenderer;
    private final Box2DDebugRenderer mDebugRenderer;
    private final Batch mBatch;
    private final OrthographicCamera mCamera;
    private final ShapeRenderer mShapeRenderer = new ShapeRenderer();
    private final GameWorld mWorld;
    private final float mMapWidth;
    private final float mMapHeight;

    private int[] mBackgroundLayerFirstIndexes = { 0 };
    private int[] mExtraBackgroundLayerIndexes;
    private int[] mForegroundLayerIndexes;
    private Vehicle mVehicle;
    private float mCameraAngle = 90;

    private int mScreenX;
    private int mScreenY;
    private int mScreenWidth;
    private int mScreenHeight;
    private PerformanceCounter mTilePerformanceCounter;
    private PerformanceCounter mGameObjectPerformanceCounter;

    public GameRenderer(GameWorld world, Vehicle vehicle, Batch batch, PerformanceCounters counters) {
        mDebugRenderer = new Box2DDebugRenderer();
        mWorld = world;
        mVehicle = vehicle;

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
    }

    public void setScreenRect(int x, int y, int width, int height) {
        mScreenX = x;
        mScreenY = y;
        mScreenWidth = width;
        mScreenHeight = height;
    }

    public void setConfig(GameConfig config) {
        mGameConfig = config;
        mDebugRenderer.setDrawVelocities(Debug.instance.drawVelocities);
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
        for (int z = 0; z < Constants.Z_COUNT; ++z) {
            for (GameObject object : mWorld.getActiveGameObjects()) {
                object.draw(mBatch, z);
            }

            if (z == Constants.Z_OBSTACLES && mForegroundLayerIndexes.length > 0) {
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

    private void updateCamera(float delta) {
        float viewportWidth = GamePlay.instance.viewportWidth;
        float viewportHeight = GamePlay.instance.viewportWidth * mScreenHeight / mScreenWidth;
        mCamera.viewportWidth = viewportWidth;
        mCamera.viewportHeight = viewportHeight;

        // Compute pos
        float maxCameraRotationSpeed = mGameConfig.rotateCamera ? Constants.MAX_CAMERA_ROTATION_SPEED : 0;

        float targetAngle = GylMathUtils.normalizeAngle(180 - mVehicle.getAngle());
        float deltaAngle = GylMathUtils.normalizeAngle180(targetAngle - mCameraAngle);

        float K = Constants.MIN_ANGLE_FOR_MAX_CAMERA_ROTATION_SPEED;
        float progress = Math.min(Math.abs(deltaAngle), K) / K;
        float maxRotationSpeed = MathUtils.lerp(1, maxCameraRotationSpeed, progress);
        float maxDeltaAngle = maxRotationSpeed * delta;
        deltaAngle = MathUtils.clamp(deltaAngle, -maxDeltaAngle, maxDeltaAngle);
        mCamera.rotate(deltaAngle);
        mCameraAngle += deltaAngle;

        if (!mGameConfig.rotateCamera && mCameraAngle != 90) {
            // If we just disabled camera rotation, reset the camera angle
            mCamera.rotate(-mCameraAngle + 90);
            mCameraAngle = 90;
        }

        // Compute advanceAngle
        float advanceAngle;
        if (mGameConfig.rotateCamera) {
            advanceAngle = 180 - mCameraAngle;
        } else {
            advanceAngle = mVehicle.getAngle();
        }

        float advance = Math.min(viewportWidth, viewportHeight) * Constants.CAMERA_ADVANCE_PERCENT;
        mCamera.position.x = mVehicle.getX() + advance * MathUtils.cosDeg(advanceAngle);
        mCamera.position.y = mVehicle.getY() + advance * MathUtils.sinDeg(advanceAngle);

        mCamera.update();
    }

    private void updateMapRendererCamera() {
        // Increase size of render view to make sure corners are correctly drawn
        float radius = (float) Math.hypot(mCamera.viewportWidth, mCamera.viewportHeight) * mCamera.zoom / 2;
        mRenderer.setView(mCamera.combined,
                mCamera.position.x - radius, mCamera.position.y - radius, radius * 2, radius * 2);
    }
}
