package com.agateau.utils;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * A class to write data in CSV format
 */
public class CsvWriter {
    private final FileHandle mHandle;
    private final Writer mWriter;
    private char mFieldSeparator = ';';

    public CsvWriter(FileHandle handle) {
        mHandle = handle;
        mWriter = mHandle.writer(false /* append */);
    }

    public void setFieldSeparator(char separator) {
        mFieldSeparator = separator;
    }

    public void addRow(Object... args) {
        try {
            for (int i = 0, n = args.length; i < n; ++i) {
                if (i > 0) {
                    mWriter.append(mFieldSeparator);
                }
                Object value = args[i];
                String text;
                if (value instanceof Float) {
                    text = String.format(Locale.US, "%f", ((Float) value).floatValue());
                } else {
                    text = value.toString();
                }
                mWriter.append(text);
            }
            mWriter.append('\n');
            mWriter.flush();
        } catch (IOException e) {
            NLog.e("Failed to write CSV file");
            e.printStackTrace();
        }
    }
}
