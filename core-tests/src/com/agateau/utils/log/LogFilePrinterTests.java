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
package com.agateau.utils.log;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.agateau.utils.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LogFilePrinterTests {
    private static final String LOG_FILENAME = "test.log";

    @Rule public TemporaryFolder mTemporaryFolder = new TemporaryFolder();

    @Test
    public void testCreateLogFile() throws IOException {
        String path = getLogPath();

        LogFilePrinter printer = new LogFilePrinter(path, 200);
        printer.setMessageFormatter(
                (level, tag, message) -> String.format(Locale.US, "%d %s %s", level, tag, message));
        printer.print(12, "tag", "hello");

        assertThat(readFile(path), is("12 tag hello\n"));
    }

    @Test
    public void testRotateLogFile() throws IOException {
        // GIVEN a log printer with a max size of 15 bytes
        String path = getLogPath();

        LogFilePrinter printer = new LogFilePrinter(path, 15);
        printer.setMessageFormatter((level, tag, message) -> message);

        // WHEN I log a short message
        printer.print(0, "", "aaaaa");

        // THEN it is in the main log
        assertThat(readFile(path), is("aaaaa\n"));

        // WHEN I log another short message
        printer.print(0, "", "bbbbb");

        // THEN it iis also in the main log
        assertThat(readFile(path), is("aaaaa\nbbbbb\n"));

        // WHEN I log a message which forces the rotation
        printer.print(0, "", "0123456789abcde");

        // THEN the backup log contains the previous content
        assertThat(readFile(path + LogFilePrinter.BACKUP_SUFFIX), is("aaaaa\nbbbbb\n"));

        // AND the main log contains the new message
        assertThat(readFile(path), is("0123456789abcde\n"));

        // WHEN I log another long message
        printer.print(0, "", "edcba0123456789");

        // THEN the backup log contains the previous main log content
        assertThat(readFile(path + LogFilePrinter.BACKUP_SUFFIX), is("0123456789abcde\n"));

        // AND the main log contains the new message
        assertThat(readFile(path), is("edcba0123456789\n"));
    }

    private String getLogPath() {
        return mTemporaryFolder.getRoot() + File.separator + LOG_FILENAME;
    }

    private String readFile(String path) throws IOException {
        return FileUtils.readUtf8(new FileInputStream(path));
    }
}
