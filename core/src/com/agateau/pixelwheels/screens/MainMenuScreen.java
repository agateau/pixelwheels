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

import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.VersionInfo;
import com.agateau.pixelwheels.gamesetup.PlayerCount;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.agateau.utils.PlatformUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/** Main menu, shown at startup */
public class MainMenuScreen extends PwStageScreen {
    private final PwGame mGame;

    public MainMenuScreen(PwGame game) {
        super(game.getAssets().ui);
        mGame = game;
        setupUi();
        new PwRefreshHelper(game, getStage()) {
            @Override
            protected void refresh() {
                mGame.showMainMenu();
            }
        };
    }

    private void setupUi() {
        boolean desktop = PlatformUtils.isDesktop();
        UiBuilder builder = new UiBuilder(mGame.getAssets().ui.atlas, mGame.getAssets().ui.skin);
        if (desktop) {
            builder.defineVariable("desktop");
        }

        AnchorGroup root = (AnchorGroup) builder.build(FileUtils.assets("screens/mainmenu.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        if (desktop) {
            builder.getActor("onePlayerButton")
                    .addListener(
                            new MenuItemListener() {
                                @Override
                                public void triggered() {
                                    mGame.pushScreen(
                                            new SelectGameModeScreen(mGame, PlayerCount.ONE));
                                }
                            });
            builder.getActor("multiPlayerButton")
                    .addListener(
                            new MenuItemListener() {
                                @Override
                                public void triggered() {
                                    mGame.pushScreen(
                                            new SelectGameModeScreen(mGame, PlayerCount.MULTI));
                                }
                            });
        } else {
            builder.getActor("quickRaceButton")
                    .addListener(
                            new MenuItemListener() {
                                @Override
                                public void triggered() {
                                    mGame.showQuickRace(PlayerCount.ONE);
                                }
                            });
            builder.getActor("championshipButton")
                    .addListener(
                            new MenuItemListener() {
                                @Override
                                public void triggered() {
                                    mGame.showChampionship(PlayerCount.ONE);
                                }
                            });
        }
        builder.getActor("settingsButton")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                mGame.pushScreen(new ConfigScreen(mGame));
                            }
                        });
        if (desktop) {
            builder.getActor("quitButton")
                    .addListener(
                            new MenuItemListener() {
                                @Override
                                public void triggered() {
                                    Gdx.app.exit();
                                }
                            });
        }

        Label versionLabel = builder.getActor("version");
        versionLabel.setText(VersionInfo.VERSION);
        versionLabel.pack();
    }

    @Override
    public void onBackPressed() {
        Gdx.app.exit();
    }
}
