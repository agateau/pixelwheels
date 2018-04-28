/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.tinywheels.screens;

import com.agateau.tinywheels.Assets;
import com.agateau.tinywheels.gamesetup.ChampionshipGameInfo;
import com.agateau.tinywheels.gamesetup.GameInfo;
import com.agateau.tinywheels.TwGame;
import com.agateau.tinywheels.racescreen.ScrollableTable;
import com.agateau.tinywheels.utils.UiUtils;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;
import java.util.Locale;

public class ChampionshipFinishedScreen extends TwStageScreen {
    private final TwGame mGame;
    private final ChampionshipGameInfo mGameInfo;

    public ChampionshipFinishedScreen(TwGame game, ChampionshipGameInfo gameInfo) {
        super(game.getAssets().ui);
        mGame = game;
        mGameInfo = gameInfo;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new ChampionshipFinishedScreen(mGame, mGameInfo));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);
        ScrollableTable.register(builder, "EntrantTable", new ScrollableTable.CellCreator() {
            @Override
            public void createCells(Table table, String style, String... values) {
                table.add(values[0], style).right().padRight(24);
                table.add(values[1], style).left().expandX();
                table.add(values[2], style).right();
            }
        });

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/championshipfinished.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        ScrollableTable table = builder.getActor("entrantTable");
        table.addHeaderRow("#", "Racer", "Score");
        Array<GameInfo.Entrant> entrants = mGameInfo.getEntrants();
        entrants.sort(new Comparator<GameInfo.Entrant>() {
            @Override
            public int compare(GameInfo.Entrant e1, GameInfo.Entrant e2) {
                return -Integer.compare(e1.getScore(), e2.getScore());
            }
        });
        for (int idx = 0; idx < entrants.size; ++idx) {
            GameInfo.Entrant entrant = entrants.get(idx);
            String style = UiUtils.getEntrantRowStyle(entrant);
            table.setRowStyle(style);
            table.addRow(
                    String.format(Locale.US, "%d.", idx + 1),
                    entrant.getVehicleId(),
                    String.valueOf(entrant.getScore())
            );
        }
    }

    @Override
    public void onBackPressed() {
        mGame.showMainMenu();
    }
}
