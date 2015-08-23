package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import com.badlogic.gdx.utils.StringBuilder;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

import java.util.Map;

/**
 * Hud showing player info during race
 */
class Hud {
    private final static float BUTTON_SIZE_CM = 1.5f;

    private final float BUTTON_SIZE_PX;

    private final PerformanceCounters mPerformanceCounters;
    private final GameWorld mGameWorld;
    private final int mPlayerId;
    private AnchorGroup mRoot;
    private Label mLapLabel;
    private Label mSpeedLabel;
    private Label mFinishedLabel;
    private Label mDebugLabel;

    private float mZoom;

    public Hud(Assets assets, GameWorld gameWorld, Stage stage, int playerId, PerformanceCounters performanceCounters) {
        mGameWorld = gameWorld;
        mPlayerId = playerId;
        mPerformanceCounters = performanceCounters;

        mRoot = new AnchorGroup();

        BUTTON_SIZE_PX = assets.findRegion("hud-square").getRegionWidth();
        Skin skin = assets.skin;

        mLapLabel = new Label("0:00.0", skin);
        mSpeedLabel = new Label("0", skin);
        mFinishedLabel = new Label("Finished!", skin);
        mFinishedLabel.setVisible(false);

        mRoot.addPositionRule(mLapLabel, Anchor.TOP_LEFT, mRoot, Anchor.TOP_LEFT, 5, 0);
        mRoot.addPositionRule(mSpeedLabel, Anchor.TOP_RIGHT, mRoot, Anchor.TOP_RIGHT, -5, 0);
        mRoot.addPositionRule(mFinishedLabel, Anchor.CENTER, mRoot, Anchor.CENTER);

        if (TheGame.getPreferences().getBoolean("debug/showDebugHud", false)
                && mPerformanceCounters != null) {
            mDebugLabel = new Label("D", skin, "small");
            mRoot.addPositionRule(mDebugLabel, Anchor.CENTER_LEFT, mRoot, Anchor.CENTER_LEFT);
        }
        stage.addActor(mRoot);
    }

    public AnchorGroup getRoot() {
        return mRoot;
    }

    public void act(float delta) {
        updateZoom();
        checkFinished();
        updateHud();
    }

    public void setScreenRect(int x, int y, int width, int height) {
        mRoot.setBounds(x, y, width, height);
    }

    public float getZoom() {
        return mZoom;
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

        mSpeedLabel.setText(StringUtils.formatSpeed(racer.getVehicle().getSpeed()));
        mSpeedLabel.pack();

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
        }
    }

    private void updateZoom() {
        float ppc = (Gdx.graphics.getPpcX() + Gdx.graphics.getPpcY()) / 2;
        float pxSize = BUTTON_SIZE_CM * ppc;
        float stageSize = pxSize * mRoot.getStage().getWidth() / Gdx.graphics.getWidth();

        float regionSize = BUTTON_SIZE_PX;
        if (stageSize < regionSize) {
            stageSize = regionSize;
        }

        mZoom = MathUtils.floor(stageSize / regionSize);
    }
}
