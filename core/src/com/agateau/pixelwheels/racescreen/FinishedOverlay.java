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
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.racer.LapPositionComponent;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.TableRowCreator;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;

/**
 * Appears on top of RaceScreen at the end of the race
 */
public class FinishedOverlay extends Overlay {
    private final PwGame mGame;
    private final RaceScreen.Listener mListener;
    private final Array<Racer> mRacers;
    private final Array<Racer> mRecordBreakers = new Array<Racer>();
    private final TableRowCreator mTableRowCreator = new TableRowCreator() {
        @Override
        protected void createCells(Table table, String style, String... values) {
            table.add(values[0], style).right().padRight(24);
            table.add(values[1], style).left().expandX();
            table.add(values[2], style).right().padRight(24);
            table.add(values[3], style).right().padRight(24);
            table.add(values[4], style).right();
        }
    };

    public FinishedOverlay(PwGame game, RaceScreen.Listener listener, final Array<Racer> racers) {
        super(game.getAssets().dot);
        mGame = game;
        mListener = listener;
        mRacers = racers;
        new PwRefreshHelper(mGame, this) {
            @Override
            protected void refresh() {
                setContent(createContent(racers));
            }
        };
        setContent(createContent(racers));
    }

    private Actor createContent(Array<Racer> racers) {
        fillRecordBreakers(racers);
        if (mRecordBreakers.size > 0) {
            return createRecordBreakerContent();
        } else {
            return createScoreTableContent();
        }
    }

    private Actor createScoreTableContent() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);

        Actor content = builder.build(FileUtils.assets("screens/finishedoverlay.gdxui"));
        Menu menu = builder.getActor("menu");
        Table table = builder.getActor("scrollableTable");

        fillMenu(menu);
        fillTable(table);
        return content;
    }

    private void fillRecordBreakers(Array<Racer> racers) {
        mRecordBreakers.clear();
        for (Racer racer : racers) {
            if (racer.getRecordRanks().brokeRecord()) {
                mRecordBreakers.add(racer);
            }
        }
    }

    private void fillMenu(Menu menu) {
        menu.addButton("OK").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                mListener.onNextTrackPressed();
            }
        });
    }

    private void fillTable(Table table) {
        mTableRowCreator.setTable(table);
        mTableRowCreator.addHeaderRow("#", "Racer", "Best Lap", "Total", "Score");
        for (int idx = 0; idx < mRacers.size; ++idx) {
            Racer racer = mRacers.get(idx);
            String style = UiUtils.getEntrantRowStyle(racer.getEntrant());
            LapPositionComponent lapPositionComponent = racer.getLapPositionComponent();
            mTableRowCreator.setRowStyle(style);
            mTableRowCreator.addRow(
                    String.format(Locale.US, "%d.", idx + 1),
                    racer.getVehicle().getName(),
                    StringUtils.formatRaceTime(lapPositionComponent.getBestLapTime()),
                    StringUtils.formatRaceTime(lapPositionComponent.getTotalTime()),
                    String.valueOf(racer.getEntrant().getScore())
            );
        }
    }

    private Actor createRecordBreakerContent() {
        Racer racer = mRecordBreakers.pop();
        GameInfo.Player player = (GameInfo.Player)racer.getEntrant();
        Racer.RecordRanks ranks = racer.getRecordRanks();

        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);
        Actor content = builder.build(FileUtils.assets("screens/recordbreaker.gdxui"));

        Label titleLabel = builder.getActor("titleLabel");

        String title = String.format(Locale.US, "Congratulations player %d!", player.getIndex() + 1);
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
        menu.addButton("OK").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                onRecordBreakerOK();
            }
        });

        return content;
    }

    private void fillBestRow(UiBuilder builder, int row, int rank, String text) {
        Image image = builder.getActor("bestImage" + String.valueOf(row));
        Label label = builder.getActor("bestLabel" + String.valueOf(row));

        TextureRegion region = mGame.getAssets().ui.atlas.findRegion("best-" + String.valueOf(rank + 1));
        image.setDrawable(new TextureRegionDrawable(region));
        image.pack();

        label.setText(text);
        label.pack();
    }

    private void onRecordBreakerOK() {
        if (mRecordBreakers.size == 0) {
            setContent(createScoreTableContent());
        } else {
            setContent(createRecordBreakerContent());
        }
    }
}
