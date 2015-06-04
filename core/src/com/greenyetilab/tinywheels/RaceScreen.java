package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Map;

public class RaceScreen extends ScreenAdapter {
    private final TheGame mGame;
    private final GameWorld mGameWorld;
    private Batch mBatch;

    private Vehicle mVehicle;

    private GameRenderer mGameRenderer;

    private Stage mHudStage;
    private ScreenViewport mHudViewport;
    private WidgetGroup mHud;
    private Label mLapLabel;
    private Label mSpeedLabel;
    private Label mDebugLabel;

    private final PerformanceCounters mPerformanceCounters = new PerformanceCounters();
    private PerformanceCounter mGameWorldPerformanceCounter;
    private PerformanceCounter mRendererPerformanceCounter;
    private PerformanceCounter mOverallPerformanceCounter;

    HudBridge mHudBridge = new HudBridge() {
        private final Vector3 mWorldVector = new Vector3();
        private final Vector2 mHudVector = new Vector2();
        @Override
        public Vector2 toHudCoordinate(float x, float y) {
            mWorldVector.x = x;
            mWorldVector.y = y;
            mWorldVector.z = 0;
            Vector3 vector = mGameRenderer.getCamera().project(mWorldVector);
            mHudVector.x = vector.x;
            mHudVector.y = vector.y;
            return mHudVector;
        }

        @Override
        public Stage getStage() {
            return mHudStage;
        }
    };

    public RaceScreen(TheGame game, MapInfo mapInfo, String playerVehicleId) {
        mGame = game;
        mBatch = new SpriteBatch();
        setupHud();
        mOverallPerformanceCounter = mPerformanceCounters.add("All");
        mGameWorldPerformanceCounter = mPerformanceCounters.add("GameWorld.act");
        mGameWorld = new GameWorld(game, mapInfo, playerVehicleId, mHudBridge, mPerformanceCounters);
        mRendererPerformanceCounter = mPerformanceCounters.add("Renderer");
        mGameRenderer = new GameRenderer(game.getAssets(), mGameWorld, mBatch, mPerformanceCounters);
        setupGameRenderer();
        mVehicle = mGameWorld.getPlayerVehicle();
    }

    private void setupGameRenderer() {
        GameRenderer.DebugConfig config = new GameRenderer.DebugConfig();
        Preferences prefs = TheGame.getPreferences();
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

        mLapLabel = new Label("0:00.0", skin);
        mSpeedLabel = new Label("0", skin);
        mHud.addActor(mLapLabel);
        mHud.addActor(mSpeedLabel);
        mHud.setHeight(mLapLabel.getHeight());

        if (TheGame.getPreferences().getBoolean("debug/showDebugHud", false)) {
            mDebugLabel = new Label("D", skin, "small");
            mHudStage.addActor(mDebugLabel);
        }
        mHudStage.addActor(mHud);
    }

    @Override
    public void render(float delta) {
        mOverallPerformanceCounter.start();
        mGameWorldPerformanceCounter.start();
        GameWorld.State oldState = mGameWorld.getState();
        mGameWorld.act(delta);
        GameWorld.State newState = mGameWorld.getState();
        mGameWorldPerformanceCounter.stop();

        if (oldState != newState) {
            showFinishedOverlay();
        }

        mHudStage.act(delta);
        updateHud();

        mRendererPerformanceCounter.start();
        mGameRenderer.render(delta);
        mHudStage.draw();
        mRendererPerformanceCounter.stop();

        mOverallPerformanceCounter.stop();
        mPerformanceCounters.tick(delta);
    }

    private static StringBuilder sDebugSB = new StringBuilder();
    private void updateHud() {
        int lapCount = Math.max(mGameWorld.getPlayerRacer().getLapPositionComponent().getLapCount(), 1);
        int totalLapCount = mGameWorld.getMapInfo().getTotalLapCount();
        int rank = mGameWorld.getPlayerRank();
        mLapLabel.setText(String.format("Lap: %d/%d Rank: %d", lapCount, totalLapCount, rank));
        mLapLabel.setPosition(5, 0);

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
            for (Map.Entry<String, String> entry : DebugStringMap.getMap().entrySet()) {
                sDebugSB.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n");
            }
            mDebugLabel.setText(sDebugSB);
            mDebugLabel.setPosition(0, mHud.getY() - mDebugLabel.getPrefHeight());
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mHudViewport.update(width, height, true);
    }

    private void showFinishedOverlay() {
        mHudStage.addActor(new FinishedOverlay(mGame, mGameWorld.getRacers(), mGameWorld.getPlayerRacer()));
    }

    @Override
    public void dispose() {
        super.dispose();
        mGameWorld.dispose();
    }
}
