/*
 * Copyright 2014 Aurélien Gâteau <mail@agateau.com>
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

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/** This class holds translations loaded by PoParser */
public class Messages {
    public static class PluralId {
        public final String singular;
        public final String plural;

        public PluralId(String s, String p) {
            singular = s;
            plural = p;
        }

        @Override
        public int hashCode() {
            return singular.hashCode() * plural.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PluralId)) {
                return false;
            }
            PluralId other = (PluralId) obj;
            return singular.equals(other.singular) && plural.equals(other.plural);
        }
    }

    public interface PluralExpression {
        int eval(int n);
    }

    private final PluralExpression mPluralExpression;

    public final HashMap<String, String> plainEntries = new HashMap<>();

    public final HashMap<PluralId, String[]> pluralEntries = new HashMap<>();

    public Messages(PluralExpression expression) {
        mPluralExpression = expression;
    }

    public int plural(int n) {
        return mPluralExpression.eval(n);
    }

    public String getCharacters() {
        SortedSet<Character> set = new TreeSet<>();
        for (String text : this.plainEntries.values()) {
            addToSet(set, text);
        }
        for (String[] texts : this.pluralEntries.values()) {
            for (String text : texts) {
                addToSet(set, text);
            }
        }

        StringBuilder builder = new StringBuilder(set.size());
        for (Character ch : set) {
            if (ch != ' ') {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    private static void addToSet(SortedSet<Character> set, String text) {
        for (int idx = text.length() - 1; idx >= 0; idx--) {
            set.add(text.charAt(idx));
        }
    }
}
