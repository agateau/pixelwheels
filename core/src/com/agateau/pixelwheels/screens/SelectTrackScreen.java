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
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.stats.TrackResult;
import com.agateau.pixelwheels.stats.TrackStats;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.ui.TableRowCreator;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;
import java.util.Locale;

/** Select your track */
public class SelectTrackScreen extends PwStageScreen {
    private final PwGame mGame;
    private final Listener mListener;
    private TrackSelector mTrackSelector;
    private Label mTrackNameLabel;
    private Label mUnlockHintLabel;
    private Label mLapRecordsLabel;
    private Table mLapRecordsTable;
    private Label mTotalRecordsLabel;
    private Table mTotalRecordsTable;
    private AnchorGroup root;

    private final TableRowCreator mTableRowCreator =
            new TableRowCreator(3) {
                @Override
                protected Cell<Label> createCell(
                        Table table, int column, String value, String style) {
                    Cell<Label> cell = table.add(value, style);
                    if (column == 1) {
                        cell.left().growX();
                    } else {
                        cell.right();
                    }
                    return cell;
                }
            };
    private Button mNextButton;

    public interface Listener {
        void onBackPressed();

        void onTrackSelected(Track track);
    }

    public SelectTrackScreen(PwGame game, Listener listener) {
        super(game.getAssets().ui);
        mGame = game;
        mListener = listener;
        mTableRowCreator.setRowStyle("small");
        mTableRowCreator.setSpacing(12);
        setupUi();
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectTrackScreen(mGame, mListener));
            }
        };
    }

    private void setupUi() {
        UiBuilder builder = UiUtils.createUiBuilder(mGame.getAssets());

        root = (AnchorGroup) builder.build(FileUtils.assets("screens/selecttrack.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mTrackNameLabel = builder.getActor("trackNameLabel");
        mUnlockHintLabel = builder.getActor("unlockHintLabel");
        mLapRecordsLabel = builder.getActor("lapRecordsLabel");
        mLapRecordsTable = builder.getActor("lapRecordsTable");
        mTotalRecordsLabel = builder.getActor("totalRecordsLabel");
        mTotalRecordsTable = builder.getActor("totalRecordsTable");

        Menu menu = builder.getActor("menu");

        createTrackSelector(menu);
        updateTrackDetails(mTrackSelector.getCurrent());

        builder.getActor("backButton")
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });

        mNextButton = builder.getActor("nextButton");
        mNextButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        next();
                    }
                });

        updateNextButton();
    }

    private void updateNextButton() {
        mNextButton.setDisabled(!mTrackSelector.isCurrentItemEnabled());
    }

    private void createTrackSelector(Menu menu) {
        Assets assets = mGame.getAssets();

        mTrackSelector = new TrackSelector(menu);
        mTrackSelector.setColumnCount(4);
        mTrackSelector.init(assets, mGame.getRewardManager());
        mTrackSelector.setCurrent(assets.findTrackById(mGame.getConfig().track));
        menu.addItem(mTrackSelector);

        mTrackSelector.setSelectionListener(
                new GridMenuItem.SelectionListener<Track>() {
                    @Override
                    public void currentChanged(Track track, int index) {
                        updateTrackDetails(track);
                        updateNextButton();
                    }

                    @Override
                    public void selectionConfirmed() {
                        next();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        mListener.onBackPressed();
    }

    private void saveSelectedMap() {
        mGame.getConfig().track = mTrackSelector.getCurrent().getId();
        mGame.getConfig().flush();
    }

    private void next() {
        if (!mTrackSelector.isCurrentItemEnabled()) {
            return;
        }
        saveSelectedMap();
        mListener.onTrackSelected(mTrackSelector.getCurrent());
    }

    private void updateTrackDetails(Track track) {
        if (mGame.getRewardManager().isTrackUnlocked(track)) {
            mTrackNameLabel.setText(track.getMapName());
            mTrackNameLabel.pack();

            mUnlockHintLabel.setVisible(false);

            mLapRecordsLabel.setVisible(true);
            mTotalRecordsLabel.setVisible(true);
            TrackStats stats = mGame.getGameStats().getTrackStats(track);
            updateRecordLabel(mLapRecordsTable, stats.get(TrackStats.ResultType.LAP));
            updateRecordLabel(mTotalRecordsTable, stats.get(TrackStats.ResultType.TOTAL));
        } else {
            mTrackNameLabel.setText("[Locked]");

            mUnlockHintLabel.setVisible(true);
            mUnlockHintLabel.setText(mGame.getRewardManager().getUnlockText(track));

            mLapRecordsLabel.setVisible(false);
            mTotalRecordsLabel.setVisible(false);
            mLapRecordsTable.clearChildren();
            mTotalRecordsTable.clearChildren();
        }
        root.layout();
    }

    private void updateRecordLabel(Table table, ArrayList<TrackResult> results) {
        table.clearChildren();
        mTableRowCreator.setTable(table);
        for (int idx = 0, n = results.size(); idx < n; ++idx) {
            TrackResult result = results.get(idx);
            mTableRowCreator.addRow(
                    String.format(Locale.US, "%d", idx + 1),
                    result.vehicle,
                    StringUtils.formatRaceTime(result.value));
        }
        table.setHeight(table.getPrefHeight());
    }
}
