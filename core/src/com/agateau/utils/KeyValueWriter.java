/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.utils;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/** A class to write data as JSONL rows */
public class KeyValueWriter {
    private final Writer mWriter;
    private boolean mFirstValue = true;

    public KeyValueWriter(FileHandle handle) {
        mWriter = handle.writer(false /* append */);
    }

    public void put(String key, Object value) {
        try {
            if (mFirstValue) {
                mWriter.append('{');
                mFirstValue = false;
            } else {
                mWriter.append(',');
            }
            mWriter.append('"');
            mWriter.append(key);
            mWriter.append("\":");

            if (value instanceof Float) {
                String text = String.format(Locale.US, "%f", (Float) value);
                mWriter.append(text);
            } else if (value instanceof Integer) {
                String text = String.format(Locale.US, "%d", (Integer) value);
                mWriter.append(text);
            } else if (value instanceof Boolean) {
                String text = (Boolean) value ? "true" : "false";
                mWriter.append(text);
            } else {
                String text = value.toString().replace("\"", "\\\"");
                mWriter.append('"');
                mWriter.append(text);
                mWriter.append('"');
            }
        } catch (IOException e) {
            NLog.e("Failed to write KV file");
            e.printStackTrace();
        }
    }

    public void endRow() {
        try {
            mWriter.append("}\n");
            mWriter.flush();
        } catch (IOException e) {
            NLog.e("Failed to write KV file");
            e.printStackTrace();
        }
        mFirstValue = true;
    }
}
