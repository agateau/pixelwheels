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

import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.gamesetup.ChampionshipMaestro;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.racer.LapPositionComponent;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.ui.AnimatedImage;
import com.agateau.ui.TableRowCreator;
import com.agateau.ui.animscript.AnimScript;
import com.agateau.ui.animscript.AnimScriptLoader;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/** Appears on top of RaceScreen at the end of the race */
public class FinishedOverlay extends Overlay {
    private static final int RANK_CHANGE_COLUMN_SIZE = 16;
    private static final float POINTS_INCREASE_SOUND_VOLUME = 1f;

    interface PageCreator {
        Actor createPage();
    }

    private enum TableType {
        QUICK_RACE,
        CHAMPIONSHIP_RACE,
        CHAMPIONSHIP_TOTAL
    }

    private static class PointsAnimInfo {
        Label label;
        int points;
        int delta = 0;
    }

    private static class RecordAnimInfo {
        final Label label;
        final int rank;

        private RecordAnimInfo(Cell<Label> labelCell, int rank) {
            this.label = labelCell.getActor();
            this.rank = rank;
        }
    }

    private static final Comparator<Racer> sRacerComparator =
            (racer1, racer2) -> {
                GameInfo.Entrant entrant1 = racer1.getEntrant();
                GameInfo.Entrant entrant2 = racer2.getEntrant();
                // Highest points first
                int deltaPoints = entrant2.getPoints() - entrant1.getPoints();
                if (deltaPoints != 0) {
                    return deltaPoints;
                }
                // Lowest race time first
                return Float.compare(entrant1.getRaceTime(), entrant2.getRaceTime());
            };

    private final PwGame mGame;
    private final RaceScreen mRaceScreen;
    private final Array<Racer> mRacers;
    private final Array<Animation<TextureRegion>> mRankChangeAnimations = new Array<>();
    private final List<PageCreator> mPageCreators = new LinkedList<>();

    private float mFirstPointsIncreaseInterval = 1f;
    private float mPointsIncreaseInterval = 0.3f;
    private int mBestIndicatorWidth = 0;
    private float mBestIndicatorMargin = 0;

    enum RaceColumn {
        RANK,
        RACER,
        BEST_LAP_TIME,
        TOTAL_TIME,
        POINTS // Championship race only
    }

    private final TableRowCreator mQuickRaceRowCreator =
            // - 1 because we don't show the Points column in quick race mode
            new TableRowCreator(RaceColumn.values().length - 1) {
                @Override
                protected Cell<Label> createCell(
                        Table table, int columnIdx, String value, String style) {
                    Cell<Label> cell = table.add(value, style);
                    RaceColumn column = RaceColumn.values()[columnIdx];
                    switch (column) {
                        case RACER:
                            cell.left().expandX();
                            break;
                        case BEST_LAP_TIME:
                        case TOTAL_TIME:
                            cell.padLeft(mBestIndicatorWidth + mBestIndicatorMargin);
                            cell.right();
                            break;
                        default:
                            cell.right();
                            break;
                    }
                    return cell;
                }
            };

    private final TableRowCreator mChampionshipRaceRowCreator =
            new TableRowCreator(RaceColumn.values().length) {
                @Override
                protected Cell<Label> createCell(
                        Table table, int columnIdx, String value, String style) {
                    Cell<Label> cell = table.add(value, style);
                    RaceColumn column = RaceColumn.values()[columnIdx];
                    switch (column) {
                        case RACER:
                            cell.left().expandX();
                            break;
                        case BEST_LAP_TIME:
                        case TOTAL_TIME:
                            cell.padLeft(mBestIndicatorWidth + mBestIndicatorMargin);
                            cell.right();
                            break;
                        default:
                            cell.right();
                            break;
                    }
                    return cell;
                }
            };

    enum ChampionshipTotalColumn {
        RANK,
        RACER,
        RANK_CHANGE,
        TOTAL_TIME,
        POINTS
    }

    private final TableRowCreator mChampionshipTotalRowCreator =
            new TableRowCreator(ChampionshipTotalColumn.values().length) {
                @SuppressWarnings("rawtypes")
                @Override
                protected Cell createCell(Table table, int column, String value, String style) {
                    Cell cell;
                    if (column == ChampionshipTotalColumn.RANK_CHANGE.ordinal()) {
                        cell = table.add(new AnimatedImage());
                        cell.size(RANK_CHANGE_COLUMN_SIZE);
                    } else {
                        cell = table.add(value, style);
                    }
                    if (column == ChampionshipTotalColumn.RACER.ordinal()) {
                        cell.left().expandX();
                    } else {
                        cell.right();
                    }
                    return cell;
                }
            };
    private final Array<PointsAnimInfo> mPointsAnimInfos = new Array<>();
    private final Array<RecordAnimInfo> mRecordAnimInfos = new Array<>();

