package com.greenyetilab.race;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RaceGame extends ApplicationAdapter {
    private Stage mStage;
    private Viewport mViewport;
    private Batch mBatch;

    private TiledMap mMap;
    private float mMapWidth;
    private float mMapHeight;
    private OrthogonalTiledMapRenderer mRenderer;
    private Car mCar;

    @Override
    public void create () {
        mViewport = new ScreenViewport();
        mBatch = new SpriteBatch();
        mStage = new Stage(mViewport, mBatch);

        mMap = new TmxMapLoader().load("race.tmx");
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        mMapWidth = layer.getWidth() * layer.getTileWidth();
        mMapHeight = layer.getHeight() * layer.getTileHeight();
        mRenderer = new OrthogonalTiledMapRenderer(mMap, 1, mBatch);
        mCar = new Car(this);
        mStage.addActor(mCar);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        mStage.act();
        updateCamera();

        mRenderer.setView((OrthographicCamera) mViewport.getCamera());
        mRenderer.render();
        mStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mViewport.update(width, height, false);
        updateCamera();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mCar.setDirection(1);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mCar.setDirection(-1);
        } else {
            mCar.setDirection(0);
        }
        mCar.setAccelerating(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT));
        mCar.setBraking(Gdx.input.isKeyPressed(Input.Keys.SPACE));
    }

    private void updateCamera() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float minX = screenWidth / 2;
        float minY = screenHeight / 2;
        float maxX = mMapWidth - screenWidth / 2;
        float maxY = mMapHeight - screenHeight / 2;

        Camera camera = mViewport.getCamera();
        if (screenWidth <= mMapWidth) {
            camera.position.x = MathUtils.clamp(mCar.getX(), minX, maxX);
        } else {
            camera.position.x = mMapWidth / 2;
        }
        if (screenHeight <= mMapHeight) {
            camera.position.y = MathUtils.clamp(mCar.getY(), minY, maxY);
        } else {
            camera.position.y = mMapHeight / 2;
        }
        camera.position.x = MathUtils.floor(camera.position.x);
        camera.position.y = MathUtils.floor(camera.position.y);
        camera.update();
    }
}
