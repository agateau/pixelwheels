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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    enum RecordTableColumn {
        RANK,
        RACER,
        TIME
    }

    private final TableRowCreator mTableRowCreator =
            new TableRowCreator(RecordTableColumn.values().length) {
                @SuppressWarnings("rawtypes")
                @Override
                protected Cell createCell(Table table, int columnIdx, String value, String style) {
                    RecordTableColumn column = RecordTableColumn.values()[columnIdx];
                    //noinspection rawtypes
                    Cell cell = null;
                    switch (column) {
                        case RANK:
                            cell = table.add(createBestIndicatorImage(value));
                            cell.right();
                            break;
                        case RACER:
                            cell = table.add(value, style);
                            cell.left().growX();
                            break;
                        case TIME:
                            cell = table.add(value, style);
                            cell.right();
                            break;
                    }
                    return cell;
                }

                private Image createBestIndicatorImage(String idx) {
                    TextureRegion region =
                            mGame.getAssets().ui.atlas.findRegion("best-" + idx + "-small");
                    Image image = new Image(region);
                    image.pack();
                    return image;
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
        mTrackSelector.setColumnCount(3);
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
            updateUnlockedTrackDetails(track);
        } else {
            updateLockedTrackDetails(track);
        }
        root.layout();
    }

    private void updateLockedTrackDetails(Track track) {
        mTrackNameLabel.setText("[Locked]");

        mUnlockHintLabel.setVisible(true);
        mUnlockHintLabel.setText(mGame.getRewardManager().getUnlockText(track));

        mLapRecordsLabel.setVisible(false);
        mTotalRecordsLabel.setVisible(false);
        mLapRecordsTable.clearChildren();
        mTotalRecordsTable.clearChildren();
    }

    private void updateUnlockedTrackDetails(Track track) {
        mTrackNameLabel.setText(track.getMapName());
        mTrackNameLabel.pack();

        mUnlockHintLabel.setVisible(false);

        mLapRecordsLabel.setVisible(true);
        mTotalRecordsLabel.setVisible(true);
        updateRecordLabel(mLapRecordsTable, track, TrackStats.ResultType.LAP);
        updateRecordLabel(mTotalRecordsTable, track, TrackStats.ResultType.TOTAL);
    }

    private static final Comparator<TrackResult> sTrackResultComparator =
            (t1, t2) -> Float.compare(t1.value, t2.value);

    private void updateRecordLabel(Table table, Track track, TrackStats.ResultType resultType) {
        TrackStats stats = mGame.getGameStats().getTrackStats(track);
        ArrayList<TrackResult> records = new ArrayList<>();
        records.addAll(stats.get(resultType));
        records.addAll(track.getDefaultTrackRecords(resultType));
        Collections.sort(records, sTrackResultComparator);
        while (records.size() > TrackStats.RECORD_COUNT) {
            records.remove(records.size() - 1);
        }

        table.clearChildren();
        mTableRowCreator.setTable(table);
        for (int idx = 0, n = records.size(); idx < n; ++idx) {
            TrackResult record = records.get(idx);
            mTableRowCreator.addRow(
                    String.valueOf(idx + 1),
                    record.vehicle,
                    StringUtils.formatRaceTime(record.value));
        }
        table.setHeight(table.getPrefHeight());
    }
}
