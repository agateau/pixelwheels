package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
    private final GameWorld mWorld;
    private final float mMapWidth;
    private final float mMapHeight;

    private int[] mBackgroundLayerIndexes = { 0 };
    private int[] mForegroundLayerIndexes;
    private Vehicle mVehicle;

    private PerformanceCounter mTilePerformanceCounter;
    private PerformanceCounter mGameObjectPerformanceCounter;

    public GameRenderer(GameWorld world, Batch batch, PerformanceCounters counters) {
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
        mGameObjectPerformanceCounter = counters.add("- g.o.");
    }

    public void setDebugConfig(DebugConfig config) {
        mDebugConfig = config;
        mDebugRenderer.setDrawVelocities(mDebugConfig.drawVelocities);
    }

    public void render() {
        updateCamera();

        mTilePerformanceCounter.start();
        mRenderer.setView(mCamera);
        mRenderer.render(mBackgroundLayerIndexes);
        mTilePerformanceCounter.stop();

        renderSkidmarks();

        mGameObjectPerformanceCounter.start();
        mBatch.setProjectionMatrix(mCamera.combined);
        mBatch.begin();
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
        mBatch.end();
        mGameObjectPerformanceCounter.stop();

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

            mShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            mShapeRenderer.setProjectionMatrix(mCamera.combined);
            mShapeRenderer.setColor(1, 0, 0, 1);
            final float U = Constants.UNIT_FOR_PIXEL;
            for (MapObject object : mWorld.getMapInfo().getDirectionsLayer().getObjects()) {
                if (object instanceof PolygonMapObject) {
                    float[] vertices = ((PolygonMapObject)object).getPolygon().getTransformedVertices();
                    for (int idx = 2; idx < vertices.length; idx += 2) {
                        mShapeRenderer.line(vertices[idx - 2] * U, vertices[idx - 1] * U, vertices[idx] * U, vertices[idx + 1] * U);
                    }
                } else if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject)object).getRectangle();
                    mShapeRenderer.rect(rect.x * U, rect.y * U, rect.width * U, rect.height * U);
                }
            }
            mShapeRenderer.end();

            mDebugRenderer.render(mWorld.getBox2DWorld(), mCamera.combined);
        }
    }

    private void renderSkidmarks() {
        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        mShapeRenderer.setColor(0, 0, 0, 0.2f);
        mShapeRenderer.setProjectionMatrix(mCamera.combined);
        for (Vector2 pos: mWorld.getSkidmarks()) {
            if (pos != null) {
                mShapeRenderer.circle(pos.x, pos.y, 4 * Constants.UNIT_FOR_PIXEL, 8);
            }
        }
        mShapeRenderer.end();
    }

    private void updateCamera() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float viewportWidth = Constants.VIEWPORT_WIDTH;
        float viewportHeight = Constants.VIEWPORT_WIDTH * screenH / screenW;
        mCamera.viewportWidth = viewportWidth;
        mCamera.viewportHeight = viewportHeight;

        // Compute pos
        // FIXME: Take car speed into account when computing advance
        float advance = /*(mCar.getSpeed() / Car.MAX_SPEED) **/ Math.min(viewportWidth, viewportHeight) / 3;
        float x = mVehicle.getX() + advance * MathUtils.cosDeg(mVehicle.getAngle());
        float y = mVehicle.getY() + advance * MathUtils.sinDeg(mVehicle.getAngle());

        // Make sure we correctly handle boundaries
        float minX = viewportWidth / 2;
        float minY = viewportHeight / 2;
        float maxX = mMapWidth - viewportWidth / 2;
        float maxY = mMapHeight - viewportHeight / 2;

        if (viewportWidth <= mMapWidth) {
            mCamera.position.x = MathUtils.clamp(x, minX, maxX);
        } else {
            mCamera.position.x = mMapWidth / 2;
        }
        if (viewportHeight <= mMapHeight) {
            mCamera.position.y = MathUtils.clamp(y, minY, maxY);
        } else {
            mCamera.position.y = mMapHeight / 2;
        }
        mCamera.update();
        mWorld.setVisibleSection(mCamera.position.y - viewportHeight / 2, mCamera.position.y + viewportHeight / 2);
    }

    public void onScreenResized() {
        updateCamera();
    }
}
