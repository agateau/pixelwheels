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

import static com.agateau.pixelwheels.utils.BodyRegionDrawer.SHADOW_ALPHA;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.gamesetup.ChampionshipGameInfo;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.TableRowCreator;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import java.util.Locale;

public class ChampionshipFinishedScreen extends NavStageScreen {
    private final PwGame mGame;
    private final ChampionshipGameInfo mGameInfo;
    private final TableRowCreator mTableRowCreator =
            new TableRowCreator(4) {
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
    private final NextListener mNextListener;

    private static class ShadowActor extends Actor {
        private final Image mSource;
        private final float mOffset;

        ShadowActor(Image source, float offset) {
            mSource = source;
            mOffset = offset;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            mSource.setZIndex(getZIndex() + 1);
            setX(mSource.getX() + mOffset);
            setY(mSource.getY() - mOffset);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.setColor(0, 0, 0, SHADOW_ALPHA);
            mSource.getDrawable()
                    .draw(batch, getX(), getY(), mSource.getWidth(), mSource.getHeight());
        }
    }

    public ChampionshipFinishedScreen(
            PwGame game, ChampionshipGameInfo gameInfo, NextListener nextListener) {
        super(game.getAssets().ui);
        mGame = game;
        mGameInfo = gameInfo;
        mNextListener = nextListener;
        if (isPlayerOnPodium()) {
            setupPodiumUi();
        } else {
            setupNoPodiumUi();
        }
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(
                        new ChampionshipFinishedScreen(mGame, mGameInfo, mNextListener));
            }
        };
    }

    private boolean isPlayerOnPodium() {
        for (int idx = 0; idx < 3; ++idx) {
            GameInfo.Entrant entrant = mGameInfo.getEntrants().get(idx);
            if (entrant.isPlayer()) {
                return true;
            }
        }
        return false;
    }

    private void setupPodiumUi() {
        final Assets assets = mGame.getAssets();
        final UiBuilder builder = new UiBuilder(assets.ui.atlas, assets.ui.skin);
        VehicleActor.register(builder, assets);

        builder.registerActorFactory(
                "Road",
                (uiBuilder, element) -> {
                    float pixelsPerSecond = element.getFloatAttribute("pixelsPerSecond", 0);
                    return new ScrollableTiledImage(
                            assets.ui.atlas.findRegion("road"), pixelsPerSecond);
                });

        builder.registerActorFactory(
                "Shadow",
                (uiBuilder, element) -> {
                    String sourceId = element.getAttribute("source", null);
                    if (sourceId == null) {
                        throw new UiBuilder.SyntaxException("Missing 'source' attribute");
                    }
                    Image source = uiBuilder.getActor(sourceId);
                    float offset = element.getFloatAttribute("offset", 12);
                    return new ShadowActor(source, offset);
                });

        mTableRowCreator.setSpacing(12);
        if (!setupCommonUi(
                builder, FileUtils.assets("screens/championshipfinished-podium.gdxui"))) {
            return;
        }
        fillPodium(builder, mGameInfo.getEntrants());
        mGame.getAudioManager().playMusic(Assets.CHAMPIONSHIP_FINISHED_MUSIC_ID);
    }

    private void setupNoPodiumUi() {
        final Assets assets = mGame.getAssets();
        final UiBuilder builder = new UiBuilder(assets.ui.atlas, assets.ui.skin);
        setupCommonUi(builder, FileUtils.assets("screens/championshipfinished-nopodium.gdxui"));
    }

    private boolean setupCommonUi(UiBuilder builder, FileHandle uiFileHandle) {
        AnchorGroup root = (AnchorGroup) builder.build(uiFileHandle);
        if (root == null) {
            NLog.e("Failed to create ui");
            return false;
        }
        root.setFillParent(true);
        getStage().addActor(root);

        setupNextButton(builder.getActor("nextButton"));
        setNavListener(mNextListener);

        Table table = builder.getActor("entrantTable");
        float spacing = builder.getFloatConfigValue("entrantTableSpacing");
        mTableRowCreator.setSpacing((int) spacing);
        fillEntrantTable(table, mGameInfo.getEntrants());

        return true;
    }

    private void fillEntrantTable(Table table, Array<GameInfo.Entrant> entrants) {
        mTableRowCreator.setTable(table);
        mTableRowCreator.addHeaderRow("#", "Racer", "Total time", "Points");
        for (int idx = 0; idx < entrants.size; ++idx) {
            GameInfo.Entrant entrant = entrants.get(idx);
            VehicleDef vehicleDef = mGame.getAssets().findVehicleDefById(entrant.getVehicleId());
            String style = UiUtils.getEntrantRowStyle(entrant);
            String totalTime = StringUtils.formatRaceTime(entrant.getRaceTime());
            mTableRowCreator.setRowStyle(style);
            mTableRowCreator.addRow(
                    String.format(Locale.US, "%d.", idx + 1),
                    vehicleDef.name,
                    totalTime,
                    String.valueOf(entrant.getPoints()));
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
