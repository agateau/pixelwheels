/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agateau.translations;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;

/** Implementation of Translator.Implementation which directly loads .po classes */
public class PoImplementation implements Translator.Implementation {
    private final Messages mMessages;

    private PoImplementation(Messages messages) {
        mMessages = messages;
    }

    /**
     * Tries to load an implementation for the given languageId
     *
     * <p>languageId must be of the form {lang} or {lang}_{territory}
     */
    public static PoImplementation load(FileHandle handle) {
        Messages messages = tryLoad(handle);
        if (messages == null) {
            return null;
        }
        return new PoImplementation(messages);
    }

    @Override
    public String trc(String src, String context) {
        if (mMessages == null) {
            return src;
        }
        String key = context == null ? src : PoParser.createIdWithContext(context, src);
        String txt = mMessages.plainEntries.get(key);
        return txt == null ? src : txt;
    }

    @Override
    public String trn(String singular, String plural, int n) {
        String txt = findPluralTranslation(singular, plural, n);
        if (txt == null) {
            txt = n == 1 ? singular : plural;
        }
        return txt.replace("%#", String.valueOf(n));
    }

    @Override
    public String getCharacters() {
        if (mMessages == null) {
            return "";
        }
        return mMessages.getCharacters();
    }

    private String findPluralTranslation(String singular, String plural, int n) {
        if (mMessages == null) {
            return null;
        }
        Messages.PluralId id = new Messages.PluralId(singular, plural);
        String[] lst = mMessages.pluralEntries.get(id);
        if (lst == null) {
            return null;
        }
        return lst[mMessages.plural(n)];
    }

    private static Messages tryLoad(FileHandle handle) {
        if (!handle.exists()) {
            return null;
        }
        BufferedReader stream = handle.reader(1024, "UTF-8");
        PoParser parser = new PoParser(stream);
        try {
            return parser.parse();
        } catch (PoParserException exc) {
            NLog.e("Failed to parse %s: %s", handle.name(), exc);
            return null;
        }
    }
}
