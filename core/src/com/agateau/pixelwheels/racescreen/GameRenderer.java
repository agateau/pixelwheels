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
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.debug.Debug;
import com.agateau.pixelwheels.debug.DebugShapeMap;
import com.agateau.pixelwheels.gameobjet.GameObject;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.map.WaypointStore;
import com.agateau.pixelwheels.utils.BodyRegionDrawer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;

/** Responsible for rendering the game world */
public class GameRenderer {
    private final Track mTrack;
    private final OrthogonalTiledMapRenderer mRenderer;
    private final Box2DDebugRenderer mDebugRenderer;
    private final Batch mBatch;
    private final OrthographicCamera mCamera;
    private final ShapeRenderer mShapeRenderer = new ShapeRenderer();
    private final GameWorld mWorld;
    private final CameraUpdater mCameraUpdater;

    private final int[] mBackgroundLayerFirstIndexes = {0};
    private final int[] mExtraBackgroundLayerIndexes;
    private final int[] mForegroundLayerIndexes;

    private int mScreenX;
    private int mScreenY;
    private int mScreenWidth;
    private int mScreenHeight;
    private final PerformanceCounter mTilePerformanceCounter;
    private final PerformanceCounter mGameObjectPerformanceCounter;
    private final PerformanceCounter mSetupPerformanceCounter;
    private FrameBuffer mVehicleShadowsFrameBuffer;
    private final Matrix4 mVehicleShadowsFrameBufferProjectionMatrix = new Matrix4();

    public GameRenderer(GameWorld world, Batch batch, PerformanceCounters counters) {
        mDebugRenderer = new Box2DDebugRenderer();
        mWorld = world;

        mTrack = mWorld.getTrack();

        mExtraBackgroundLayerIndexes = mTrack.getExtraBackgroundLayerIndexes();
        mForegroundLayerIndexes = mTrack.getForegroundLayerIndexes();

        mBatch = batch;
        mCamera = new OrthographicCamera();
        boolean singlePlayer = mWorld.getPlayerRacers().size == 1;
        mCameraUpdater =
                GamePlay.instance.freeCamera
                        ? new FreeCameraUpdater(mWorld)
                        : singlePlayer
                                ? new SinglePlayerCameraUpdater(mWorld)
                                : new MultiPlayerCameraUpdater(mWorld);
        mRenderer =
                new OrthogonalTiledMapRenderer(mTrack.getMap(), Constants.UNIT_FOR_PIXEL, mBatch);

        mSetupPerformanceCounter = counters.add("- setup");
        mTilePerformanceCounter = counters.add("- tiles");
        mGameObjectPerformanceCounter = counters.add("- g.o.");

        mDebugRenderer.setDrawVelocities(Debug.instance.drawVelocities);

        if (Debug.instance.showDebugLayer) {
            setupWaypointDebugShape();
        }
    }

    private void setupWaypointDebugShape() {
        DebugShapeMap.put(
                "waypoints",
                renderer -> {
                    WaypointStore store = mTrack.getWaypointStore();

                    renderer.begin(ShapeRenderer.ShapeType.Line);
                    for (int idx = 0; idx < store.getCount(); ++idx) {
                        renderer.setColor(idx % 2, 1, 0, 1);
                        int prevIdx = store.getPreviousIndex(idx);
                        renderer.line(store.getWaypoint(prevIdx), store.getWaypoint(idx));
                    }
                    renderer.end();
                });
    }

    public void setScreenRect(int x, int y, int width, int height) {
        mScreenX = x;
        mScreenY = y;
        mScreenWidth = width;
        mScreenHeight = height;
        mCameraUpdater.init(mCamera, width, height);
        mVehicleShadowsFrameBuffer =
                new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false /* hasDepth */);
        mVehicleShadowsFrameBufferProjectionMatrix.setToOrtho2D(0, 0, width, height);
    }

    public void onAboutToStart() {
        updateCamera(CameraUpdater.IMMEDIATE);
    }

    public void render(float delta) {
        mSetupPerformanceCounter.start();
        HdpiUtils.glViewport(mScreenX, mScreenY, mScreenWidth, mScreenHeight);
        updateCamera(delta);
        updateMapRendererCamera();
        Rectangle viewBounds = mRenderer.getViewBounds();
        mSetupPerformanceCounter.stop();

        mTilePerformanceCounter.start();
        // Reset the color in case it was modified by the previous frame
        mBatch.setColor(1, 1, 1, 1);
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
            if (z == ZLevel.VEHICLE_SHADOWS) {
                mBatch.end();

                mVehicleShadowsFrameBuffer.begin();
                mBatch.begin();
                Gdx.gl.glClearColor(0, 0, 0, 0);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            }

            for (GameObject object : mWorld.getActiveGameObjects()) {
                object.draw(mBatch, z, viewBounds);
            }

            if (z == ZLevel.VEHICLE_SHADOWS) {
                mBatch.end();
                mVehicleShadowsFrameBuffer.end();
                mBatch.begin();
                drawVehicleShadowsFrameBuffer();
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
        mBatch.end();
        mGameObjectPerformanceCounter.stop();

        if (Debug.instance.showDebugLayer) {
            mShapeRenderer.setProjectionMatrix(mCamera.combined);
            if (Debug.instance.drawTileCorners) {
                mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                mShapeRenderer.setColor(1, 1, 1, 1);
                float tileW = mTrack.getTileWidth();
                float tileH = mTrack.getTileHeight();
                float mapWidth = mTrack.getMapWidth();
                float mapHeight = mTrack.getMapHeight();
                for (float y = 0; y < mapHeight; y += tileH) {
                    for (float x = 0; x < mapWidth; x += tileW) {
                        mShapeRenderer.rect(
                                x, y, Constants.UNIT_FOR_PIXEL, Constants.UNIT_FOR_PIXEL);
                    }
                }
                mShapeRenderer.end();
            }

            for (DebugShapeMap.Shape shape : DebugShapeMap.values()) {
                shape.draw(mShapeRenderer);
            }

            mDebugRenderer.render(mWorld.getBox2DWorld(), mCamera.combined);
        }
    }

    private void drawVehicleShadowsFrameBuffer() {
        float old = mBatch.getPackedColor();
        mBatch.setColor(0, 0, 0, BodyRegionDrawer.SHADOW_ALPHA);
        mBatch.setProjectionMatrix(mVehicleShadowsFrameBufferProjectionMatrix);

        int w = mVehicleShadowsFrameBuffer.getWidth();
        int h = mVehicleShadowsFrameBuffer.getHeight();
        Texture texture = mVehicleShadowsFrameBuffer.getColorBufferTexture();
        mBatch.draw(
                texture,
                // dst
                0,
                0,
                w,
                h,
                // src
                0,
                0,
                w,
                h,
                // flips
                false,
                true);

        mBatch.setProjectionMatrix(mCamera.combined);
        mBatch.setPackedColor(old);
    }

    private void updateCamera(float delta) {
        mCameraUpdater.update(delta);
    }

    private void updateMapRendererCamera() {
        float width = mCamera.viewportWidth * mCamera.zoom;
        float height = mCamera.viewportHeight * mCamera.zoom;
        mRenderer.setView(
                mCamera.combined,
                mCamera.position.x - width / 2,
                mCamera.position.y - height / 2,
                width,
                height);
    }

    private final Vector3 sTmp3 = new Vector3();

    public void mapFromScreen(Vector2 coord) {
        sTmp3.set(coord, 0);
        mCamera.unproject(sTmp3);
        coord.set(sTmp3.x, sTmp3.y);
    }
}
