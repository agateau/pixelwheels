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

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.debug.Debug;
import com.agateau.pixelwheels.debug.DebugShapeMap;
import com.agateau.pixelwheels.gameobjet.GameObject;
import com.agateau.pixelwheels.map.MapUtils;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.screens.PwStageScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
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
    private final Track mTrack;
    private final OrthogonalTiledMapRenderer mRenderer;
    private final Box2DDebugRenderer mDebugRenderer;
    private final Batch mScreenBatch;
    private final OrthographicCamera mCamera;
    private final ShapeRenderer mShapeRenderer = new ShapeRenderer();
    private final GameWorld mWorld;
    private final CameraUpdater mCameraUpdater;
    private final FrameBuffer mFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, PwStageScreen.WIDTH, PwStageScreen.HEIGHT, false /* hasDepth */);
    private final Batch mBatch = new SpriteBatch();

    private final int[] mBackgroundLayerFirstIndexes = { 0 };
    private final int[] mExtraBackgroundLayerIndexes;
    private final int[] mForegroundLayerIndexes;

    private int mScreenWidth;
    private int mScreenHeight;
    private final PerformanceCounter mTilePerformanceCounter;
    private final PerformanceCounter mGameObjectPerformanceCounter;

    public GameRenderer(GameWorld world, Batch batch, PerformanceCounters counters) {
        mDebugRenderer = new Box2DDebugRenderer();
        mWorld = world;

        mTrack = mWorld.getTrack();

        mExtraBackgroundLayerIndexes = mTrack.getExtraBackgroundLayerIndexes();
        mForegroundLayerIndexes = mTrack.getForegroundLayerIndexes();

        mScreenBatch = batch;
        mCamera = new OrthographicCamera();
        boolean singlePlayer = mWorld.getPlayerRacers().size == 1;
        mCameraUpdater = singlePlayer ? new SinglePlayerCameraUpdater(mWorld) : new MultiPlayerCameraUpdater(mWorld);
        mRenderer = new OrthogonalTiledMapRenderer(mTrack.getMap(), Constants.UNIT_FOR_PIXEL, mBatch);

        mTilePerformanceCounter = counters.add("- tiles");
        mGameObjectPerformanceCounter = counters.add("- g.o.");

        mDebugRenderer.setDrawVelocities(Debug.instance.drawVelocities);

        mCameraUpdater.init(mCamera, PwStageScreen.WIDTH, PwStageScreen.HEIGHT);
    }

    public void setScreenSize(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
    }

    public void onAboutToStart() {
        updateCamera(CameraUpdater.IMMEDIATE);
    }

    public void render(float delta) {
        mFrameBuffer.begin();
        Gdx.gl.glViewport(0, 0, mFrameBuffer.getWidth(), mFrameBuffer.getHeight());
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
            if (Debug.instance.drawTileCorners) {
                mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                mShapeRenderer.setProjectionMatrix(mCamera.combined);
                mShapeRenderer.setColor(1, 1, 1, 1);
                float tileW = mTrack.getTileWidth();
                float tileH = mTrack.getTileHeight();
                float mapWidth = mTrack.getMapWidth();
                float mapHeight = mTrack.getMapHeight();
                for (float y = 0; y < mapHeight; y += tileH) {
                    for (float x = 0; x < mapWidth; x += tileW) {
                        mShapeRenderer.rect(x, y, Constants.UNIT_FOR_PIXEL, Constants.UNIT_FOR_PIXEL);
                    }
                }
                mShapeRenderer.end();
            }

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
        mFrameBuffer.end();
        renderToScreen();
    }

    private void renderToScreen() {
        mScreenBatch.begin();
        mScreenBatch.draw(mFrameBuffer.getColorBufferTexture(),
                // dst
                0, 0,
                // origin
                0, 0,
                // dst size
                mScreenWidth, mScreenHeight,
                // scale
                1, 1,
                // rotation
                0,
                // src
                0, 0, mFrameBuffer.getWidth(), mFrameBuffer.getHeight(),
                // flips
                false, true
                );
        mScreenBatch.end();
    }

    private void updateCamera(float delta) {
        mCameraUpdater.update(delta);
    }

    private void updateMapRendererCamera() {
        float width = MathUtils.floor(mCamera.viewportWidth * mCamera.zoom);
        float height = MathUtils.floor(mCamera.viewportHeight * mCamera.zoom);
        mRenderer.setView(mCamera.combined,
                MathUtils.floor(mCamera.position.x - width / 2), MathUtils.floor(mCamera.position.y - height / 2), width, height);
    }
}
