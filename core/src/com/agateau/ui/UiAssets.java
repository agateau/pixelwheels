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

import com.agateau.utils.FileUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UiAssets {
    public final Skin skin;
    public final TextureAtlas atlas;
    public final TextureRegion background;

    private static final float SMALL_HUD_RATIO = 0.7f;

    public UiAssets(FontSet fontSet) {
        this(fontSet, "");
    }

    public UiAssets(FontSet fontSet, String extraCharacters) {
        this.atlas = new StrictTextureAtlas(FileUtils.assets("ui/uiskin.atlas"));
        this.background = this.atlas.findRegion("background");

        this.skin = new Skin(this.atlas);
        loadFontSet(fontSet, extraCharacters);
        this.skin.load(FileUtils.assets("ui/uiskin.gdxjson"));
    }

    private void loadFontSet(FontSet fontSet, String characters) {
        characters = FreeTypeFontGenerator.DEFAULT_CHARS + characters;

        FreeTypeFontGenerator.FreeTypeFontParameter parameter;
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSet.defaultFontSize;
        parameter.characters = characters;
        this.skin.add("default-font", loadFont("fonts/" + fontSet.defaultFontName, parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSet.defaultFontSize;
        // The '*' character is required for the switches in the debug screen
        parameter.characters = "*⭘⏽";
        this.skin.add("symbols-font", loadFont("fonts/NotoSansSymbols2-Regular.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSet.titleFontSize;
        parameter.characters = characters;
        this.skin.add("title-font", loadFont("fonts/" + fontSet.titleFontName, parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.borderWidth = 0.5f;
        parameter.characters = characters;
        this.skin.add("tiny-font", loadFont("fonts/" + fontSet.defaultFontName, parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (fontSet.defaultFontSize * 0.5f);
        parameter.characters = characters;
        this.skin.add(
                "tiny-bold-font", loadFont("fonts/" + fontSet.defaultBoldFontName, parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (fontSet.defaultFontSize * 0.8f);
        parameter.borderWidth = 0.5f;
        parameter.characters = characters;
        this.skin.add("small-font", loadFont("fonts/" + fontSet.defaultFontName, parameter));

        // hud-font
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSet.defaultFontSize;
        parameter.borderWidth = 0.5f;
        parameter.characters = characters;
        this.skin.add("hud-font", loadFont("fonts/" + fontSet.hudFontName, parameter));
        parameter.size = (int) (parameter.size * SMALL_HUD_RATIO);
        this.skin.add("small-hud-font", loadFont("fonts/" + fontSet.hudFontName, parameter));

        // hud-rank-font
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 56;
        parameter.borderWidth = 0.5f;
        parameter.characters = characters;
        this.skin.add("hud-rank-font", loadFont("fonts/" + fontSet.hudFontName, parameter));
        parameter.size = (int) (parameter.size * SMALL_HUD_RATIO);
        this.skin.add("small-hud-rank-font", loadFont("fonts/" + fontSet.hudFontName, parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 180;
        parameter.characters = "123GO!";
        parameter.borderWidth = 0.5f;
        this.skin.add("hud-countdown-font", loadFont("fonts/" + fontSet.hudFontName, parameter));
    }

    private BitmapFont loadFont(
            String name, FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(FileUtils.assets(name));
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}
