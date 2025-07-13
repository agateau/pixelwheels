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

import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.gamesetup.Difficulty;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.SliderMenuItem;
import com.agateau.ui.uibuilder.UiBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

class DifficultySelectorController {
    public static void registerFactory(UiBuilder builder, GameConfig config) {
        builder.registerMenuItemFactory(
                "DifficultySelector",
                (menu, element) -> {
                    if (element.getBoolean("header", true)) {
                        menu.addLabel(tr("League"));
                    }
                    return create(menu, config);
                });
    }

    private static SliderMenuItem create(Menu menu, GameConfig config) {
        SliderMenuItem selector =
                new SliderMenuItem(menu) {
                    @Override
                    protected String formatValue(int value) {
                        Difficulty difficulty = difficultyForInt(value);
                        return difficulty.toTranslatedString();
                    }
                };
        menu.addItem(selector);

        selector.setRange(0, Difficulty.values().length - 1);

        selector.setIntValue(config.difficulty.ordinal());

        selector.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        config.difficulty = difficultyForInt(selector.getIntValue());
                        config.flush(GameConfig.ConfigGroup.OTHER);
                    }
                });

        return selector;
    }

    private static Difficulty difficultyForInt(int value) {
        return Difficulty.values()[value];
    }
}
