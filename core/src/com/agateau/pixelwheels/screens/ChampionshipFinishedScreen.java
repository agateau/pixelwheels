/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
import com.agateau.pixelwheels.gamesetup.ChampionshipGameInfo;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.racer.Vehicle;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.TableRowCreator;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

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
        }
    };
    private final NextListener mNextListener;

    private static class VehicleActor extends Actor {
        private final VehicleDrawer mDrawer;

        public VehicleActor(Assets assets) {
            mDrawer = new VehicleDrawer(assets);
            mDrawer.angle = 90;
        }

        public void setVehicleDef(VehicleDef vehicleDef) {
            mDrawer.vehicleDef = vehicleDef;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            mDrawer.center.x = getX();
            mDrawer.center.y = getY();
            mDrawer.draw(batch);
        }
    }

    public ChampionshipFinishedScreen(PwGame game, ChampionshipGameInfo gameInfo, NextListener nextListener) {
        super(game.getAssets().ui);
        mGame = game;
        mGameInfo = gameInfo;
        mNextListener = nextListener;
        setupUi();
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new ChampionshipFinishedScreen(mGame, mGameInfo, mNextListener));
            }
        };
    }

    private void setupUi() {
        final Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.ui.atlas, assets.ui.skin);
        builder.registerActorFactory("Vehicle", new UiBuilder.ActorFactory() {
            @Override
            public Actor createActor(UiBuilder uiBuilder, XmlReader.Element element) {
                return new VehicleActor(assets);
            }
        });

        AnchorGroup root = (AnchorGroup) builder.build(FileUtils.assets("screens/championshipfinished.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        setupNextButton((Button)builder.getActor("nextButton"));
        setNavListener(mNextListener);

        Table table = builder.getActor("entrantTable");
        fillEntrantTable(table, mGameInfo.getEntrants());

        fillPodium(builder, mGameInfo.getEntrants());
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
                    String.valueOf(entrant.getScore())
            );
        }
    }

    private void fillPodium(UiBuilder builder, Array<GameInfo.Entrant> entrants) {
        Assets assets = mGame.getAssets();
        for (int idx = 0; idx < 3; ++idx) {
            GameInfo.Entrant entrant = entrants.get(idx);
            VehicleDef vehicleDef = assets.findVehicleDefById(entrant.getVehicleId());
            VehicleActor actor = builder.getActor("vehicle" + idx);
            actor.setVehicleDef(vehicleDef);
        }
    }
}
