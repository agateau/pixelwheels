/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
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
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.map.TrackRecords;
import com.agateau.pixelwheels.map.TrackResult;
import com.agateau.pixelwheels.map.TrackStats;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Select your track
 */
public class SelectTrackScreen extends PwStageScreen {
    private final PwGame mGame;
    private final Listener mListener;
    private TrackSelector mTrackSelector;
    private Label mLapRecordsLabel;
    private Label mTotalRecordsLabel;

    public interface Listener {
        void onBackPressed();
        void onTrackSelected(Track track);
    }

    public SelectTrackScreen(PwGame game, Listener listener) {
        super(game.getAssets().ui);
        mGame = game;
        mListener = listener;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectTrackScreen(mGame, mListener));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/selecttrack.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mLapRecordsLabel = builder.getActor("lapRecordsLabel");
        mTotalRecordsLabel = builder.getActor("totalRecordsLabel");

        Menu menu = builder.getActor("menu");

        mTrackSelector = new TrackSelector(menu);
        mTrackSelector.setColumnCount(2);
        mTrackSelector.init(assets);
        mTrackSelector.setCurrent(assets.findTrackById(mGame.getConfig().track));
        menu.addItem(mTrackSelector);

        mTrackSelector.addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                next();
            }
        });

        mTrackSelector.setSelectionListener(new GridMenuItem.SelectionListener<Track>() {
            @Override
            public void selectedChanged(Track item, int index) {
            }

            @Override
            public void currentChanged(Track track, int index) {
                updateTrackRecords(track);
            }
        });

        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onBackPressed();
            }
        });

        updateTrackRecords(mGame.getAssets().tracks.get(0));
    }

    @Override
    public void onBackPressed() {
        mListener.onBackPressed();
    }

    private void saveSelectedMap() {
        mGame.getConfig().track = mTrackSelector.getSelected().getId();
        mGame.getConfig().flush();
    }

    private void next() {
        saveSelectedMap();
        mListener.onTrackSelected(mTrackSelector.getSelected());
    }

    private void updateTrackRecords(Track track) {
        TrackStats stats = mGame.getTrackStats();
        TrackRecords lapRecords = stats.getRecords(track.getId(), TrackStats.ResultType.LAP);
        TrackRecords totalRecords = stats.getRecords(track.getId(), TrackStats.ResultType.TOTAL);
        updateRecordLabel(mLapRecordsLabel, lapRecords);
        updateRecordLabel(mTotalRecordsLabel, totalRecords);
    }

    private final StringBuilder mStringBuilder = new StringBuilder();
    private void updateRecordLabel(Label label, TrackRecords records) {
        mStringBuilder.setLength(0);
        ArrayList<TrackResult> results = records.getResults();
        if (results.isEmpty()) {
            label.setText("No record yet");
            return;
        }
        for (int idx = 0, n = results.size(); idx < n; ++idx) {
            TrackResult result = results.get(idx);
            if (idx > 0) {
                mStringBuilder.append('\n');
            }
            String timeStr = StringUtils.formatRaceTime(result.value);
            String line = String.format(Locale.US, "%d %s %s", idx + 1, timeStr, result.racer);
            mStringBuilder.append(line);
        }
        label.setText(mStringBuilder.toString());
    }
}
