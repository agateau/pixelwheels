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

import static com.agateau.translations.Translator.tr;
import static com.agateau.translations.Translator.trc;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.stats.TrackResult;
import com.agateau.pixelwheels.stats.TrackStats;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.TableRowCreator;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.CornerMenuButton;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;

/** Select your track */
public class SelectTrackScreen extends PwStageScreen {
    private final PwGame mGame;
    private final Listener mListener;
    private ChampionshipSelector mChampionshipSelector;
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
    private CornerMenuButton mNextButton;

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

        Track track = getCurrentTrack();

        createChampionshipSelector(menu, track);
        createTrackSelector(menu, track);
        updateTrackDetails(track);

        menu.addBackButton()
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });

        mNextButton = menu.addNextButton();
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

    private Track getCurrentTrack() {
        Assets assets = mGame.getAssets();
        Track track = assets.findTrackById(mGame.getConfig().track);
        if (track == null) {
            track = assets.championships.get(0).getTracks().get(0);
        }
        return track;
    }

    private void createChampionshipSelector(Menu menu, Track currentTrack) {
        Assets assets = mGame.getAssets();

        mChampionshipSelector = new ChampionshipSelector(menu);
        mChampionshipSelector.setColumnCount(3);
        mChampionshipSelector.init(assets, mGame.getRewardManager());
        mChampionshipSelector.setCurrent(currentTrack.getChampionship());
        menu.addItem(mChampionshipSelector);

        mChampionshipSelector.setSelectionListener(
                new GridMenuItem.SelectionListener<Championship>() {
                    @Override
                    public void currentChanged(Championship item, int index) {}

                    @Override
                    public void selectionConfirmed() {
                        mTrackSelector.setCurrentChampionship(mChampionshipSelector.getSelected());
                        menu.setCurrentItem(mTrackSelector);
                    }
                });
    }

    private void createTrackSelector(Menu menu, Track currentTrack) {
        Assets assets = mGame.getAssets();

        mTrackSelector = new TrackSelector(menu);
        mTrackSelector.setMenuStyle(assets.ui.skin.get("large", Menu.MenuStyle.class));
        mTrackSelector.setColumnCount(3);
        mTrackSelector.init(assets, mGame.getRewardManager(), currentTrack.getChampionship());
        mTrackSelector.setCurrent(currentTrack);
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
        mTrackNameLabel.setText(tr("[Locked]"));

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

    private void updateRecordLabel(Table table, Track track, TrackStats.ResultType resultType) {
        TrackStats stats = mGame.getGameStats().getTrackStats(track);
        ArrayList<TrackResult> records = stats.get(resultType);

        table.clearChildren();
        mTableRowCreator.setTable(table);
        for (int idx = 0, n = records.size(); idx < n; ++idx) {
            TrackResult record = records.get(idx);
            mTableRowCreator.addRow(
                    String.valueOf(idx + 1),
                    getVehicleName(record.vehicle),
                    StringUtils.formatRaceTime(record.value));
        }
        table.setHeight(table.getPrefHeight());
    }

    private String getVehicleName(String vehicleId) {
        if (vehicleId.equals(TrackStats.DEFAULT_RECORD_VEHICLE)) {
            return trc("CPU", "vehicle-record-placeholder");
        }
        VehicleDef vehicleDef = mGame.getAssets().findVehicleDefById(vehicleId);
        // vehicleDef can be null for records established when record.vehicle was the
        // name of the vehicle
        return vehicleDef == null ? vehicleId : vehicleDef.getName();
    }
}
