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
import com.agateau.ui.TableRowCreator;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
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
    private static final float FIRST_SCORE_INCREASE_INTERVAL = 1f;
    private static final float SCORE_INCREASE_INTERVAL = 0.3f;
    private static final int RANK_CHANGE_COLUMN_SIZE = 16;

    interface PageCreator {
        Actor createPage();
    }

    private enum ScoreTable {
        RACE,
        CHAMPIONSHIP
    }

    private static class ScoreAnimInfo {
        Label label;
        int score;
        int delta = 0;
    }

    private static final Comparator<Racer> sRacerComparator =
            (racer1, racer2) -> {
                GameInfo.Entrant entrant1 = racer1.getEntrant();
                GameInfo.Entrant entrant2 = racer2.getEntrant();
                // Highest score first
                int deltaScore = entrant2.getScore() - entrant1.getScore();
                if (deltaScore != 0) {
                    return deltaScore;
                }
                // Lowest race time first
                return Float.compare(entrant1.getRaceTime(), entrant2.getRaceTime());
            };

    private final PwGame mGame;
    private final RaceScreen mRaceScreen;
    private final Array<Racer> mRacers;
    private final Drawable[] mRankChangeDrawables = new Drawable[3];
    private final List<PageCreator> mPageCreators = new LinkedList<>();
    private final TableRowCreator mRaceRowCreator =
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

    enum ChampionshipColumn {
        RANK,
        RACER,
        RANK_CHANGE,
        TOTAL_TIME,
        POINTS
    }

    private final TableRowCreator mChampionshipRowCreator =
            new TableRowCreator(ChampionshipColumn.values().length) {
                @SuppressWarnings("rawtypes")
                @Override
                protected Cell createCell(Table table, int column, String value, String style) {
                    Cell cell;
                    if (column == ChampionshipColumn.RANK_CHANGE.ordinal()) {
                        Image image = new Image();
                        cell = table.add(image);
                        cell.size(RANK_CHANGE_COLUMN_SIZE);
                    } else {
                        cell = table.add(value, style);
                    }
                    if (column == ChampionshipColumn.RACER.ordinal()) {
                        cell.left().expandX();
                    } else {
                        cell.right();
                    }
                    return cell;
                }
            };
    private final Array<ScoreAnimInfo> mScoreAnimInfos = new Array<>();

    Timer.Task mIncreaseScoreTask =
            new Timer.Task() {
                @Override
                public void run() {
                    boolean done = true;
                    for (ScoreAnimInfo info : mScoreAnimInfos) {
                        if (info.delta == 0) {
                            continue;
                        }
                        ++info.score;
                        --info.delta;
                        if (info.delta > 0) {
                            done = false;
                        }
                    }
                    if (!done) {
                        scheduleScoreIncrease(SCORE_INCREASE_INTERVAL);
                    }
                    updateScoreLabels();
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
        TextureAtlas atlas = mGame.getAssets().ui.atlas;
        mRankChangeDrawables[0] = new TextureRegionDrawable(atlas.findRegion("rank-down"));
        mRankChangeDrawables[1] = new TextureRegionDrawable(atlas.findRegion("rank-same"));
        mRankChangeDrawables[2] = new TextureRegionDrawable(atlas.findRegion("rank-up"));
        createPageCreators(racers);
        showNextPage();
    }

    private void createPageCreators(Array<Racer> racers) {
        for (Racer racer : racers) {
            if (racer.getRecordRanks().brokeRecord()) {
                mPageCreators.add(() -> createRecordBreakerPage(racer));
            }
        }
        mPageCreators.add(() -> createScoreTablePage(ScoreTable.RACE));
        if (isChampionship()) {
            mPageCreators.add(() -> createScoreTablePage(ScoreTable.CHAMPIONSHIP));
        }
    }

    private void showNextPage() {
        mIncreaseScoreTask.cancel();
        PageCreator creator = mPageCreators.remove(0);
        setContent(creator.createPage());
    }

    private Actor createScoreTablePage(ScoreTable scoreTable) {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);
        if (!isChampionship()) {
            builder.defineVariable("quickRace");
        }
        HashMap<Racer, Integer> oldRankMap = null;
        if (scoreTable == ScoreTable.CHAMPIONSHIP) {
            mRacers.sort(sRacerComparator);
            ChampionshipMaestro maestro = (ChampionshipMaestro) mGame.getMaestro();
            if (!maestro.isFirstTrack()) {
                oldRankMap = createOldRankMap();
            }
        }

        Actor content = builder.build(FileUtils.assets("screens/finishedoverlay.gdxui"));
        Table table = builder.getActor("scrollableTable");

        Label titleLabel = builder.getActor("titleLabel");
        titleLabel.setText(
                scoreTable == ScoreTable.CHAMPIONSHIP ? "Championship Rankings" : "Race Results");
        titleLabel.pack();

        fillMenu(builder);
        fillTable(table, scoreTable, oldRankMap);
        return content;
    }

    private static int getOldPointsForRacer(Racer racer) {
        GameInfo.Entrant entrant = racer.getEntrant();
        return entrant.getScore() - entrant.getLastRacePoints();
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

    private void fillTable(Table table, ScoreTable scoreTable, HashMap<Racer, Integer> oldRankMap) {
        mScoreAnimInfos.clear();
        TableRowCreator rowCreator =
                scoreTable == ScoreTable.CHAMPIONSHIP ? mChampionshipRowCreator : mRaceRowCreator;
        rowCreator.setTable(table);
        rowCreator.setPadding(24);
        if (scoreTable == ScoreTable.CHAMPIONSHIP) {
            rowCreator.addHeaderRow("#", "Racer", "", "Total time", "Points");
        } else {
            rowCreator.addHeaderRow("#", "Racer", "Best lap", "Race time", "Points");
        }
        boolean needScoreAnim = isChampionship() && scoreTable == ScoreTable.RACE;
        for (int idx = 0; idx < mRacers.size; ++idx) {
            Racer racer = mRacers.get(idx);
            GameInfo.Entrant entrant = racer.getEntrant();
            String style = UiUtils.getEntrantRowStyle(entrant);
            rowCreator.setRowStyle(style);
            String rank = String.format(Locale.US, "%d.", idx + 1);
            String name = racer.getVehicle().getName();
            if (scoreTable == ScoreTable.RACE) {
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
                rowCreator.addRow(rank, name, bestLapTime, totalTime, "");
            } else {
                String totalTime = StringUtils.formatRaceTime(entrant.getRaceTime());
                rowCreator.addRow(rank, name, null, totalTime, "");
                if (oldRankMap != null) {
                    int oldIdx = oldRankMap.get(racer);
                    Drawable drawable = mRankChangeDrawables[(int) Math.signum(oldIdx - idx) + 1];

                    Cell<Image> imageCell =
                            rowCreator.getCreatedRowCell(ChampionshipColumn.RANK_CHANGE.ordinal());
                    imageCell.getActor().setDrawable(drawable);
                }
            }
            Cell<Label> scoreCell = rowCreator.getCreatedRowCell(-1);
            ScoreAnimInfo info = new ScoreAnimInfo();
            info.label = scoreCell.getActor();
            if (needScoreAnim) {
                info.delta = entrant.getLastRacePoints();
                info.score = entrant.getScore() - info.delta;
            } else {
                info.score = entrant.getScore();
            }
            mScoreAnimInfos.add(info);
        }
        updateScoreLabels();
        if (needScoreAnim) {
            scheduleScoreIncrease(FIRST_SCORE_INCREASE_INTERVAL);
        }
    }

    private void scheduleScoreIncrease(float interval) {
        Timer.schedule(mIncreaseScoreTask, interval);
    }

    private void updateScoreLabels() {
        for (ScoreAnimInfo info : mScoreAnimInfos) {
            String score;
            if (info.delta == 0) {
                score = String.valueOf(info.score);
            } else {
                score = StringUtils.format("+%d %d", info.delta, info.score);
            }
            info.label.setText(score);
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
}
