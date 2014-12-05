package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class RaceGameScreen extends ScreenAdapter {
    private final RaceGame mGame;
    private final GameWorld mGameWorld;
    private Batch mBatch;

    private Vehicle mVehicle;

    private GameRenderer mGameRenderer;

    private GameInput mInput = new GameInput();
    private GameInputHandler mInputHandler;

    private Stage mHudStage;
    private ScreenViewport mHudViewport;
    private WidgetGroup mHud;
    private Label mTimeLabel;
    private Label mSpeedLabel;
    private float mTime = 0;

    public RaceGameScreen(RaceGame game, MapInfo mapInfo) {
        mGame = game;
        mBatch = new SpriteBatch();
        mGameWorld = new GameWorld(game, mapInfo);
        mGameRenderer = new GameRenderer(mGameWorld, mBatch);
        setupGameRenderer();
        mVehicle = mGameWorld.getVehicle();
        setupHud();
        //if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            //mInputHandler = new AccelerometerInputHandler();
        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
            mInputHandler = new TouchInputHandler();
        } else {
            mInputHandler = new KeyboardInputHandler();
        }
    }

    private void setupGameRenderer() {
        GameRenderer.DebugConfig config = new GameRenderer.DebugConfig();
        Preferences prefs = RaceGame.getPreferences();
        config.enabled = prefs.getBoolean("debug/enabled", false);
        config.drawTileCorners = prefs.getBoolean("debug/drawTileCorners", false);
        config.drawVelocities = prefs.getBoolean("debug/drawVelocities", false);
        mGameRenderer.setDebugConfig(config);
    }

    void setupHud() {
        mHudViewport = new ScreenViewport();
        mHudStage = new Stage(mHudViewport, mBatch);
        Gdx.input.setInputProcessor(mHudStage);

        Skin skin = mGame.getAssets().skin;
        mHud = new WidgetGroup();

        mTimeLabel = new Label("0:00.0", skin);
        mSpeedLabel = new Label("0", skin);
        mHud.addActor(mTimeLabel);
        mHud.addActor(mSpeedLabel);
        mHud.setHeight(mTimeLabel.getHeight());

        mHudStage.addActor(mHud);
        updateHud();
    }

    @Override
    public void render(float delta) {
        mTime += delta;

        mGameWorld.act(delta);
        mHudStage.act(delta);
        switch (mVehicle.getState()) {
        case RUNNING:
            break;
        case BROKEN:
            mGame.showGameOverOverlay(mGameWorld.getMapInfo());
            return;
        case FINISHED:
            mGame.showFinishedOverlay(mGameWorld.getMapInfo(), mTime);
            return;
        }

        handleInput();
        updateHud();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mGameRenderer.render();

        mHudStage.draw();
    }

    private void updateHud() {
        String text = StringUtils.formatRaceTime(mTime);
        mTimeLabel.setText(text);
        mTimeLabel.setPosition(5, 0);

        mSpeedLabel.setText(StringUtils.formatSpeed(mVehicle.getSpeed()));
        mSpeedLabel.setPosition(mHudViewport.getScreenWidth() - mSpeedLabel.getPrefWidth() - 5, 0);

        mHud.setPosition(0, mHudViewport.getScreenHeight() - mHud.getHeight() - 5);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mHudViewport.update(width, height, true);
        mGameRenderer.onScreenResized();
    }

    private void handleInput() {
        mInput.braking = false;
        mInput.accelerating = false;
        mInput.direction = 0;
        mInputHandler.updateGameInput(mInput);
        mVehicle.setDirection(mInput.direction);
        mVehicle.setAccelerating(mInput.accelerating);
        mVehicle.setBraking(mInput.braking);
    }
}
