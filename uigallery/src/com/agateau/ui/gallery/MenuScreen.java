/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
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

import com.agateau.ui.StageScreen;
import com.agateau.ui.TextureRegionItemRenderer;
import com.agateau.ui.UiAssets;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.anchor.SizeRule;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemGroup;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.menu.SelectorMenuItem;
import com.agateau.ui.menu.SliderMenuItem;
import com.agateau.ui.menu.SwitchMenuItem;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/** Demonstrates the Menu class */
class MenuScreen extends StageScreen {
    private final TextureAtlas mAtlas;
    private final Skin mSkin;

    MenuScreen() {
        super(new ScreenViewport());
        UiAssets assets = new UiAssets();
        mAtlas = assets.atlas;
        mSkin = assets.skin;
        setupUi();
    }

    private void setupUi() {
        AnchorGroup root = new AnchorGroup();
        getStage().addActor(root);
        root.setFillParent(true);

        final Menu menu = new Menu(mSkin);
        menu.setLabelColumnWidth(200);
        menu.setWidth(500);
        menu.addButton("Button A")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                NLog.e("Button A clicked");
                            }
                        });
        menu.addButton("Button B")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                NLog.e("Button B clicked");
                            }
                        });
        final MenuItemGroup hiddenGroup = new MenuItemGroup(menu);
        menu.addButton("Toggle Hidden Group")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                boolean visible = menu.isItemVisible(hiddenGroup);
                                menu.setItemVisible(hiddenGroup, !visible);
                            }
                        });
        hiddenGroup.addTitleLabel("Hidden item");
        hiddenGroup.addButton("I was hidden");
        menu.addItem(hiddenGroup);
        menu.setItemVisible(hiddenGroup, false);

        menu.addTitleLabel("Two columns");
        final SwitchMenuItem switchMenuItem = new SwitchMenuItem(menu);
        menu.addItemWithLabel("Super Power", switchMenuItem)
                .addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                NLog.d(
                                        "Switch changed to %s",
                                        switchMenuItem.isChecked() ? "ON" : "OFF");
                            }
                        });

        SelectorMenuItem<Integer> selectorMenuItem = new SelectorMenuItem<>(menu);
        selectorMenuItem.addEntry("Keyboard", 12);
        selectorMenuItem.addEntry("Joystick", 24);
        selectorMenuItem.addEntry("Mind", 36);
        menu.addItemWithLabel("Control", selectorMenuItem);

        SliderMenuItem sliderMenuItem = new SliderMenuItem(menu);
        sliderMenuItem.setRange(12, 36, 2);
        menu.addItemWithLabel("Ints", sliderMenuItem);

        SliderMenuItem floatSliderMenuItem = new SliderMenuItem(menu);
        floatSliderMenuItem.setRange(1f, 9f, 0.5f);
        menu.addItemWithLabel("Floats", floatSliderMenuItem);

        menu.addTitleLabel("GridMenuItem");

        final GridMenuItem<TextureRegion> gridMenuItem = createGridMenuItem(menu);
        menu.addItem(gridMenuItem);
        menu.addButton("Add column")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                gridMenuItem.setColumnCount(gridMenuItem.getColumnCount() + 1);
                            }
                        });

        menu.addButton("Quit")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                Gdx.app.exit();
                            }
                        });

        root.addSizeRule(menu, root, SizeRule.IGNORE, 1);
        root.addPositionRule(menu, Anchor.TOP_CENTER, root, Anchor.TOP_CENTER);
    }

    private GridMenuItem<TextureRegion> createGridMenuItem(Menu menu) {
        Array<TextureRegion> items = new Array<>();
        items.add(mAtlas.findRegion("icon-back"));
        items.add(mAtlas.findRegion("icon-restart"));
        items.add(mAtlas.findRegion("rectbutton"));
        items.add(mAtlas.findRegion("icon-left"));
        items.add(mAtlas.findRegion("icon-right"));
        items.add(mAtlas.findRegion("checkbox-off"));
        items.add(mAtlas.findRegion("icon-config"));
        items.add(mAtlas.findRegion("icon-debug"));

        GridMenuItem<TextureRegion> gridMenuItem = new GridMenuItem<>(menu);
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