    private Sound mPointsIncreaseSound;
    private final Timer.Task mIncreasePointsTask =
            new Timer.Task() {
                @Override
                public void run() {
                    boolean done = true;
                    for (PointsAnimInfo info : mPointsAnimInfos) {
                        if (info.delta == 0) {
                            continue;
                        }
                        ++info.points;
                        --info.delta;
                        mGame.getAudioManager()
                                .play(mPointsIncreaseSound, POINTS_INCREASE_SOUND_VOLUME);
                        if (info.delta > 0) {
                            done = false;
                        }
                    }
                    if (!done) {
                        schedulePointsIncrease(mPointsIncreaseInterval);
                    }
                    updatePointsLabels();
                }
            };

    public FinishedOverlay(PwGame game, RaceScreen raceScreen, final Array<Racer> racers) {
        super(game.getAssets().dot);
        mGame = game;
        mRaceScreen = raceScreen;
        mRacers = racers;
        new PwRefreshHelper(mGame, this) {
            @Override
            protected void refresh() {
                setupUi();
            }
        };
        setupUi();
    }

    private void setupUi() {
        mPointsIncreaseSound = mGame.getAssets().soundAtlas.get("points-increase");
        mBestIndicatorWidth = mGame.getAssets().ui.atlas.findRegion("best-1").getRegionWidth();
        fillPageCreators();
        showNextPage();
    }

    private void fillPageCreators() {
        mPageCreators.clear();
        if (isChampionship()) {
            mPageCreators.add(() -> createTablePage(TableType.CHAMPIONSHIP_RACE));
            mPageCreators.add(() -> createTablePage(TableType.CHAMPIONSHIP_TOTAL));
        } else {
            mPageCreators.add(() -> createTablePage(TableType.QUICK_RACE));
        }
    }

    private void showNextPage() {
        mIncreasePointsTask.cancel();
        PageCreator creator = mPageCreators.remove(0);
        setContent(creator.createPage());
    }

    private Actor createTablePage(TableType tableType) {
        UiBuilder builder = new UiBuilder(mGame.getAssets().ui.atlas, mGame.getAssets().ui.skin);
        if (!isChampionship()) {
            builder.defineVariable("quickRace");
        }
        if (tableType != TableType.CHAMPIONSHIP_TOTAL && didPlayerBreakRecord()) {
            builder.defineVariable("recordBroken");
        }
        HashMap<Racer, Integer> oldRankMap = null;
        if (tableType == TableType.CHAMPIONSHIP_TOTAL) {
            mRacers.sort(sRacerComparator);
            ChampionshipMaestro maestro = (ChampionshipMaestro) mGame.getMaestro();
            if (!maestro.isFirstTrack()) {
                oldRankMap = createOldRankMap();
            }
        }

        Actor content = builder.build(FileUtils.assets("screens/finishedoverlay.gdxui"));
        mFirstPointsIncreaseInterval = builder.getFloatConfigValue("firstPointsIncreaseInterval");
        mPointsIncreaseInterval = builder.getFloatConfigValue("pointsIncreaseInterval");
        mBestIndicatorMargin = builder.getFloatConfigValue("bestIndicatorMargin");

        loadRankChangeAnimations(builder);
        Table table = builder.getActor("scrollableTable");

        Label titleLabel = builder.getActor("titleLabel");
        titleLabel.setText(
                tableType == TableType.CHAMPIONSHIP_TOTAL
                        ? "Championship Rankings"
                        : "Race Results");
        titleLabel.pack();

        fillMenu(builder);
        fillTable(table, tableType, oldRankMap);
        if (!mRecordAnimInfos.isEmpty()) {
            // Create animations after the Overlay is at its final position, to ensure the table
            // cell coordinates are final
            Timer.schedule(
                    new Timer.Task() {
                        @Override
                        public void run() {
                            createRecordAnimations(builder, (Group) content);
                        }
                    },
                    Overlay.IN_DURATION);
        }
        return content;
    }

