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
package com.agateau.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UiAssets {
    public final Skin skin;
    public final TextureAtlas atlas;
    public final TextureRegion background;

    public UiAssets() {
        this.atlas = new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
        this.skin = new Skin(this.atlas);

        loadFonts();

        this.skin.load(Gdx.files.internal("ui/uiskin.json"));

        this.background = this.atlas.findRegion("background");
    }

    private void loadFonts() {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter;
        this.skin.add("default-font", loadFont("fonts/Xolonium-Regular.ttf", 28));
        this.skin.add("title-font", loadFont("fonts/Aero.ttf", 32));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.borderWidth = 0.5f;
        this.skin.add("small-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 28;
        parameter.borderWidth = 0.5f;
        this.skin.add("hud-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 56;
        parameter.characters = "1234567890thsrdneméè";
        parameter.borderWidth = 0.5f;
        this.skin.add("hud-rank-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 180;
        parameter.characters = "123GO!";
        parameter.borderWidth = 0.5f;
        this.skin.add("hud-countdown-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));
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

    public static TextureAtlas.AtlasRegion findRegion(TextureAtlas atlas, String name) {
        TextureAtlas.AtlasRegion region = atlas.findRegion(name);
        if (region == null) {
            throw new RuntimeException("Failed to load a texture region named '" + name + "' from atlas " + atlas);
        }
        return region;
    }
}
