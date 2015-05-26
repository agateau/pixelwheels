package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
    public static class DebugConfig {
        public boolean enabled = false;
        public boolean drawVelocities = false;
        public boolean drawTileCorners = false;
    }
    private DebugConfig mDebugConfig = new DebugConfig();

    private final MapInfo mMapInfo;
    private final OrthogonalTiledMapRenderer mRenderer;
    private final Box2DDebugRenderer mDebugRenderer;
    private final Batch mBatch;
    private final OrthographicCamera mCamera;
    private final ShapeRenderer mShapeRenderer = new ShapeRenderer();
    private final Assets mAssets;
    private final GameWorld mWorld;
    private final float mMapWidth;
    private final float mMapHeight;

    private int[] mBackgroundLayerIndexes = { 0 };
    private int[] mForegroundLayerIndexes;
    private Vehicle mVehicle;
    private float mCameraAngle = 90;

    private PerformanceCounter mTilePerformanceCounter;
    private PerformanceCounter mSkidmarksPerformanceCounter;
    private PerformanceCounter mGameObjectPerformanceCounter;

    public GameRenderer(Assets assets, GameWorld world, Batch batch, PerformanceCounters counters) {
        mAssets = assets;
        mDebugRenderer = new Box2DDebugRenderer();
        mWorld = world;

        mMapInfo = mWorld.getMapInfo();
        mMapWidth = mMapInfo.getMapWidth();
        mMapHeight = mMapInfo.getMapHeight();

        mForegroundLayerIndexes = new int[]{ 1 };

        mBatch = batch;
        mCamera = new OrthographicCamera();
        mRenderer = new OrthogonalTiledMapRenderer(mMapInfo.getMap(), Constants.UNIT_FOR_PIXEL, mBatch);

        mVehicle = mWorld.getPlayerVehicle();

        mTilePerformanceCounter = counters.add("- tiles");
        mSkidmarksPerformanceCounter = counters.add("- skidmarks");
        mGameObjectPerformanceCounter = counters.add("- g.o.");
    }

    public void setDebugConfig(DebugConfig config) {
        mDebugConfig = config;
        mDebugRenderer.setDrawVelocities(mDebugConfig.drawVelocities);
    }

    public OrthographicCamera getCamera() {
        return mCamera;
    }

    public void render(float delta) {
        Color bg = mMapInfo.getBackgroundColor();
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateCamera(delta);
        updateMapRendererCamera();

        mTilePerformanceCounter.start();
        mBatch.disableBlending();
        mRenderer.render(mBackgroundLayerIndexes);
        mTilePerformanceCounter.stop();

        mBatch.setProjectionMatrix(mCamera.combined);
        mBatch.enableBlending();
        mBatch.begin();
        mSkidmarksPerformanceCounter.start();
        renderSkidmarks();
        mSkidmarksPerformanceCounter.stop();

        mGameObjectPerformanceCounter.start();
        for (int z = 0; z < Constants.Z_COUNT; ++z) {
            for (GameObject object : mWorld.getActiveGameObjects()) {
                object.draw(mBatch, z);
            }

            if (z == Constants.Z_OBSTACLES && mForegroundLayerIndexes != null) {
                mBatch.end();
                mRenderer.render(mForegroundLayerIndexes);
                mBatch.begin();
            }
        }
        mGameObjectPerformanceCounter.stop();
        mBatch.end();

        if (mDebugConfig.enabled) {
            mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            mShapeRenderer.setProjectionMatrix(mCamera.combined);
            if (mDebugConfig.drawTileCorners) {
                mShapeRenderer.setColor(1, 1, 1, 1);
                float tileW = mMapInfo.getTileWidth();
                float tileH = mMapInfo.getTileHeight();
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
            MapUtils.renderObjectLayer(mShapeRenderer, mWorld.getMapInfo().getBordersLayer());
            mShapeRenderer.end();

            mDebugRenderer.render(mWorld.getBox2DWorld(), mCamera.combined);
        }
    }

    private void renderSkidmarks() {
        final float U = Constants.UNIT_FOR_PIXEL;
        final float width = mAssets.skidmark.getRegionWidth() * U;
        final float height = mAssets.skidmark.getRegionHeight() * U;
        for (Vector2 pos: mWorld.getSkidmarks()) {
            if (pos != null) {
                mBatch.draw(mAssets.skidmark, pos.x, pos.y, width, height);
            }
        }
    }

    private void updateCamera(float delta) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float viewportWidth = GamePlay.instance.viewportWidth;
        float viewportHeight = GamePlay.instance.viewportWidth * screenH / screenW;
        mCamera.viewportWidth = viewportWidth;
        mCamera.viewportHeight = viewportHeight;

        // Compute pos
        float advanceAngle;
        if (Constants.ROTATE_CAMERA) {
            float targetAngle = 180 - mVehicle.getAngle();
            float deltaAngle = targetAngle - mCameraAngle;
            float K = Constants.MIN_ANGLE_FOR_MAX_CAMERA_ROTATION_SPEED;
            float progress = Math.min(Math.abs(deltaAngle), K) / K;
            float maxRotationSpeed = MathUtils.lerp(1, Constants.MAX_CAMERA_ROTATION_SPEED, progress);
            float maxDeltaAngle = maxRotationSpeed * delta;
            deltaAngle = MathUtils.clamp(deltaAngle, -maxDeltaAngle, maxDeltaAngle);
            mCamera.rotate(deltaAngle);
            mCameraAngle += deltaAngle;

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
        if (Constants.ROTATE_CAMERA) {
            // Increase size of render view to make sure corners are correctly drawn
            float radius = (float) Math.hypot(mCamera.viewportWidth, mCamera.viewportHeight) * mCamera.zoom / 2;
            mRenderer.setView(mCamera.combined,
                    mCamera.position.x - radius, mCamera.position.y - radius, radius * 2, radius * 2);
        } else {
            mRenderer.setView(mCamera);
        }
    }
}
