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
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import java.util.HashMap;
import java.util.Locale;

public class Languages {
    public static final String DEFAULT_ID = "en";

    private final HashMap<String, FontSet> mFontSets = new HashMap<>();
    private final Array<Language> mLanguages = new Array<>();

    public Languages(FileHandle handle) {
        if (!handle.exists()) {
            throw new RuntimeException("No such file " + handle.name());
        }

        XmlReader.Element root = FileUtils.parseXml(handle);
        for (XmlReader.Element element : root.getChildrenByName("FontSet")) {
            loadFontSet(element);
        }
        for (XmlReader.Element element : root.getChildrenByName("Language")) {
            loadLanguage(element);
        }
    }

    public String findBestLanguageId() {
        String lang = Locale.getDefault().getLanguage();
        String langAndCountry = lang + "_" + Locale.getDefault().getCountry();
        if (findLanguage(langAndCountry) != null) {
            return langAndCountry;
        }
        if (findLanguage(lang) != null) {
            return lang;
        }
        NLog.i("Neither %s nor %s are supported languages", langAndCountry, lang);
        return DEFAULT_ID;
    }

    /**
     * Finds the language for the given ID. Falls back to DEFAULT_ID if the language is not
     * available for some reason.
     */
    public Language getLanguage(String languageId) {
        Language language = findLanguage(languageId);
        if (language != null) {
            return language;
        }
        NLog.e("No language with id %s", languageId);
        language = findLanguage(DEFAULT_ID);
        Assert.check(language != null, "Could not find language with id " + DEFAULT_ID);
        return language;
    }

    public FontSet getFontSet(String languageId) {
        return getLanguage(languageId).fontSet;
    }

    public Array<Language> getAll() {
        return mLanguages;
    }

    private void loadFontSet(XmlReader.Element element) {
        FontSet fontSet = new FontSet();
        fontSet.defaultFontName = element.getAttribute("defaultFontName");
        fontSet.defaultBoldFontName = element.getAttribute("defaultBoldFontName");
        fontSet.defaultFontSize = element.getIntAttribute("defaultFontSize");
        fontSet.titleFontName = element.getAttribute("titleFontName");
        fontSet.titleFontSize = element.getIntAttribute("titleFontSize");
        fontSet.hudFontName = element.getAttribute("hudFontName");

        mFontSets.put(element.getAttribute("id"), fontSet);
    }

    private void loadLanguage(XmlReader.Element element) {
        String fontSetId = element.getAttribute("fontSet");
        FontSet fontSet = mFontSets.get(fontSetId);
        Assert.check(fontSet != null, "No fontset with id " + fontSetId);

        Language language =
                new Language(element.getAttribute("id"), element.getAttribute("name"), fontSet);

        mLanguages.add(language);
    }

    /** Internal version of getLanguage(). Returns null if language is not found. */
    private Language findLanguage(String languageId) {
        for (Language language : mLanguages) {
            if (language.id.equals(languageId)) {
                return language;
            }
        }
        return null;
    }
}
