/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.ui.gallery;

import com.agateau.ui.GridMenuItem;
import com.agateau.ui.Menu;

import com.agateau.ui.MenuItemListener;
import com.agateau.ui.SelectorMenuItem;
import com.agateau.ui.StageScreen;
import com.agateau.ui.SwitchMenuItem;
import com.agateau.ui.TextureRegionItemRenderer;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.anchor.SizeRule;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

import java.util.Locale;

/**
 * The main screen of the gallery
 */

class GalleryScreen extends StageScreen {
    private TextureAtlas mAtlas;
    private Skin mSkin;

    GalleryScreen() {
        super(new ScalingViewport(Scaling.fit, 800, 480));
        loadSkin();
        setupUi();
    }

    private void loadSkin() {
        mAtlas = new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
        mSkin = new Skin(mAtlas);
        loadFonts();
        mSkin.load(Gdx.files.internal("ui/uiskin.json"));
    }

    private void loadFonts() {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter;
        mSkin.add("default-font", loadFont("fonts/Xolonium-Regular.ttf", 28));
        mSkin.add("title-font", loadFont("fonts/Aero.ttf", 32));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.borderWidth = 0.5f;
        mSkin.add("small-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 28;
        parameter.borderWidth = 0.5f;
        mSkin.add("hud-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 56;
        parameter.characters = "1234567890thsrdneméè";
        parameter.borderWidth = 0.5f;
        mSkin.add("hud-rank-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));
    }

    private BitmapFont loadFont(String name, int size) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        return loadFont(name, parameter);
    }

    private BitmapFont loadFont(String name, FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(name));
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    private void setupUi() {
        AnchorGroup root = new AnchorGroup();
        getStage().addActor(root);
        root.setFillParent(true);

        Menu menu = new Menu(mSkin);
        menu.setLabelColumnWidth(200);
        menu.setDefaultItemWidth(500);
        menu.addButton("Button A").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                NLog.e("Button A clicked");
            }
        });
        menu.addButton("Button B").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                NLog.e("Button B clicked");
            }
        });

        menu.addTitleLabel("Two columns");
        SwitchMenuItem switchMenuItem = new SwitchMenuItem(menu);
        menu.addItemWithLabel("Super Power", switchMenuItem);

        SelectorMenuItem<Integer> selectorMenuItem = new SelectorMenuItem(menu);
        selectorMenuItem.addEntry("Keyboard", 12);
        selectorMenuItem.addEntry("Joystick", 24);
        selectorMenuItem.addEntry("Mind", 36);
        menu.addItemWithLabel("Control", selectorMenuItem);

        menu.addTitleLabel("GridMenuItem");

        final GridMenuItem<TextureRegion> gridMenuItem = createGridMenuItem(menu);
        menu.addItem(gridMenuItem);
        menu.addButton("Add column").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                gridMenuItem.setColumnCount(gridMenuItem.getColumnCount() + 1);
            }
        });

        menu.addButton("Quit").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                Gdx.app.exit();
            }
        });

        for (int i = 0; i < 100; ++i) {
            menu.addButton(String.format(Locale.getDefault(), "Dummy %d", i));
        }
        root.addSizeRule(menu, root, SizeRule.IGNORE, 1);
        root.addPositionRule(menu, Anchor.TOP_CENTER, root, Anchor.TOP_CENTER);
    }

    private GridMenuItem<TextureRegion> createGridMenuItem(Menu menu) {
        Array<TextureRegion> items = new Array<TextureRegion>();
        items.add(mAtlas.findRegion("icon-back"));
        items.add(mAtlas.findRegion("icon-restart"));
        items.add(mAtlas.findRegion("rectbutton"));
        items.add(mAtlas.findRegion("icon-left"));
        items.add(mAtlas.findRegion("icon-right"));
        items.add(mAtlas.findRegion("checkbox-off"));
        items.add(mAtlas.findRegion("icon-config"));
        items.add(mAtlas.findRegion("icon-debug"));

        GridMenuItem<TextureRegion> gridMenuItem = new GridMenuItem<TextureRegion>(menu);
        gridMenuItem.setItemSize(70, 80);
        gridMenuItem.setItemRenderer(new TextureRegionItemRenderer());
        gridMenuItem.setItems(items);
        return gridMenuItem;
    }

    @Override
    public void onBackPressed() {
        Gdx.app.exit();
    }

    @Override
    public boolean isBackKeyPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
    }
}
