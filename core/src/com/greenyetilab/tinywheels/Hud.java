package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import com.badlogic.gdx.utils.StringBuilder;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;
import com.greenyetilab.utils.anchor.SizeRule;

import java.util.Map;

/**
 * Hud showing player info during race
 */
class Hud {
    private final PerformanceCounters mPerformanceCounters;
    private final GameWorld mGameWorld;
    private final int mPlayerId;
    private AnchorGroup mRoot;
    private WidgetGroup mTopRow;
    private Label mLapLabel;
    private Label mSpeedLabel;
    private Label mFinishedLabel;
    private Label mDebugLabel;

    public Hud(Assets assets, GameWorld gameWorld, Stage stage, int playerId, PerformanceCounters performanceCounters) {
        mGameWorld = gameWorld;
        mPlayerId = playerId;
        mPerformanceCounters = performanceCounters;

        mRoot = new AnchorGroup();

        Skin skin = assets.skin;
        mTopRow = new WidgetGroup();

        mLapLabel = new Label("0:00.0", skin);
        mSpeedLabel = new Label("0", skin);
        mFinishedLabel = new Label("Finished!", skin);
        mFinishedLabel.setVisible(false);
        mTopRow.addActor(mLapLabel);
        mTopRow.addActor(mSpeedLabel);
        mTopRow.setHeight(mLapLabel.getHeight());

        mRoot.addPositionRule(mTopRow, Anchor.TOP_LEFT, mRoot, Anchor.TOP_LEFT, 0, -5);
        mRoot.addSizeRule(mTopRow, mRoot, 1, SizeRule.IGNORE);
        mRoot.addPositionRule(mFinishedLabel, Anchor.CENTER, mRoot, Anchor.CENTER);

        if (TheGame.getPreferences().getBoolean("debug/showDebugHud", false)
                && mPerformanceCounters != null) {
            mDebugLabel = new Label("D", skin, "small");
            stage.addActor(mDebugLabel);
        }
        stage.addActor(mRoot);
    }

    public Group getRoot() {
        return mRoot;
    }

    public void act(float delta) {
        checkFinished();
        updateHud();
    }

    public void setScreenRect(int x, int y, int width, int height) {
        mRoot.setBounds(x, y, width, height);
    }

    private void checkFinished() {
        Racer racer = mGameWorld.getPlayerRacer(mPlayerId);
        if (racer.getLapPositionComponent().hasFinishedRace() && !mFinishedLabel.isVisible() && mGameWorld.getPlayerRacers().size > 1) {
            showFinishedLabel();
        }
    }

    private void showFinishedLabel() {
        int rank = mGameWorld.getPlayerRank(mPlayerId);
        String text;
        switch (rank) {
        case 1:
            text = "1st place!";
            break;
        case 2:
            text = "2nd place!";
            break;
        case 3:
            text = "3nd place!";
            break;
        default:
            text = rank + "th place";
            break;
        }
        mFinishedLabel.setText(text);
        mFinishedLabel.setVisible(true);
    }

    private static com.badlogic.gdx.utils.StringBuilder sDebugSB = new StringBuilder();
    private void updateHud() {
        Racer racer = mGameWorld.getPlayerRacer(mPlayerId);
        int lapCount = Math.max(racer.getLapPositionComponent().getLapCount(), 1);
        int totalLapCount = mGameWorld.getMapInfo().getTotalLapCount();
        int rank = mGameWorld.getPlayerRank(mPlayerId);
        mLapLabel.setText(String.format("Lap: %d/%d Rank: %d", lapCount, totalLapCount, rank));
        mLapLabel.setPosition(5, 0);

        mSpeedLabel.setText(StringUtils.formatSpeed(racer.getVehicle().getSpeed()));
        mSpeedLabel.setPosition(mTopRow.getWidth() - mSpeedLabel.getPrefWidth() - 5, 0);

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
            mDebugLabel.setPosition(0, mTopRow.getY() - mDebugLabel.getPrefHeight());
        }
    }

}
