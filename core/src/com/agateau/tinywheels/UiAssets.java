/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UiAssets {
    public final Skin skin;
    public final TextureAtlas atlas;
    public final TextureRegion background;

    UiAssets() {
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
}
