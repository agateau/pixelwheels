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
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.debug.DebugStringMap;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import com.badlogic.gdx.utils.StringBuilder;
import java.util.Map;

/** Various labels and actors shown on the hud */
public class HudContent {
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final Hud mHud;
    private final Racer mRacer;
    private PerformanceCounters mPerformanceCounters = null;

    private Label mRankLabel;
    private Label mLapLabel;
    private VerticalGroup mDebugGroup = null;
    private Label mDebugLabel = null;

    private final StringBuilder mStringBuilder = new StringBuilder();

    private final String[] mRankStrings = new String[GamePlay.instance.racerCount];

    public HudContent(Assets assets, GameWorld gameWorld, Hud hud, Racer racer) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mHud = hud;
        mRacer = racer;

        AnchorGroup root = hud.getRoot();

        // Generate all possible ranks to avoid translation calls
        for (int idx = 0; idx < mRankStrings.length; ++idx) {
            mRankStrings[idx] = StringUtils.formatRankInHud(idx + 1);
        }

        createPlayerLabels(root);
    }

    public Hud getHud() {
        return mHud;
    }

    private void createPlayerLabels(AnchorGroup root) {
        Skin skin = mAssets.ui.skin;

        TextureRegion lapIconRegion = mAssets.findRegion("lap-icon");

        boolean singlePlayer = mGameWorld.getPlayerRacers().size == 1;

        mRankLabel = new Label("", skin, singlePlayer ? "hudRank" : "smallHudRank");
        mRankLabel.setAlignment(Align.right);

        mLapLabel = new Label("", skin, singlePlayer ? "hud" : "smallHud");
        mLapLabel.setAlignment(Align.right);

        Image lapIconImage = new Image(lapIconRegion);
        lapIconImage.pack();

        root.addPositionRule(mRankLabel, Anchor.TOP_RIGHT, root, Anchor.TOP_RIGHT, -5, 0);
        root.addPositionRule(mLapLabel, Anchor.TOP_RIGHT, mRankLabel, Anchor.BOTTOM_RIGHT, 0, 10);
        root.addPositionRule(
                lapIconImage, Anchor.CENTER_RIGHT, mLapLabel, Anchor.CENTER_LEFT, -8, 0);
    }

    public void initDebugHud(PerformanceCounters performanceCounters) {
        mPerformanceCounters = performanceCounters;

        mDebugGroup = new VerticalGroup();
        mDebugLabel = new Label("D", mAssets.ui.skin, "tiny");

        AnchorGroup root = mHud.getRoot();
        root.addPositionRule(mDebugGroup, Anchor.CENTER_LEFT, root, Anchor.CENTER_LEFT, 40, 0);

        mDebugGroup.addActor(mDebugLabel);
        mDebugGroup.pack();
    }

    public void addDebugActor(Actor actor) {
        mDebugGroup.addActor(actor);
        mDebugGroup.pack();
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
        if (mDebugLabel != null) {
            updateDebugLabel();
        }
    }

    private void updateLabels() {
        int lapCount = Math.max(mRacer.getLapPositionComponent().getLapCount(), 1);
        int totalLapCount = mGameWorld.getTrack().getTotalLapCount();
        int rank = mGameWorld.getRacerRank(mRacer);

        mRankLabel.setText(mRankStrings[rank - 1]);
        mRankLabel.pack();

        mStringBuilder.setLength(0);
        mStringBuilder.append(lapCount).append('/').append(totalLapCount);
        mLapLabel.setText(mStringBuilder);
        mLapLabel.pack();
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
