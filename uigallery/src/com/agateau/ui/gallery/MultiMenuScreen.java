/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
import com.agateau.ui.anchor.SizeRule;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.menu.SwitchMenuItem;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

class MultiMenuScreen extends StageScreen {
    private Skin mSkin;
    private Array<Menu> mSubMenus = new Array<Menu>();
    private Menu mMenu;

    MultiMenuScreen() {
        super(new ScalingViewport(Scaling.fit, 800, 480));
        UiAssets assets = new UiAssets();
        mSkin = assets.skin;
        setupUi();
    }

    private void setupUi() {
        AnchorGroup root = new AnchorGroup();
        getStage().addActor(root);
        root.setFillParent(true);

        mMenu = new Menu(mSkin) {
            @Override
            public String toString() {
                return "mainMenu";
            }
        };
        final SwitchMenuItem pageSwitch = new SwitchMenuItem(mMenu);
        pageSwitch.getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showPage(pageSwitch.isChecked() ? 1 : 0);
            }
        });
        mMenu.addItem(pageSwitch);

        Menu subMenu = new Menu(mSkin) {
            @Override
            public String toString() {
                return "sub1";
            }
        };
        subMenu.addButton("Button A").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                NLog.d("A");
            }
        });
        subMenu.addButton("Button B");
        subMenu.mParentMenu = mMenu;
        mMenu.addItem(subMenu);
        mSubMenus.add(subMenu);

        subMenu = new Menu(mSkin) {
            @Override
            public String toString() {
                return "sub2";
            }
        };
        subMenu.setVisible(false);
        subMenu.addButton("Button C");
        subMenu.addButton("Button D").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                NLog.d("D");
            }
        });
        subMenu.mParentMenu = mMenu;
        mMenu.addItem(subMenu);
        mSubMenus.add(subMenu);

        root.addSizeRule(mMenu, root, SizeRule.IGNORE, 1f);
        root.addPositionRule(mMenu, Anchor.TOP_CENTER, root, Anchor.TOP_CENTER);

        showPage(0);
    }

    private void showPage(int page) {
        int otherPage = page == 0 ? 1 : 0;
        NLog.d("page=%d", page);
        mMenu.setItemVisible(mSubMenus.get(otherPage), false);
        mMenu.setItemVisible(mSubMenus.get(page), true);
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
