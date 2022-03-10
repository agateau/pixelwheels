/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.translations.PoImplementation;
import com.agateau.utils.FileUtils;
import com.agateau.utils.TestGdxFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TranslationTest {
    @BeforeClass
    public static void before() {
        Gdx.files = new TestGdxFiles();
    }

    /** Check we are able to load all our .po files */
    @Test
    public void loadAll() {
        Languages languages = new Languages(FileUtils.assets("ui/languages.xml"));
        Array<Language> lst = languages.getAll();
        assertTrue(lst.notEmpty());

        for (Language language : lst) {
            if (language.id.equals(Languages.DEFAULT_ID)) {
                // No .po for default language
                continue;
            }
            String path = StringUtils.format("po/%s.po", language.id);
            FileHandle handle = FileUtils.assets(path);
            assertTrue(path + " does not exist", handle.exists());
            PoImplementation impl = PoImplementation.load(handle);
            assertNotNull(impl);
        }
    }
}
