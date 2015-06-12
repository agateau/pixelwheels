package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
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
    private Stage mHudStage;
    private ScreenViewport mHudViewport;
    private WidgetGroup mHud;
    private Label mLapLabel;
    private Label mSpeedLabel;
    private Label mDebugLabel;

    public Hud(Assets assets, GameWorld gameWorld, Batch batch, int playerId, PerformanceCounters performanceCounters) {
        mGameWorld = gameWorld;
        mPlayerId = playerId;
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

    public void act(float delta) {
        mHudStage.act(delta);
        updateHud();
    }

    public void draw() {
        mHudStage.draw();
    }

    public void resize(int width, int height) {
        mHudViewport.update(width, height, true);
    }

    private static com.badlogic.gdx.utils.StringBuilder sDebugSB = new StringBuilder();

    private void updateHud() {
        int lapCount = Math.max(mGameWorld.getPlayerRacer().getLapPositionComponent().getLapCount(), 1);
        int totalLapCount = mGameWorld.getMapInfo().getTotalLapCount();
        int rank = mGameWorld.getPlayerRank();
        mLapLabel.setText(String.format("Lap: %d/%d Rank: %d", lapCount, totalLapCount, rank));
        mLapLabel.setPosition(5, 0);

        Vehicle vehicle = mGameWorld.getPlayerVehicle(mPlayerId);
        mSpeedLabel.setText(StringUtils.formatSpeed(vehicle.getSpeed()));
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
