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
import com.agateau.pixelwheels.map.WaypointStore;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
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
                Debug.instance.freeCamera
                        ? new FreeCameraUpdater(mWorld)
                        : singlePlayer
                                ? new SinglePlayerCameraUpdater(mWorld)
                                : new MultiPlayerCameraUpdater(mWorld);
        mRenderer =
                new OrthogonalTiledMapRenderer(mTrack.getMap(), Constants.UNIT_FOR_PIXEL, mBatch);

        mTilePerformanceCounter = counters.add("- tiles");
        mGameObjectPerformanceCounter = counters.add("- g.o.");

        mDebugRenderer.setDrawVelocities(Debug.instance.drawVelocities);

        if (Debug.instance.showDebugLayer) {
            setupWaypointDebugShape();
        }
    }

    private void setupWaypointDebugShape() {
        DebugShapeMap.getMap()
                .put(
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
    }

    public void onAboutToStart() {
        updateCamera(CameraUpdater.IMMEDIATE);
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
                        mShapeRenderer.rect(
                                x, y, Constants.UNIT_FOR_PIXEL, Constants.UNIT_FOR_PIXEL);
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
            renderBorders();
            mShapeRenderer.end();

            mDebugRenderer.render(mWorld.getBox2DWorld(), mCamera.combined);
        }
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

    private void renderBorders() {
        final float U = Constants.UNIT_FOR_PIXEL;
        for (MapObject object : mWorld.getTrack().getObstacleObjects()) {
            if (!MapUtils.isBorderObstacle(object)) {
                return;
            }
            if (object instanceof PolygonMapObject) {
                float[] vertices =
                        ((PolygonMapObject) object).getPolygon().getTransformedVertices();
                for (int idx = 2; idx < vertices.length; idx += 2) {
                    mShapeRenderer.line(
                            vertices[idx - 2] * U,
                            vertices[idx - 1] * U,
                            vertices[idx] * U,
                            vertices[idx + 1] * U);
                }
            } else if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                mShapeRenderer.rect(rect.x * U, rect.y * U, rect.width * U, rect.height * U);
            }
        }
    }
}
