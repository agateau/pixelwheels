package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.greenyetilab.utils.log.NLog;

public class RaceGameScreen extends ScreenAdapter {
    private static final float MAX_AZIMUTH = 30;
    private final RaceGame mGame;
    private Stage mStage;
    private Viewport mViewport;
    private Batch mBatch;

    private MapInfo mMapInfo;
    private TiledMap mMap;
    private float mMapWidth;
    private float mMapHeight;
    private OrthogonalTiledMapRenderer mRenderer;
    private Car mCar;

    private WidgetGroup mHud;
    private Label mTimeLabel;
    private float mTime = 0;

    public RaceGameScreen(RaceGame game, MapInfo mapInfo) {
        mGame = game;
        mViewport = new ScreenViewport();
        mBatch = new SpriteBatch();
        mStage = new Stage(mViewport, mBatch);
        Gdx.input.setInputProcessor(mStage);
        setupMap(mapInfo);
        setupCar();
        setupHud();
    }

    void setupMap(MapInfo mapInfo) {
        mMapInfo = mapInfo;
        mMap = mapInfo.getMap();
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        mMapWidth = layer.getWidth() * layer.getTileWidth();
        mMapHeight = layer.getHeight() * layer.getTileHeight();
        mRenderer = new OrthogonalTiledMapRenderer(mMap, 1, mBatch);
    }

    void setupCar() {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        mCar = new Car(mGame, layer);
        moveCarToStartTile(mCar, layer);
        mStage.addActor(mCar);
    }

    void setupHud() {
        Skin skin = mGame.getAssets().skin;
        mHud = new WidgetGroup();

        mTimeLabel = new Label("0:00.0", skin);
        mTimeLabel.invalidate();
        mHud.addActor(mTimeLabel);
        mHud.setHeight(mTimeLabel.getHeight());

        mStage.addActor(mHud);
        updateHud();
    }

    private void moveCarToStartTile(Car car, TiledMapTileLayer layer) {
        for (int ty=0; ty < layer.getHeight(); ++ty) {
            for (int tx=0; tx < layer.getWidth(); ++tx) {
                TiledMapTileLayer.Cell cell = layer.getCell(tx, ty);
                TiledMapTile tile = cell.getTile();
                if (tile.getProperties().containsKey("start")) {
                    float tw = layer.getTileWidth();
                    float th = layer.getTileHeight();
                    car.setPosition(tx * tw + tw / 2, ty * th + th / 2);
                    return;
                }
            }
        }
        NLog.e("No Tile with 'start' property found");
    }

    @Override
    public void render(float delta) {
        mTime += delta;
        mStage.act(delta);
        switch (mCar.getState()) {
        case RUNNING:
            break;
        case BROKEN:
            mGame.showGameOverOverlay(mMapInfo);
            return;
        case FINISHED:
            mGame.showFinishedOverlay(mMapInfo, mTime);
            return;
        }

        handleInput();
        updateCamera();
        updateHud();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mRenderer.setView((OrthographicCamera) mViewport.getCamera());
        mRenderer.render();
        mStage.draw();
    }

    private void updateHud() {
        String text = StringUtils.formatRaceTime(mTime);
        mTimeLabel.setText(text);

        float x = mViewport.getCamera().position.x - Gdx.graphics.getWidth() / 2;
        float y = mViewport.getCamera().position.y + Gdx.graphics.getHeight() / 2;
        mHud.setPosition(x, y - mHud.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mViewport.update(width, height, false);
        updateCamera();
    }

    private void handleInput() {
        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            float angle = Gdx.input.getPitch();
            float direction = MathUtils.clamp(angle, -MAX_AZIMUTH, MAX_AZIMUTH) / MAX_AZIMUTH;
            mCar.setDirection(direction);
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                mCar.setDirection(1);
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                mCar.setDirection(-1);
            } else {
                mCar.setDirection(0);
            }
        }
        mCar.setAccelerating(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || touchAt(0.5f, 1));
        mCar.setBraking(Gdx.input.isKeyPressed(Input.Keys.SPACE) || touchAt(0, 0.5f));
    }

    private void updateCamera() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float minX = screenWidth / 2;
        float minY = screenHeight / 2;
        float maxX = mMapWidth - screenWidth / 2;
        float maxY = mMapHeight - screenHeight / 2;

        float advance = (mCar.getSpeed() / Car.MAX_SPEED) * Math.min(screenWidth, screenHeight) / 3;
        float x = mCar.getX() + advance * MathUtils.cosDeg(mCar.getAngle());
        float y = mCar.getY() + advance * MathUtils.sinDeg(mCar.getAngle());
        Camera camera = mViewport.getCamera();
        if (screenWidth <= mMapWidth) {
            camera.position.x = MathUtils.clamp(x, minX, maxX);
        } else {
            camera.position.x = mMapWidth / 2;
        }
        if (screenHeight <= mMapHeight) {
            camera.position.y = MathUtils.clamp(y, minY, maxY);
        } else {
            camera.position.y = mMapHeight / 2;
        }
        camera.position.x = MathUtils.floor(camera.position.x);
        camera.position.y = MathUtils.floor(camera.position.y);
        camera.update();
    }

    private boolean touchAt(float startX, float endX) {
        // check if any finger is touching the area between startX and endX
        // startX/endX are given between 0 (left edge of the screen) and 1 (right edge of the screen)
        for (int i = 0; i < 2; i++) {
            float x = Gdx.input.getX() / (float)Gdx.graphics.getWidth();
            if (Gdx.input.isTouched(i) && (x >= startX && x <= endX)) {
                return true;
            }
        }
        return false;
    }
}
