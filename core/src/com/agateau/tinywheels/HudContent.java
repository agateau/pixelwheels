/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import com.badlogic.gdx.utils.StringBuilder;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;

import java.util.Map;

/**
 * Various labels and actors shown on the hud
 */
public class HudContent {
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final int mPlayerId;
    private final Hud mHud;
    private PerformanceCounters mPerformanceCounters = null;

    private final Label mLapLabel;
    private final Label mFinishedLabel;
    private Label mDebugLabel = null;

    public HudContent(Assets assets, GameWorld gameWorld, Hud hud, int playerId) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mHud = hud;
        mPlayerId = playerId;
        Skin skin = assets.skin;
        mLapLabel = new Label("", skin);
        mLapLabel.setAlignment(Align.right);

        mFinishedLabel = new Label("Finished!", skin);
        mFinishedLabel.setVisible(false);

        AnchorGroup root = hud.getRoot();

        root.addPositionRule(mLapLabel, Anchor.TOP_RIGHT, root, Anchor.TOP_RIGHT, -5, 0);
        root.addPositionRule(mFinishedLabel, Anchor.CENTER, root, Anchor.CENTER);
    }

    public void setPerformanceCounters(PerformanceCounters performanceCounters) {
        mPerformanceCounters = performanceCounters;
        mDebugLabel = new Label("D", mAssets.skin, "small");

        AnchorGroup root = mHud.getRoot();
        root.addPositionRule(mDebugLabel, Anchor.CENTER_LEFT, root, Anchor.CENTER_LEFT);
    }

    public void createPauseButton(ClickListener clickListener) {
        HudButton button = new HudButton(mAssets, mHud, "action");
        button.setIcon(mAssets.findRegion("hud-icon-pause"));
        button.addListener(clickListener);
        AnchorGroup root = mHud.getRoot();
        root.addPositionRule(button, Anchor.TOP_LEFT, root, Anchor.TOP_LEFT);
    }

    public void act(float delta) {
        updateLabels();
        checkFinished();
        if (mDebugLabel != null) {
            updateDebugLabel();
        }
    }

    private void updateLabels() {
        Racer racer = mGameWorld.getPlayerRacer(mPlayerId);
        int lapCount = Math.max(racer.getLapPositionComponent().getLapCount(), 1);
        int totalLapCount = mGameWorld.getMapInfo().getTotalLapCount();
        int rank = mGameWorld.getPlayerRank(mPlayerId);
        mLapLabel.setText(String.format("%d%s\nLap %d/%d", rank, StringUtils.getRankSuffix(rank), lapCount, totalLapCount));
        mLapLabel.pack();
    }

    private static StringBuilder sDebugSB = new StringBuilder();
    private void updateDebugLabel() {
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

    private void checkFinished() {
        Racer racer = mGameWorld.getPlayerRacer(mPlayerId);
        if (racer.getLapPositionComponent().hasFinishedRace() && !mFinishedLabel.isVisible() && mGameWorld.getPlayerRacers().size > 1) {
            showFinishedLabel();
        }
    }

    private void showFinishedLabel() {
        int rank = mGameWorld.getPlayerRank(mPlayerId);
        String suffix = StringUtils.getRankSuffix(rank);
        String text;
        if (rank <= 3) {
            text = String.format("%d%s place!", rank, suffix);
        } else {
            text = String.format("%d%s place", rank, suffix);
        }
        mFinishedLabel.setText(text);
        mFinishedLabel.setVisible(true);
    }
}
