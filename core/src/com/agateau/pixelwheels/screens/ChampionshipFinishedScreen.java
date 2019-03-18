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
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.gamesetup.ChampionshipGameInfo;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.TableRowCreator;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;

public class ChampionshipFinishedScreen extends NavStageScreen {
    private final PwGame mGame;
    private final ChampionshipGameInfo mGameInfo;
    private final TableRowCreator mTableRowCreator = new TableRowCreator() {
        @Override
        protected void createCells(Table table, String style, String... values) {
            table.add(values[0], style).right().padRight(24);
            table.add(values[1], style).left().expandX().padRight(24);
            table.add(values[2], style).right().padRight(24);
            table.add(values[3], style).right();
        }
    };
    private final NextListener mNextListener;

    public ChampionshipFinishedScreen(PwGame game, ChampionshipGameInfo gameInfo, NextListener nextListener) {
        super(game.getAssets().ui);
        mGame = game;
        mGameInfo = gameInfo;
        mNextListener = nextListener;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new ChampionshipFinishedScreen(mGame, mGameInfo, mNextListener));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);

        AnchorGroup root = (AnchorGroup) builder.build(FileUtils.assets("screens/championshipfinished.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        setupNextButton((Button)builder.getActor("nextButton"));
        setNavListener(mNextListener);

        Table table = builder.getActor("entrantTable");
        fillEntrantTable(table, mGameInfo.getEntrants());
    }

    private void fillEntrantTable(Table table, Array<GameInfo.Entrant> entrants) {
        mTableRowCreator.setTable(table);
        mTableRowCreator.addHeaderRow("#", "Racer", "Score", "Total Time");
        for (int idx = 0; idx < entrants.size; ++idx) {
            GameInfo.Entrant entrant = entrants.get(idx);
            String style = UiUtils.getEntrantRowStyle(entrant);
            mTableRowCreator.setRowStyle(style);
            mTableRowCreator.addRow(
                    String.format(Locale.US, "%d.", idx + 1),
                    entrant.getVehicleId(),
                    String.valueOf(entrant.getScore()),
                    StringUtils.formatRaceTime(entrant.getRaceTime())
            );
        }
    }
}
