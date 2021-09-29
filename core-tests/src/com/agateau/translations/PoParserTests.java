/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.translations;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PoParserTests {
    private static final String HEADER =
            joinLines(
                    "msgid \"\"",
                    "msgstr \"\"",
                    "\"Project-Id-Version: \\n\"",
                    "\"Report-Msgid-Bugs-To: \\n\"",
                    "\"POT-Creation-Date: 2021-09-22 22:33+0200\\n\"",
                    "\"PO-Revision-Date: 2021-09-22 22:35+0200\\n\"",
                    "\"Last-Translator: Aurélien Gâteau <mail@agateau.com>\\n\"",
                    "\"Language-Team: French <>\\n\"",
                    "\"Language: fr_FR\\n\"",
                    "\"MIME-Version: 1.0\\n\"",
                    "\"Content-Type: text/plain; charset=UTF-8\\n\"",
                    "\"Content-Transfer-Encoding: 8bit\\n\"",
                    "\"Plural-Forms: nplurals=2; plural=(n > 1);\\n\"",
                    "\"X-Generator: Poedit 2.3\\n\"");

    @Test
    public void testParserSimpleEntries() throws PoParserException {
        BufferedReader reader =
                createReader(
                        joinLines(
                                HEADER,
                                "msgid \"id1\"",
                                "msgstr \"str1\"",
                                "",
                                "# A comment",
                                "msgid \"id2\"",
                                "msgstr \"str2\""));

        PoParser parser = new PoParser(reader);
        Messages messages = parser.parse();
        assertThat(messages, is(notNullValue()));

        assertThat(messages.plainEntries.get("id1"), is("str1"));
        assertThat(messages.plainEntries.get("id2"), is("str2"));
        // Metadata must not be in entries
        assertThat(messages.plainEntries.get(""), is(nullValue()));
    }

    @Test
    public void testParserContinuationLine() throws PoParserException {
        BufferedReader reader =
                createReader(joinLines(HEADER, "msgid \"i\"", "\"d\"", "msgstr \"s\"", "\"tr\""));

        PoParser parser = new PoParser(reader);
        Messages messages = parser.parse();
        assertThat(messages, is(notNullValue()));

        assertThat(messages.plainEntries.get("id"), is("str"));
    }

    @Test
    public void testParserPlural() throws PoParserException {
        BufferedReader reader =
                createReader(
                        joinLines(
                                "msgid \"\"",
                                "msgstr \"\"",
                                "\"Plural-Forms: nplurals=3; plural=(n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2);\"",
                                "",
                                "msgid \"one file\"",
                                "msgid_plural \"%# files\"",
                                "msgstr[0] \"%# plik\"",
                                "msgstr[1] \"%# pliki\"",
                                "msgstr[2] \"%# plików\""));

        PoParser parser = new PoParser(reader);
        Messages messages = parser.parse();
        assertThat(messages, is(notNullValue()));

        Messages.PluralId pluralId = new Messages.PluralId("one file", "%# files");
        String[] strings = messages.pluralEntries.get(pluralId);
        assertThat(strings, is(notNullValue()));
        assertThat(strings, is(new String[] {"%# plik", "%# pliki", "%# plików"}));

        assertThat(messages.plural(0), is(2));
        assertThat(messages.plural(1), is(0));
        assertThat(messages.plural(22), is(1));
        assertThat(messages.plural(45), is(2));
    }

    @Test
    public void testNoEmptyTranslations() throws PoParserException {
        BufferedReader reader =
                createReader(
                        joinLines(
                                HEADER,
                                "msgid \"id\"",
                                "msgstr \"\"",
                                "msgid \"singularId\"",
                                "msgid_plural \"pluralId\"",
                                "msgstr[0] \"\"",
                                "msgstr[1] \"\""));

        PoParser parser = new PoParser(reader);
        Messages messages = parser.parse();
        assertThat(messages, is(notNullValue()));

        assertThat(messages.plainEntries.get("id1"), is(nullValue()));

        Messages.PluralId pluralId = new Messages.PluralId("singularId", "pluralId");
        assertThat(messages.pluralEntries.get(pluralId), is(nullValue()));
    }

    @Test
    public void testNoFuzzyTranslations() throws PoParserException {
        BufferedReader reader =
                createReader(
                        joinLines(
                                HEADER,
                                "#, fuzzy",
                                "msgid \"fuzzy\"",
                                "msgstr \"ignoreMe\"",
                                "msgid \"id\"",
                                "msgstr \"str\""));

        PoParser parser = new PoParser(reader);
        Messages messages = parser.parse();
        assertThat(messages, is(notNullValue()));

        assertThat(messages.plainEntries.get("fuzzy"), is(nullValue()));
        assertThat(messages.plainEntries.get("id"), is("str"));
    }

    @Test
    public void testParserContext() throws PoParserException {
        BufferedReader reader =
                createReader(
                        joinLines(HEADER, "msgctxt \"ctxt\"", "msgid \"id1\"", "msgstr \"str1\""));

        PoParser parser = new PoParser(reader);
        Messages messages = parser.parse();
        assertThat(messages, is(notNullValue()));

        assertThat(
                messages.plainEntries.get(PoParser.createIdWithContext("ctxt", "id1")), is("str1"));
    }

    @Test(expected = PoParserException.class)
    public void testMustStartWithHeader() throws PoParserException {
        BufferedReader reader = createReader(joinLines("msgid \"id1\"", "msgstr \"str1\""));

        PoParser parser = new PoParser(reader);
        parser.parse();
    }

    @Test
    public void testParseString() {
        assertThat(PoParser.parseString("\"abc\""), is("abc"));
        assertThat(PoParser.parseString("\"aaa\\\"aaa\""), is("aaa\"aaa"));
        assertThat(PoParser.parseString("\"l1\\nl2\""), is("l1\nl2"));
        assertThat(PoParser.parseString("\"anti\\\\slash\""), is("anti\\slash"));
    }

    private static BufferedReader createReader(String text) {
        StringReader reader = new StringReader(text);
        return new BufferedReader(reader);
    }

    private static String joinLines(String... args) {
        return String.join("\n", args);
    }
}
