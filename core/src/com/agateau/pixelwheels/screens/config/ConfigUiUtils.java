/*
 * Copyright 2023 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.screens.config;

import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.LabelMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItem;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ConfigUiUtils {
    static MenuItem createTwoColumnRow(Menu menu, String text1, String text2, MenuItem item) {
        return createTwoColumnRow(menu, text1, text2, item, "default");
    }

    static MenuItem createTwoColumnRow(
            Menu menu, String text1, String text2, MenuItem item, String style) {
        if (text2 == null) {
            return menu.addItemWithLabel(text1, item, style);
        }
        AnchorGroup group = new AnchorGroup();
        Label label1 = new Label(text1, menu.getSkin(), style);
        Label label2 = new Label(text2, menu.getSkin(), style);
        group.addPositionRule(label1, Anchor.CENTER_LEFT, group, Anchor.CENTER_LEFT);
        group.addPositionRule(label2, Anchor.CENTER_LEFT, group, Anchor.CENTER);
        return menu.addItemWithLabelActor(group, item);
    }

    static MenuItem createTwoColumnTitle(Menu menu, String text1, String text2) {
        LabelMenuItem emptyItem = new LabelMenuItem("", menu.getSkin());
        return createTwoColumnRow(menu, text1, text2, emptyItem, "gamepadHeaderRow");
    }
}
