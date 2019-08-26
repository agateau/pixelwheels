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

    private static final float SMALL_HUD_RATIO = 0.7f;

    public UiAssets() {
        this.atlas = new StrictTextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
        this.skin = new Skin(this.atlas);

        loadFonts();

        this.skin.load(Gdx.files.internal("ui/uiskin.gdxjson"));

        this.background = this.atlas.findRegion("background");
    }

    private void loadFonts() {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter;
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 28;
        parameter.characters += "•";
        this.skin.add("default-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        this.skin.add("title-font", loadFont("fonts/Aero.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.borderWidth = 0.5f;
        this.skin.add("tiny-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        this.skin.add("tiny-bold-font", loadFont("fonts/Xolonium-Bold.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        parameter.borderWidth = 0.5f;
        this.skin.add("small-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        // hud-font
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 28;
        parameter.borderWidth = 0.5f;
        this.skin.add("hud-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));
        parameter.size = (int) (parameter.size * SMALL_HUD_RATIO);
        this.skin.add("small-hud-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        // hud-rank-font
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 56;
        parameter.characters = "1234567890thsrdneméèP:";
        parameter.borderWidth = 0.5f;
        this.skin.add("hud-rank-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));
        parameter.size = (int) (parameter.size * SMALL_HUD_RATIO);
        this.skin.add("small-hud-rank-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 180;
        parameter.characters = "123GO!";
        parameter.borderWidth = 0.5f;
        this.skin.add("hud-countdown-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));
    }

    private BitmapFont loadFont(
            String name, FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(name));
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}
