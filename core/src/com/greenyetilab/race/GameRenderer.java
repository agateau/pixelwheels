package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

/**
 * Responsible for rendering the game world
 */
public class GameRenderer {
    private static final float VIEWPORT_WIDTH = 40;
    private static final boolean DEBUG_RENDERER = true;
    private static final boolean DEBUG_RENDERER_VELOCITIES = false;

    private final TiledMap mMap;
    private final OrthogonalTiledMapRenderer mRenderer;
    private final Box2DDebugRenderer mDebugRenderer;
    private final Batch mBatch;
    private final OrthographicCamera mCamera;
    private final ShapeRenderer mShapeRenderer = new ShapeRenderer();
    private final GameWorld mWorld;
    private final float mMapWidth;
    private final float mMapHeight;

    private Vector2[] mSkidmarks = new Vector2[4000];
    private int mSkidmarksIndex = 0;
    private Car mCar;

    public GameRenderer(GameWorld world, Batch batch) {
        mDebugRenderer = new Box2DDebugRenderer(true, true, false, true, DEBUG_RENDERER_VELOCITIES, false);
        mWorld = world;

        mMap = mWorld.getMap();
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        mMapWidth = Constants.UNIT_FOR_PIXEL * layer.getWidth() * layer.getTileWidth();
        mMapHeight = Constants.UNIT_FOR_PIXEL * layer.getHeight() * layer.getTileHeight();

        mBatch = batch;
        mCamera = new OrthographicCamera();
        mRenderer = new OrthogonalTiledMapRenderer(mMap, Constants.UNIT_FOR_PIXEL, mBatch);

        mCar = mWorld.getCar();
        setupSkidmarks();
    }

    private void setupSkidmarks() {
        NMessageBus.register("skid", new NMessageBus.Handler() {
            @Override
            public void handle(String channel, Object data) {
                Wheel wheel = (Wheel) data;
                addSkidmarkAt(wheel.getBody().getWorldCenter());
            }
        });
    }

    public void render() {
        updateCamera();

        mRenderer.setView(mCamera);
        mRenderer.render();

        renderSkidmarks();

        mBatch.setProjectionMatrix(mCamera.combined);
        mBatch.begin();
        mCar.draw(mBatch);
        mBatch.end();

        if (DEBUG_RENDERER) {
            mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            mShapeRenderer.setColor(1, 1, 1, 1);
            mShapeRenderer.setProjectionMatrix(mCamera.combined);
            TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
            float tileW = Constants.UNIT_FOR_PIXEL * layer.getTileWidth();
            float tileH = Constants.UNIT_FOR_PIXEL * layer.getTileHeight();
            for (float y = 0; y < mMapHeight; y += tileH) {
                for (float x = 0; x < mMapWidth; x += tileW) {
                    mShapeRenderer.rect(x, y, Constants.UNIT_FOR_PIXEL, Constants.UNIT_FOR_PIXEL);
                }
            }
            mShapeRenderer.setColor(0, 0, 1, 1);
            mShapeRenderer.rect(mCar.getX(), mCar.getY(), Constants.UNIT_FOR_PIXEL, Constants.UNIT_FOR_PIXEL);
            mShapeRenderer.end();

            mDebugRenderer.render(mWorld.getBox2DWorld(), mCamera.combined);
        }
    }

    private void renderSkidmarks() {
        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        mShapeRenderer.setColor(0, 0, 0, 0.2f);
        mShapeRenderer.setProjectionMatrix(mCamera.combined);
        for (Vector2 pos: mSkidmarks) {
            if (pos != null) {
                mShapeRenderer.circle(pos.x, pos.y, 4 * Constants.UNIT_FOR_PIXEL, 8);
            }
        }
        mShapeRenderer.end();
    }

    private void addSkidmarkAt(Vector2 position) {
        Vector2 pos = mSkidmarks[mSkidmarksIndex];
        if (pos == null) {
            pos = new Vector2();
            mSkidmarks[mSkidmarksIndex] = pos;
        }
        pos.x = position.x;
        pos.y = position.y;
        mSkidmarksIndex = (mSkidmarksIndex + 1) % mSkidmarks.length;
    }

    private void updateCamera() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        mCamera.viewportWidth = VIEWPORT_WIDTH;
        mCamera.viewportHeight = VIEWPORT_WIDTH * screenH / screenW;

        // Compute pos
        // FIXME: Take car speed into account when computing advance
        float advance = /*(mCar.getSpeed() / Car.MAX_SPEED) **/ Math.min(mCamera.viewportWidth, mCamera.viewportHeight) / 3;
        float x = mCar.getX() + advance * MathUtils.cosDeg(mCar.getAngle());
        float y = mCar.getY() + advance * MathUtils.sinDeg(mCar.getAngle());

        // Make sure we correctly handle boundaries
        float minX = mCamera.viewportWidth / 2;
        float minY = mCamera.viewportHeight / 2;
        float maxX = mMapWidth - mCamera.viewportWidth / 2;
        float maxY = mMapHeight - mCamera.viewportHeight / 2;

        if (mCamera.viewportWidth <= mMapWidth) {
            mCamera.position.x = MathUtils.clamp(x, minX, maxX);
        } else {
            mCamera.position.x = mMapWidth / 2;
        }
        if (mCamera.viewportHeight <= mMapHeight) {
            mCamera.position.y = MathUtils.clamp(y, minY, maxY);
        } else {
            mCamera.position.y = mMapHeight / 2;
        }
        mCamera.update();
    }

    public void onScreenResized() {
        updateCamera();
    }
}
