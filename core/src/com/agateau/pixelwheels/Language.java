/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels;

import com.agateau.ui.FontSet;

/**
 * Represents a language supported by the game
 *
 * <p>The ALL instance provides all supported languages
 */
public class Language {
    // id must match the format expected by GetTextImplementation.load():
    // Either {lang} or {lang}_{territory}, preferably {lang} to cover more variants.
    //
    // The matching .po file must be named {id}.po.
    public final String id;
    // The name of the language, in the language itself (so: "Français", not "French")
    public final String name;

    public final FontSet fontSet;

    Language(String id, String name, FontSet fontSet) {
        this.id = id;
        this.name = name;
        this.fontSet = fontSet;
    }
}
