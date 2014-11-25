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
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.greenyetilab.utils.log.NLog;

public class RaceGameScreen extends ScreenAdapter {
    private static final float MAX_PITCH = 30;
    private static final float MAX_ACCEL = 7;
    private static final float TIME_STEP = 1f/60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private final RaceGame mGame;
    private final World mWorld;
    private final Box2DDebugRenderer mDebugRenderer;
    private Stage mStage;
    private Viewport mViewport;
    private Batch mBatch;

    private MapInfo mMapInfo;
    private TiledMap mMap;
    private float mMapWidth;
    private float mMapHeight;
    private OrthogonalTiledMapRenderer mRenderer;
    private Car mCar;
    private Wheel mWheel;

    private WidgetGroup mHud;
    private Label mTimeLabel;
    private float mTime = 0;

    public RaceGameScreen(RaceGame game, MapInfo mapInfo) {
        mGame = game;
        mViewport = new ScreenViewport();
        mBatch = new SpriteBatch();
        mStage = new Stage(mViewport, mBatch);
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
        mMapWidth = layer.getWidth() * layer.getTileWidth();
        mMapHeight = layer.getHeight() * layer.getTileHeight();
        mRenderer = new OrthogonalTiledMapRenderer(mMap, 1, mBatch);
    }

    void setupCar() {
        TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get(0);
        mCar = new Car(mGame, mWorld, layer);
        mWheel = new Wheel(mGame, mWorld);
        //moveCarToStartTile(mCar, layer);
        //mStage.addActor(mCar);
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
        mWheel.act(delta);
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
        mRenderer.setView((OrthographicCamera) mViewport.getCamera());
        mRenderer.render();
        mBatch.begin();
        mCar.draw(mBatch);
        mWheel.draw(mBatch);
        mBatch.end();
        mStage.draw();
        mDebugRenderer.render(mWorld, mViewport.getCamera().combined);
    }

    private void updateHud() {
        String text = StringUtils.formatRaceTime(mTime);
        mTimeLabel.setText(text);

        float x = mViewport.getCamera().position.x - Gdx.graphics.getWidth() / 2;
        float y = mViewport.getCamera().position.y + Gdx.graphics.getHeight() / 2;
        mHud.setPosition(x + 5, y - mHud.getHeight() - 15);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mViewport.update(width, height, false);
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
        //mCar.setDirection(direction);
        mWheel.setDirection(direction);
        boolean accelerating = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || touchAt(0.5f, 1);
        boolean braking = Gdx.input.isKeyPressed(Input.Keys.SPACE) || touchAt(0, 0.5f);
        /*
        mCar.setAccelerating(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || touchAt(0.5f, 1));
        mCar.setBraking(Gdx.input.isKeyPressed(Input.Keys.SPACE) || touchAt(0, 0.5f));
        */
        if (accelerating || braking) {
            mWheel.adjustSpeed(accelerating ? 1f : -1f);
        }
    }

    private void updateCamera() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float minX = screenWidth / 2;
        float minY = screenHeight / 2;
        float maxX = mMapWidth - screenWidth / 2;
        float maxY = mMapHeight - screenHeight / 2;

        float advance = (mCar.getSpeed() / Car.MAX_SPEED) * Math.min(screenWidth, screenHeight) / 3;
        float x = /*mCar*/ mWheel.getX() + advance * MathUtils.cosDeg(mCar.getAngle());
        float y = /*mCar*/ mWheel.getY() + advance * MathUtils.sinDeg(mCar.getAngle());
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
