package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import com.badlogic.gdx.utils.StringBuilder;
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
    private Label mScoreLabel;
    private Label mSpeedLabel;
    private Label mDebugLabel;

    private final PerformanceCounters mPerformanceCounters = new PerformanceCounters();
    private PerformanceCounter mGameWorldPerformanceCounter;
    private PerformanceCounter mRendererPerformanceCounter;
    private PerformanceCounter mOverallPerformanceCounter;

    public RaceGameScreen(RaceGame game, MapInfo mapInfo) {
        mGame = game;
        mBatch = new SpriteBatch();
        mOverallPerformanceCounter = mPerformanceCounters.add("All");
        mGameWorldPerformanceCounter = mPerformanceCounters.add("GameWorld.act");
        mGameWorld = new GameWorld(game, mapInfo, mPerformanceCounters);
        mRendererPerformanceCounter = mPerformanceCounters.add("Renderer");
        mGameRenderer = new GameRenderer(mGameWorld, mBatch, mPerformanceCounters);
        setupGameRenderer();
        mVehicle = mGameWorld.getVehicle();
        setupHud();
        /*if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            mInputHandler = new AccelerometerInputHandler();
        } else*/ if (Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
            mInputHandler = new TouchInputHandler();
        } else {
            mInputHandler = new KeyboardInputHandler();
        }
    }

    private void setupGameRenderer() {
        GameRenderer.DebugConfig config = new GameRenderer.DebugConfig();
        Preferences prefs = RaceGame.getPreferences();
        config.enabled = prefs.getBoolean("debug/box2d", false);
        config.drawTileCorners = prefs.getBoolean("debug/tiles/drawCorners", false);
        config.drawVelocities = prefs.getBoolean("debug/box2d/drawVelocities", false);
        mGameRenderer.setDebugConfig(config);
    }

    void setupHud() {
        mHudViewport = new ScreenViewport();
        mHudStage = new Stage(mHudViewport, mBatch);
        Gdx.input.setInputProcessor(mHudStage);

        Skin skin = mGame.getAssets().skin;
        mHud = new WidgetGroup();

        mScoreLabel = new Label("0:00.0", skin);
        mSpeedLabel = new Label("0", skin);
        mHud.addActor(mScoreLabel);
        mHud.addActor(mSpeedLabel);
        mHud.setHeight(mScoreLabel.getHeight());

        if (RaceGame.getPreferences().getBoolean("debug/showDebugHud", false)) {
            mDebugLabel = new Label("D", skin, "small");
            mHudStage.addActor(mDebugLabel);
        }
        mHudStage.addActor(mHud);
        updateHud();
    }

    @Override
    public void render(float delta) {
        mOverallPerformanceCounter.start();
        mGameWorldPerformanceCounter.start();
        mGameWorld.act(delta);
        mGameWorldPerformanceCounter.stop();

        mHudStage.act(delta);
        switch (mGameWorld.getState()) {
        case RUNNING:
            break;
        case BROKEN:
            mGame.showGameOverOverlay();
            return;
        case FINISHED:
            return;
        }

        handleInput();
        updateHud();

        mRendererPerformanceCounter.start();
        mGameRenderer.render();
        mHudStage.draw();
        mRendererPerformanceCounter.stop();

        mOverallPerformanceCounter.stop();
        mPerformanceCounters.tick(delta);
    }

    private static StringBuilder sDebugSB = new StringBuilder();
    private void updateHud() {
        mScoreLabel.setText(String.format("%06d", mGameWorld.getScore()));
        mScoreLabel.setPosition(5, 0);

        mSpeedLabel.setText(StringUtils.formatSpeed(mVehicle.getSpeed()));
        mSpeedLabel.setPosition(mHudViewport.getScreenWidth() - mSpeedLabel.getPrefWidth() - 5, 0);

        mHud.setPosition(0, mHudViewport.getScreenHeight() - mHud.getHeight() - 5);

        if (mDebugLabel != null) {
            sDebugSB.setLength(0);
            sDebugSB.append("objCount: ").append(mGameWorld.getActiveGameObjects().size).append('\n');
            sDebugSB.append("FPS: ").append(Gdx.graphics.getFramesPerSecond()).append('\n');
            for (PerformanceCounter counter : mPerformanceCounters.counters) {
                sDebugSB.append(counter.name).append(": ")
                        .append(String.valueOf((int)(counter.time.value * 1000)))
                        .append(" | ")
                        .append(String.valueOf((int)(counter.load.value * 100)))
                        .append("%\n")
                        ;
            }
            mDebugLabel.setText(sDebugSB);
            mDebugLabel.setPosition(0, mHud.getY() - mDebugLabel.getPrefHeight());
        }
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

    @Override
    public void dispose() {
        super.dispose();
        mGameWorld.dispose();
    }
}
