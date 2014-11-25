package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.greenyetilab.utils.log.NLog;

public class RaceGameScreen extends ScreenAdapter {
    private static final boolean DEBUG_RENDERER = false;

    private static final float VIEWPORT_WIDTH = 40;
    private static final float MAX_PITCH = 30;
    private static final float MAX_ACCEL = 7;
    private static final float TIME_STEP = 1f/60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private final RaceGame mGame;
    private final World mWorld;
    private final Box2DDebugRenderer mDebugRenderer;
    private Stage mStage;
    private OrthographicCamera mCamera;
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
        mCamera = new OrthographicCamera();
        mBatch = new SpriteBatch();
        mStage = new Stage();
        mWorld = new World(new Vector2(0, 0), true);
        Gdx.input.setInputProcessor(mStage);
        setupMap(mapInfo);
        setupCar();
        setupHud();
        mDebugRenderer = new Box2DDebugRenderer();
    }

    void setupMap(MapInfo mapInfo) {
        mMapInfo = mapInfo;
        mMap = mapInfo.getMap();
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        mMapWidth = Constants.UNIT_FOR_PIXEL * layer.getWidth() * layer.getTileWidth();
        mMapHeight = Constants.UNIT_FOR_PIXEL * layer.getHeight() * layer.getTileHeight();
        mRenderer = new OrthogonalTiledMapRenderer(mMap, Constants.UNIT_FOR_PIXEL, mBatch);
    }

    void setupCar() {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        mCar = new Car(mGame, mWorld, layer);
        //moveCarToStartTile(mCar, layer);
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
                    car.setPosition((tx * tw + tw / 2), (ty * th + th / 2));
                    return;
                }
            }
        }
        NLog.e("No Tile with 'start' property found");
    }
    
    private float mTimeAccumulator = 0;

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        mTimeAccumulator += frameTime;
        while (mTimeAccumulator >= TIME_STEP) {
            mWorld.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            mTimeAccumulator -= TIME_STEP;
        }
    }

    @Override
    public void render(float delta) {
        mTime += delta;

        mStage.act(delta);
        mCar.act(delta);
        doPhysicsStep(delta);
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
        mRenderer.setView(mCamera);
        mRenderer.render();
        mBatch.setProjectionMatrix(mCamera.combined);
        mBatch.begin();
        mCar.draw(mBatch);
        mBatch.end();
        mStage.draw();
        if (DEBUG_RENDERER) {
            mDebugRenderer.render(mWorld, mCamera.combined);
        }
    }

    private void updateHud() {
        String text = StringUtils.formatRaceTime(mTime);
        mTimeLabel.setText(text);

        float x = mCamera.position.x - Gdx.graphics.getWidth() / 2;
        float y = mCamera.position.y + Gdx.graphics.getHeight() / 2;
        mHud.setPosition(x + 5, y - mHud.getHeight() - 15);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        updateCamera();
    }

    private void handleInput() {
        float direction = 0;
        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Compass)) {
            float angle = Gdx.input.getPitch();
            direction = MathUtils.clamp(angle, -MAX_PITCH, MAX_PITCH) / MAX_PITCH;

        } else if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            float angle = -Gdx.input.getAccelerometerY();
            direction = MathUtils.clamp(angle, -MAX_ACCEL, MAX_ACCEL) / MAX_ACCEL;
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                direction = 1;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                direction = -1;
            }
        }
        mCar.setDirection(direction);
        boolean accelerating = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || touchAt(0.5f, 1);
        boolean braking = Gdx.input.isKeyPressed(Input.Keys.SPACE) || touchAt(0, 0.5f);
        mCar.setAccelerating(accelerating);
        mCar.setBraking(braking);
    }

    private void updateCamera() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        mCamera.viewportWidth = VIEWPORT_WIDTH;
        mCamera.viewportHeight = VIEWPORT_WIDTH * screenH / screenW;

        // Compute pos
        float advance = (mCar.getSpeed() / Car.MAX_SPEED) * Math.min(mCamera.viewportWidth, mCamera.viewportHeight) / 3;
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
