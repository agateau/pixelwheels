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
    @Rule public TemporaryFolder mTemporaryFolder = new TemporaryFolder();

    @Test
    public void testCreateLogFile() throws IOException {
        String path = mTemporaryFolder.getRoot() + File.separator + "test.log";

        LogFilePrinter printer = new LogFilePrinter(path);
        printer.setMessageFormatter(
                (level, tag, message) ->
                        String.format(Locale.US, "%d %s %s", level, tag, message));
        printer.print(12, "tag", "hello");

        String content = FileUtils.readUtf8(new FileInputStream(path));
        assertThat(content, is("12 tag hello\n"));
    }
}
