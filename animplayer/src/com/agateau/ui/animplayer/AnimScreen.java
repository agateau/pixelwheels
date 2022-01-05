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
package com.agateau.ui.animplayer;

import com.agateau.ui.FontSet;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.StageScreen;
import com.agateau.ui.UiAssets;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/** Shows a gdxui animation */
class AnimScreen extends StageScreen {
    private TextureAtlas mAtlas;
    private Skin mSkin;

    AnimScreen() {
        super(new ScreenViewport());
        loadAssets();
        setupUi();
    }

    private void loadAssets() {
        UiAssets assets = new UiAssets(FontSet.createTestInstance());
        mAtlas = assets.atlas;
        mSkin = assets.skin;
    }

    private void setupUi() {
        getStage().clear();
        UiBuilder builder = new UiBuilder(mAtlas, mSkin);
        AnchorGroup root = (AnchorGroup) builder.build(FileUtils.assets("screens/anim.gdxui"));
        getStage().addActor(root);
        root.setFillParent(true);

        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                loadAssets();
                setupUi();
            }
        };
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
