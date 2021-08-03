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

import com.agateau.utils.log.NLog;
import java.util.Locale;

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

    private Language(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static final Language[] ALL = {
        new Language("en", "English"), new Language("fr", "Français"),
    };

    public static final String DEFAULT_ID = "en";

    public static String findBestLanguageId() {
        String lang = Locale.getDefault().getLanguage();
        String langAndCountry = lang + "_" + Locale.getDefault().getCountry();
        if (isSupported(langAndCountry)) {
            return langAndCountry;
        }
        if (isSupported(lang)) {
            return lang;
        }
        NLog.i("Neither %s nor %s are supported languages", langAndCountry, lang);
        return DEFAULT_ID;
    }

    private static boolean isSupported(String languageId) {
        for (Language language : ALL) {
            if (language.id.equals(languageId)) {
                return true;
            }
        }
        return false;
    }
}
