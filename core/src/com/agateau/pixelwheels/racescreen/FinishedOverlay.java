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
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/** Appears on top of RaceScreen at the end of the race */
public class FinishedOverlay extends Overlay {
    private float mFirstPointsIncreaseInterval = 1f;
    private float mPointsIncreaseInterval = 0.3f;
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
    private final TableRowCreator mQuickRaceRowCreator =
            new TableRowCreator(4) {
                @Override
                protected Cell<Label> createCell(
                        Table table, int column, String value, String style) {
                    Cell<Label> cell = table.add(value, style);
                    if (column == 1) {
                        cell.left().expandX();
                    } else {
                        cell.right();
                    }
                    return cell;
                }
            };

    private final TableRowCreator mChampionshipRaceRowCreator =
            new TableRowCreator(5) {
                @Override
                protected Cell<Label> createCell(
                        Table table, int column, String value, String style) {
                    Cell<Label> cell = table.add(value, style);
                    if (column == 1) {
                        cell.left().expandX();
                    } else {
                        cell.right();
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
                setupUi(racers);
            }
        };
        setupUi(racers);
    }

    private void setupUi(Array<Racer> racers) {
        mPointsIncreaseSound = mGame.getAssets().soundAtlas.get("points-increase");
        fillPageCreators(racers);
        showNextPage();
    }

    private void fillPageCreators(Array<Racer> racers) {
        mPageCreators.clear();
        for (Racer racer : racers) {
            if (racer.getRecordRanks().brokeRecord()) {
                mPageCreators.add(() -> createRecordBreakerPage(racer));
            }
        }
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
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);
        if (!isChampionship()) {
            builder.defineVariable("quickRace");
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

    private Actor createRecordBreakerPage(Racer racer) {
        GameInfo.Player player = (GameInfo.Player) racer.getEntrant();
        Racer.RecordRanks ranks = racer.getRecordRanks();

        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);
        Actor content = builder.build(FileUtils.assets("screens/recordbreaker.gdxui"));

        Label titleLabel = builder.getActor("titleLabel");

        String title =
                String.format(Locale.US, "Congratulations player %d!", player.getIndex() + 1);
        if (ranks.lapRecordRank >= 0) {
            fillBestRow(builder, 1, ranks.lapRecordRank, "Best lap");
        }
        if (ranks.totalRecordRank >= 0) {
            int row = ranks.lapRecordRank >= 0 ? 2 : 1;
            fillBestRow(builder, row, ranks.totalRecordRank, "Best total time");
        }

        titleLabel.setText(title);
        titleLabel.pack();

        Menu menu = builder.getActor("menu");
        menu.addButton("OK")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                showNextPage();
                            }
                        });

        return content;
    }

    private void fillBestRow(UiBuilder builder, int row, int rank, String text) {
        Image image = builder.getActor("bestImage" + row);
        Label label = builder.getActor("bestLabel" + row);

        TextureRegion region = mGame.getAssets().ui.atlas.findRegion("best-" + (rank + 1));
        image.setDrawable(new TextureRegionDrawable(region));
        image.pack();

        label.setText(text);
        label.pack();
    }

    private boolean isChampionship() {
        return mRaceScreen.getGameType() == GameInfo.GameType.CHAMPIONSHIP;
    }

    private static String getRacerName(Racer racer) {
        return racer.getVehicle().getName();
    }
}
