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
import com.agateau.ui.UiAssets;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemGroup;
import com.agateau.ui.menu.SwitchMenuItem;
import com.agateau.ui.menu.TabMenuItem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

class TabbedMenuScreen extends StageScreen {
    private final Skin mSkin;

    TabbedMenuScreen() {
        super(new ScalingViewport(Scaling.fit, 800, 480));
        UiAssets assets = new UiAssets();
        mSkin = assets.skin;
        setupUi();
    }

    private void setupUi() {
        final AnchorGroup root = new AnchorGroup();
        getStage().addActor(root);
        root.setFillParent(true);

        final Menu menu = new Menu(mSkin);
        menu.setLabelColumnWidth(200);
        menu.setWidth(700);

        TabMenuItem tab = new TabMenuItem(menu);
        menu.addItem(tab);
        MenuItemGroup group1 = tab.addPage("1st page");
        group1.setWidth(500);
        group1.addItemWithLabel("A switch", new SwitchMenuItem(menu));
        group1.addButton("Group 1");

        MenuItemGroup group2 = tab.addPage("Page"); // with long title");
        group2.addButton("Group 2");
        group2.addButton("Another Group 2 button");
        group2.addButton("Yet Another Group 2 button");

        MenuItemGroup group3 = tab.addPage("Another page");
        group3.addLabel("This is page 3");
        group3.addItem(new SwitchMenuItem(menu));

        menu.addTitleLabel("Common Part");
        menu.addButton("A Common Button");

        root.addPositionRule(menu, Anchor.TOP_CENTER, root, Anchor.TOP_CENTER);
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
