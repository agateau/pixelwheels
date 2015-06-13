package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Map;

/**
 * Hud showing player info during race
 */
class Hud {
    private final PerformanceCounters mPerformanceCounters;
    private final GameWorld mGameWorld;
    private final int mPlayerId;
    private final Camera mCamera;
    private Stage mHudStage;
    private ScreenViewport mHudViewport;
    private WidgetGroup mHud;
    private Label mLapLabel;
    private Label mSpeedLabel;
    private Label mDebugLabel;

    HudBridge mHudBridge = new HudBridge() {
        private final Vector3 mWorldVector = new Vector3();
        private final Vector2 mHudVector = new Vector2();
        @Override
        public Vector2 toHudCoordinate(float x, float y) {
            mWorldVector.x = x;
            mWorldVector.y = y;
            mWorldVector.z = 0;
            Vector3 vector = mCamera.project(mWorldVector);
            mHudVector.x = vector.x;
            mHudVector.y = vector.y;
            return mHudVector;
        }

        @Override
        public Stage getStage() {
            return mHud.getStage();
        }
    };

    public Hud(Assets assets, GameWorld gameWorld, Batch batch, int playerId, Camera camera, PerformanceCounters performanceCounters) {
        mGameWorld = gameWorld;
        mPlayerId = playerId;
        mCamera = camera;
        mPerformanceCounters = performanceCounters;
        mHudViewport = new ScreenViewport();
        mHudStage = new Stage(mHudViewport, batch);
        Gdx.input.setInputProcessor(mHudStage);

        Skin skin = assets.skin;
        mHud = new WidgetGroup();

        mLapLabel = new Label("0:00.0", skin);
        mSpeedLabel = new Label("0", skin);
        mHud.addActor(mLapLabel);
        mHud.addActor(mSpeedLabel);
        mHud.setHeight(mLapLabel.getHeight());

        if (TheGame.getPreferences().getBoolean("debug/showDebugHud", false)
                && mPerformanceCounters != null) {
            mDebugLabel = new Label("D", skin, "small");
            mHudStage.addActor(mDebugLabel);
        }
        mHudStage.addActor(mHud);
    }

    public Stage getStage() {
        return mHudStage;
    }

    public HudBridge getHudBridge() {
        return mHudBridge;
    }

    public void act(float delta) {
        mHudStage.act(delta);
        updateHud();
    }

    public void draw() {
        mHudViewport.apply(true);
        mHudStage.draw();
    }

    public void setScreenRect(int x, int y, int width, int height) {
        mHudViewport.setScreenBounds(x, y, width, height);
        mHudViewport.setWorldSize(width, height);
    }

    private static com.badlogic.gdx.utils.StringBuilder sDebugSB = new StringBuilder();

    private void updateHud() {
        Racer racer = mGameWorld.getPlayerRacer(mPlayerId);
        int lapCount = Math.max(racer.getLapPositionComponent().getLapCount(), 1);
        int totalLapCount = mGameWorld.getMapInfo().getTotalLapCount();
        int rank = mGameWorld.getPlayerRank();
        mLapLabel.setText(String.format("Lap: %d/%d Rank: %d", lapCount, totalLapCount, rank));
        mLapLabel.setPosition(5, 0);

        mSpeedLabel.setText(StringUtils.formatSpeed(racer.getVehicle().getSpeed()));
        mSpeedLabel.setPosition(mHudViewport.getScreenWidth() - mSpeedLabel.getPrefWidth() - 5, 0);

        mHud.setPosition(0, mHudViewport.getScreenHeight() - mHud.getHeight() - 5);

        if (mDebugLabel != null) {
            sDebugSB.setLength(0);
            sDebugSB.append("objCount: ").append(mGameWorld.getActiveGameObjects().size).append('\n');
            sDebugSB.append("FPS: ").append(Gdx.graphics.getFramesPerSecond()).append('\n');
            for (PerformanceCounter counter : mPerformanceCounters.counters) {
                sDebugSB.append(counter.name).append(": ")
                        .append(String.valueOf((int) (counter.time.value * 1000)))
                        .append(" | ")
                        .append(String.valueOf((int) (counter.load.value * 100)))
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

}
