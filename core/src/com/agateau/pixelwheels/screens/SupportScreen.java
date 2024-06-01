/*
 * Copyright 2024 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.LabelMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.agateau.utils.PlatformUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SupportScreen extends PwStageScreen {
    private static class SupportInfo {
        public String url;
        public String label;
        public String buttonText;
    }

    private final PwGame mGame;

    public SupportScreen(PwGame game) {
        super(game.getAssets().ui);
        mGame = game;
        setupUi();
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SupportScreen(mGame));
            }
        };
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);

        AnchorGroup root = (AnchorGroup) builder.build(FileUtils.assets("screens/support.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");
        menu.setLabelColumnWidth(250);

        SupportScreen.SupportInfo supportInfo = getSupportInfo();
        LabelMenuItem labelMenuItem = menu.addLabel(supportInfo.label);
        labelMenuItem.setWrap(true);
        menu.addButton(supportInfo.buttonText)
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                PlatformUtils.openURI(supportInfo.url);
                            }
                        });

        menu.addBackButton()
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });
    }

    @Override
    public void onBackPressed() {
        mGame.popScreen();
    }

    /**
     * GPlay does not allow linking to a support page, so use more generic information for the GPlay
     * build.
     */
    private SupportScreen.SupportInfo getSupportInfo() {
        SupportScreen.SupportInfo info = new SupportScreen.SupportInfo();
        switch (Constants.STORE) {
            case ITCHIO:
                info.url = "https://agateau.com/support/";
                info.label =
                        tr(
                                "Pixel Wheels is free, but you can support its\ndevelopment in various ways.");
                info.buttonText = tr("VISIT SUPPORT PAGE");
                break;
            case GPLAY:
                info.url = "https://agateau.com/projects/pixelwheels";
                info.label = tr("Learn more about Pixel Wheels");
                info.buttonText = tr("VISIT WEB SITE");
                break;
        }
        return info;
    }
}