    private void loadRankChangeAnimations(UiBuilder builder) {
        float speed = builder.getFloatConfigValue("rankChangeAnimationSpeed");
        mRankChangeAnimations.clear();
        for (String name : new String[] {"rank-down", "rank-same", "rank-up"}) {
            Array<TextureAtlas.AtlasRegion> regions = mGame.getAssets().ui.atlas.findRegions(name);
            Animation<TextureRegion> animation = new Animation<>(speed, regions);
            mRankChangeAnimations.add(animation);
        }
    }

    private static int getOldPointsForRacer(Racer racer) {
        GameInfo.Entrant entrant = racer.getEntrant();
        return entrant.getPoints() - entrant.getLastRacePoints();
    }

    private HashMap<Racer, Integer> createOldRankMap() {
        HashMap<Racer, Integer> oldRank = new HashMap<>();
        Array<Racer> racers = new Array<>(mRacers);
        racers.sort(
                (Racer racer1, Racer racer2) ->
                        -Integer.compare(
                                getOldPointsForRacer(racer1), getOldPointsForRacer(racer2)));
        for (int idx = 0; idx < racers.size; ++idx) {
            oldRank.put(racers.get(idx), idx);
        }
        return oldRank;
    }

    private void fillMenu(UiBuilder builder) {
        if (!isChampionship()) {
            builder.getActor("restartButton")
                    .addListener(
                            new MenuItemListener() {
                                @Override
                                public void triggered() {
                                    mRaceScreen.getListener().onRestartPressed();
                                }
                            });
        }
        builder.getActor("continueButton")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                if (mPageCreators.isEmpty()) {
                                    mRaceScreen.getListener().onNextTrackPressed();
                                } else {
                                    showNextPage();
                                }
                            }
                        });
    }

    private TableRowCreator getRowCreatorForTable(TableType tableType) {
        switch (tableType) {
            case QUICK_RACE:
                return mQuickRaceRowCreator;
            case CHAMPIONSHIP_RACE:
                return mChampionshipRaceRowCreator;
            case CHAMPIONSHIP_TOTAL:
                return mChampionshipTotalRowCreator;
        }
        // Never reached
        throw new AssertionError();
    }

    private void fillTable(Table table, TableType tableType, HashMap<Racer, Integer> oldRankMap) {
        mPointsAnimInfos.clear();
        mRecordAnimInfos.clear();

        // Init our table
        TableRowCreator rowCreator = getRowCreatorForTable(tableType);
        rowCreator.setTable(table);
        rowCreator.setSpacing(24);

        // Create header row
        switch (tableType) {
            case QUICK_RACE:
                rowCreator.addHeaderRow("#", "Racer", "Best lap", "Total time");
                break;
            case CHAMPIONSHIP_RACE:
                rowCreator.addHeaderRow("#", "Racer", "Best lap", "Total time", "Points");
                break;
            case CHAMPIONSHIP_TOTAL:
                rowCreator.addHeaderRow("#", "Racer", "", "Race time", "Points");
                break;
        }

        // Fill table
        for (int idx = 0; idx < mRacers.size; ++idx) {
            Racer racer = mRacers.get(idx);
            GameInfo.Entrant entrant = racer.getEntrant();
            rowCreator.setRowStyle(UiUtils.getEntrantRowStyle(entrant));

            switch (tableType) {
                case QUICK_RACE:
                case CHAMPIONSHIP_RACE:
                    createRaceRow(tableType, rowCreator, idx, racer);
                    break;
                case CHAMPIONSHIP_TOTAL:
                    createChampionshipTotalRow(oldRankMap, idx, racer);
                    break;
            }

            if (tableType != TableType.QUICK_RACE) {
                // add PointsAnimInfo for row
                // -1 is the last column: the Points column
                Cell<Label> pointsCell = rowCreator.getCreatedRowCell(-1);
                PointsAnimInfo info = new PointsAnimInfo();
                info.label = pointsCell.getActor();
                if (tableType == TableType.CHAMPIONSHIP_RACE) {
                    info.delta = entrant.getLastRacePoints();
                    info.points = entrant.getPoints() - info.delta;
                } else {
                    info.points = entrant.getPoints();
                }
                mPointsAnimInfos.add(info);
            }

            // For player rows in a race table, get info to show new record
            if (tableType != TableType.CHAMPIONSHIP_TOTAL && racer.getRecordRanks().brokeRecord()) {
                Racer.RecordRanks ranks = racer.getRecordRanks();
                if (ranks.lapRecordRank >= 0) {
                    Cell<Label> cell =
                            rowCreator.getCreatedRowCell(RaceColumn.BEST_LAP_TIME.ordinal());
                    RecordAnimInfo info = new RecordAnimInfo(cell, ranks.lapRecordRank);
                    mRecordAnimInfos.add(info);
                }
                if (ranks.totalRecordRank >= 0) {
                    Cell<Label> cell =
                            rowCreator.getCreatedRowCell(RaceColumn.TOTAL_TIME.ordinal());
                    RecordAnimInfo info = new RecordAnimInfo(cell, ranks.totalRecordRank);
                    mRecordAnimInfos.add(info);
                }
            }
        }

        // Animate points if needed
        if (tableType != TableType.QUICK_RACE) {
            updatePointsLabels();
            if (tableType == TableType.CHAMPIONSHIP_RACE) {
                schedulePointsIncrease(mFirstPointsIncreaseInterval);
            }
        }
    }

    /** Create a row for either QUICK_RACE or CHAMPIONSHIP_RACE */
    private void createRaceRow(
            TableType tableType, TableRowCreator rowCreator, int idx, Racer racer) {
        String name = getRacerName(racer);
        String rank = StringUtils.formatRank(idx + 1);
        LapPositionComponent lapPositionComponent = racer.getLapPositionComponent();
        String bestLapTime;
        String totalTime;
        if (lapPositionComponent.getStatus() == LapPositionComponent.Status.DID_NOT_START) {
            bestLapTime = "-";
            totalTime = "-";
        } else {
            bestLapTime = StringUtils.formatRaceTime(lapPositionComponent.getBestLapTime());
            totalTime = StringUtils.formatRaceTime(lapPositionComponent.getTotalTime());
        }

        if (tableType == TableType.QUICK_RACE) {
            rowCreator.addRow(rank, name, bestLapTime, totalTime);
        } else {
            rowCreator.addRow(rank, name, bestLapTime, totalTime, "" /* points */);
        }
    }

    private void createChampionshipTotalRow(
            HashMap<Racer, Integer> oldRankMap, int idx, Racer racer) {
        String rank = StringUtils.formatRank(idx + 1);
        String name = getRacerName(racer);
        String totalTime = StringUtils.formatRaceTime(racer.getEntrant().getRaceTime());

        mChampionshipTotalRowCreator.addRow(
                rank, name, null /* rank change indicator */, totalTime, "" /* points */);

        if (oldRankMap == null) {
            return;
        }
        int oldIdx = oldRankMap.get(racer);
        Animation<TextureRegion> animation =
                mRankChangeAnimations.get((int) Math.signum(oldIdx - idx) + 1);

        Cell<AnimatedImage> rankChangeCell =
                mChampionshipTotalRowCreator.getCreatedRowCell(
                        ChampionshipTotalColumn.RANK_CHANGE.ordinal());
        rankChangeCell.getActor().setAnimation(animation);
    }

    private void schedulePointsIncrease(float interval) {
        Timer.schedule(mIncreasePointsTask, interval);
    }

    private void updatePointsLabels() {
        for (PointsAnimInfo info : mPointsAnimInfos) {
            String points;
            if (info.delta == 0) {
                points = String.valueOf(info.points);
            } else {
                points = StringUtils.format("+%d %d", info.delta, info.points);
            }
            info.label.setText(points);
        }
    }

    private boolean isChampionship() {
        return mRaceScreen.getGameType() == GameInfo.GameType.CHAMPIONSHIP;
    }

    private boolean didPlayerBreakRecord() {
        for (Racer racer : mRacers) {
            if (racer.getRecordRanks().brokeRecord()) {
                return true;
            }
        }
        return false;
    }

    private static String getRacerName(Racer racer) {
        return racer.getVehicle().getName();
    }

    private final Vector2 mTmp = new Vector2();

    private void createRecordAnimations(UiBuilder builder, Group root) {
        AnimScript script;
        try {
            script = builder.getAnimScriptConfigValue("bestIndicatorAnimation");
        } catch (AnimScriptLoader.SyntaxException e) {
            NLog.e("Error loading animscript: " + e);
            return;
        }

        for (RecordAnimInfo info : mRecordAnimInfos) {
            TextureRegion region = mGame.getAssets().ui.atlas.findRegion("best-" + (info.rank + 1));
            Image image = new Image(region);
            image.setOrigin(Align.center);
            image.pack();
            root.addActor(image);

            mTmp.set(
                    -image.getWidth() - mBestIndicatorMargin,
                    (info.label.getHeight() - image.getHeight()) / 2);
            Vector2 pos = info.label.localToAscendantCoordinates(root, mTmp);

            Action action = script.createAction();
            image.setPosition(pos.x, pos.y);
            image.addAction(action);
            action.act(0);
        }
    }
}
