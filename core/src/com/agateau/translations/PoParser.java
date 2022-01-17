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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a gettext .po file into a Messages instance.
 *
 * <p>The plural expression used by the .po file must have been added to sPluralExpressionByString.
 */
public class PoParser {
    // When an entry has a context, the generated key is:
    // {msgctx}{CONTEXT_SEPARATOR}{msgid}
    private static final String CONTEXT_SEPARATOR = "@@@";

    private static final String FUZZY_COMMENT = "#, fuzzy";

    private static final Pattern HEADER_PATTERN =
            Pattern.compile("Plural-Forms:\\s*nplurals\\s*=\\s*(\\d+)\\s*; plural\\s*=\\s*(.*);");
    private static final HashMap<String, Messages.PluralExpression> sPluralExpressionByString =
            new HashMap<>();

    static {
        // Keys of this map are the value of the "plural = " part of the "Plural-forms" header
        // entry,
        // without spaces
        sPluralExpressionByString.put("n>1", n -> n > 1 ? 1 : 0);
        sPluralExpressionByString.put("n!=1", n -> n != 1 ? 1 : 0);
        sPluralExpressionByString.put(
                "n==1?0:n%10>=2&&n%10<=4&&(n%100<12||n%100>14)?1:2",
                n ->
                        n == 1
                                ? 0
                                : n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 12 || n % 100 > 14)
                                        ? 1
                                        : 2);
        sPluralExpressionByString.put("0", n -> 0);
        sPluralExpressionByString.put(
                "n%10==1&&n%100!=11?0:n%10>=2&&n%10<=4&&(n%100<12||n%100>14)?1:2",
                n ->
                        n % 10 == 1 && n % 100 != 11
                                ? 0
                                : n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 12 || n % 100 > 14)
                                        ? 1
                                        : 2);
    }

    private enum State {
        EXPECT_MSGID_OR_MSGCTXT,
        EXPECT_MSGID,
        GOT_MSGID,
        EXPECT_MSGSTR_PLURAL,
    }

    private final BufferedReader mReader;
    private int mLineNumber = 0;
    private State mState = State.EXPECT_MSGID_OR_MSGCTXT;

    private Messages mMessages = null;
    private int mPluralCount;

    private boolean mCurrentEntryIsFuzzy = false;
    private String mMsgCtxt;
    private String mMsgId;
    private String mMsgIdPlural;
    private final ArrayList<String> mMsgStr = new ArrayList<>();

    PoParser(BufferedReader reader) {
        mReader = reader;
    }

    public Messages parse() throws PoParserException {
        // A keyword is one of msgctxt, msgid, msgid_plural, msgstr or msgstr[$N]
        // (Where $N is a number)
        String keyword = null;
        StringBuilder keywordArguments = new StringBuilder();
        while (true) {
            // Read lines
            String line;
            try {
                mLineNumber++;
                line = mReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                throw new PoParserException(mLineNumber, e.toString());
            }
            if (line == null) {
                break;
            }
            line = line.trim();

            // Early-process continuation lines
            if (line.startsWith("\"")) {
                if (keyword == null) {
                    throw new PoParserException(
                            mLineNumber, "Expected keyword, got continuation line");
                }
                keywordArguments.append(parseString(line));
                continue;
            }

            // If we reach this point, we know the line is not a continuation line. If we have been
            // accumulating the lines of a keyword argument, it is now complete, we can process it
            if (keyword != null) {
                processKeyword(keyword, keywordArguments.toString());
                keyword = null;
            }

            // Is the line an interesting comment?
            if (line.startsWith(FUZZY_COMMENT)) {
                mCurrentEntryIsFuzzy = true;
                continue;
            }
            // Is the line something we can ignore?
            if (line.isEmpty() || line.charAt(0) == '#') {
                continue;
            }

            // If we reach this point, we are at the start of a new keyword
            String[] tokens = line.split("\\s+", 2);
            if (tokens.length != 2) {
                throw new PoParserException(mLineNumber, "Invalid line, could not find a space");
            }
            keyword = tokens[0];
            keywordArguments.setLength(0);
            keywordArguments.append(parseString(tokens[1]));
        }

        // We finished reading the file, process the last keyword
        if (keyword != null) {
            processKeyword(keyword, keywordArguments.toString());
        }

        if (mMsgIdPlural != null) {
            addCurrentEntry();
        }

        return mMessages;
    }

    public static String createIdWithContext(String context, String id) {
        return context + CONTEXT_SEPARATOR + id;
    }

    private void processKeyword(String keyword, String argument) throws PoParserException {
        switch (mState) {
            case EXPECT_MSGID_OR_MSGCTXT:
                if (keyword.equals("msgctxt")) {
                    processMsgctxt(argument);
                } else if (keyword.equals("msgid")) {
                    processMsgid(argument);
                } else {
                    throw new PoParserException(
                            mLineNumber - 1, "Expected msgctxt or msgid, got " + keyword);
                }
                break;
            case EXPECT_MSGID:
                if (keyword.equals("msgid")) {
                    processMsgid(argument);
                } else {
                    throw new PoParserException(mLineNumber - 1, "Expected msgid, got " + keyword);
                }
                break;
            case GOT_MSGID:
                if (keyword.equals("msgstr")) {
                    mMsgStr.add(argument);
                    addCurrentEntry();
                    mState = State.EXPECT_MSGID_OR_MSGCTXT;
                } else if (keyword.equals("msgid_plural")) {
                    mMsgIdPlural = argument;
                    mState = State.EXPECT_MSGSTR_PLURAL;
                } else {
                    throw new PoParserException(
                            mLineNumber - 1, "Expected msgstr or msgid_plural, got " + keyword);
                }
                break;
            case EXPECT_MSGSTR_PLURAL:
                if (keyword.startsWith("msgstr[")) {
                    // Assumes msgstr[] entries are sorted in ascending order
                    mMsgStr.add(argument);
                } else if (keyword.equals("msgctxt")) {
                    addCurrentEntry();
                    processMsgctxt(argument);
                } else if (keyword.equals("msgid")) {
                    addCurrentEntry();
                    processMsgid(argument);
                } else {
                    throw new PoParserException(
                            mLineNumber - 1, "Expected msgid or msgstr[N] line, got " + keyword);
                }
                break;
        }
    }

    private void processMsgid(String argument) {
        mMsgId = argument;
        mState = State.GOT_MSGID;
    }

    private void processMsgctxt(String argument) {
        mMsgCtxt = argument;
        mState = State.EXPECT_MSGID;
    }

    private void addCurrentEntry() throws PoParserException {
        if (mMessages == null) {
            parseHeader();
        } else {
            if (!mCurrentEntryIsFuzzy) {
                doAddCurrentEntry();
            }
        }
        mMsgCtxt = null;
        mMsgId = null;
        mMsgIdPlural = null;
        mMsgStr.clear();
        mCurrentEntryIsFuzzy = false;
    }

    private void parseHeader() throws PoParserException {
        if (!mMsgId.equals("")) {
            throw new PoParserException(mLineNumber + 1, "Missing header");
        }
        String header = mMsgStr.get(0);
        Matcher matcher = HEADER_PATTERN.matcher(header);
        if (!matcher.find()) {
            throw new PoParserException(
                    mLineNumber + 1, "Can't find plural definition in header:\n" + header);
        }
        mPluralCount = Integer.parseInt(matcher.group(1));
        // Simplify expressionString: remove spaces and surrounding parenthesis
        String expressionString = matcher.group(2).replace(" ", "");
        if (expressionString.startsWith("(") && expressionString.endsWith(")")) {
            expressionString = expressionString.substring(1, expressionString.length() - 1);
        }
        Messages.PluralExpression expression = sPluralExpressionByString.get(expressionString);
        if (expression == null) {
            throw new PoParserException(
                    mLineNumber + 1, "Unknown plural expression: " + expressionString);
        }
        mMessages = new Messages(expression);
    }

    // Internal function to be able to early returns and still get the clean of member vars
    // in addCurrentEntry()
    private void doAddCurrentEntry() throws PoParserException {
        if (mMsgIdPlural == null) {
            String id;
            if (mMsgCtxt == null) {
                id = mMsgId;
            } else {
                id = createIdWithContext(mMsgCtxt, mMsgId);
            }
            String message = mMsgStr.get(0);
            if (message.isEmpty()) {
                return;
            }
            mMessages.plainEntries.put(id, message);
        } else {
            if (mMsgStr.size() != mPluralCount) {
                throw new PoParserException(
                        mLineNumber - 1,
                        String.format(
                                Locale.US,
                                "Wrong number of msgstr for plural entry (%s, %s). Expected %d, found %d.",
                                mMsgId,
                                mMsgIdPlural,
                                mPluralCount,
                                mMsgStr.size()));
            }
            for (String message : mMsgStr) {
                if (message.isEmpty()) {
                    return;
                }
            }
            Messages.PluralId pluralId = new Messages.PluralId(mMsgId, mMsgIdPlural);
            String[] strings = new String[mMsgStr.size()];
            mMessages.pluralEntries.put(pluralId, mMsgStr.toArray(strings));
        }
    }

    static String parseString(String string) {
        // Remove surrounding quotes. Assume they are there.
        string = string.substring(1, string.length() - 1);

        return string.replace("\\\"", "\"").replace("\\n", "\n").replace("\\\\", "\\");
    }
}
