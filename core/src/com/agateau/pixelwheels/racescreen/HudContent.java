/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.pixelwheels.racescreen;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.debug.DebugStringMap;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import com.badlogic.gdx.utils.StringBuilder;
import java.util.Map;

/** Various labels and actors shown on the hud */
public class HudContent {
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final Hud mHud;
    private PerformanceCounters mPerformanceCounters = null;

    private final Array<Label> mRankLabels = new Array<>();
    private final Array<Label> mLapLabels = new Array<>();
    private final Label mCountDownLabel;
    private Label mDebugLabel = null;

    private final StringBuilder mStringBuilder = new StringBuilder();

    public HudContent(Assets assets, GameWorld gameWorld, Hud hud) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mHud = hud;
        Skin skin = assets.ui.skin;

        AnchorGroup root = hud.getRoot();

        createPlayerLabels(root);

        mCountDownLabel = new Label("", skin, "hudCountDown");
        mCountDownLabel.setAlignment(Align.bottom);

        root.addPositionRule(mCountDownLabel, Anchor.BOTTOM_CENTER, root, Anchor.CENTER);
    }

    private void createPlayerLabels(AnchorGroup root) {
        Skin skin = mAssets.ui.skin;
        int playerCount = mGameWorld.getPlayerRacers().size;

        Actor topEdge = root;
        Anchor topAnchor = Anchor.TOP_RIGHT;
        float hMargin = -5;

        boolean singlePlayer = mGameWorld.getPlayerRacers().size == 1;
        for (int idx = 0; idx < playerCount; ++idx) {
            Label rankLabel = new Label("", skin, singlePlayer ? "hudRank" : "smallHudRank");
            rankLabel.setAlignment(Align.right);

            Label lapLabel = new Label("", skin, singlePlayer ? "hud" : "smallHud");
            lapLabel.setAlignment(Align.right);

            root.addPositionRule(rankLabel, Anchor.TOP_RIGHT, topEdge, topAnchor, hMargin, 0);
            root.addPositionRule(lapLabel, Anchor.TOP_RIGHT, rankLabel, Anchor.BOTTOM_RIGHT, 0, 10);

            mRankLabels.add(rankLabel);
            mLapLabels.add(lapLabel);

            topAnchor = Anchor.BOTTOM_RIGHT;
            topEdge = lapLabel;
            hMargin = 0;
        }
    }

    public void setPerformanceCounters(PerformanceCounters performanceCounters) {
        mPerformanceCounters = performanceCounters;
        mDebugLabel = new Label("D", mAssets.ui.skin, "tiny");

        AnchorGroup root = mHud.getRoot();
        root.addPositionRule(mDebugLabel, Anchor.CENTER_LEFT, root, Anchor.CENTER_LEFT);
    }

    public void createPauseButton(ClickListener clickListener) {
        HudButton button = new HudButton(mAssets, mHud, "pause");
        button.addListener(clickListener);
        AnchorGroup root = mHud.getRoot();
        root.addPositionRule(button, Anchor.TOP_LEFT, root, Anchor.TOP_LEFT);
    }

    @SuppressWarnings("UnusedParameters")
    public void act(float delta) {
        updateLabels();
        updateCountDownLabel();
        if (mDebugLabel != null) {
            updateDebugLabel();
        }
    }

    private void updateLabels() {
        int idx = 0;
        boolean singlePlayer = mGameWorld.getPlayerRacers().size == 1;
        for (Racer racer : mGameWorld.getPlayerRacers()) {
            Label lapLabel = mLapLabels.get(idx);
            Label rankLabel = mRankLabels.get(idx);

            int lapCount = Math.max(racer.getLapPositionComponent().getLapCount(), 1);
            int totalLapCount = mGameWorld.getTrack().getTotalLapCount();
            int rank = mGameWorld.getRacerRank(racer);

            mStringBuilder.setLength(0);
            if (!singlePlayer) {
                mStringBuilder.append("P").append(idx + 1).append(": ");
            }

            mStringBuilder.append(rank).append(StringUtils.getRankSuffix(rank));
            rankLabel.setText(mStringBuilder);
            rankLabel.pack();

            mStringBuilder.setLength(0);
            mStringBuilder.append("Lap ").append(lapCount).append('/').append(totalLapCount);
            lapLabel.setText(mStringBuilder);
            lapLabel.pack();

            ++idx;
        }
    }

    private void updateCountDownLabel() {
        CountDown countDown = mGameWorld.getCountDown();
        if (countDown.isFinished()) {
            mCountDownLabel.setVisible(false);
            return;
        }
        float alpha = countDown.getPercent();
        int count = countDown.getValue();

        mCountDownLabel.setColor(1, 1, 1, alpha);

        String text = count > 0 ? String.valueOf(count) : "GO!";
        mCountDownLabel.setText(text);
    }

    private static final StringBuilder sDebugSB = new StringBuilder();

    private void updateDebugLabel() {
        sDebugSB.setLength(0);
        sDebugSB.append("objCount: ").append(mGameWorld.getActiveGameObjects().size).append('\n');
        sDebugSB.append("FPS: ").append(Gdx.graphics.getFramesPerSecond()).append('\n');
        for (PerformanceCounter counter : mPerformanceCounters.counters) {
            sDebugSB.append(counter.name)
                    .append(": ")
                    .append(String.valueOf((int) (counter.time.value * 1000)))
                    .append(" | ")
                    .append(String.valueOf((int) (counter.load.value * 100)))
                    .append("%\n");
        }
        for (Map.Entry<String, String> entry : DebugStringMap.getMap().entrySet()) {
            sDebugSB.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        mDebugLabel.setText(sDebugSB);
    }
}
