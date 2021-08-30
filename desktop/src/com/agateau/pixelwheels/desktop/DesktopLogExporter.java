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
package com.agateau.pixelwheels.desktop;

import static com.agateau.translations.Translator.tr;

import com.agateau.pixelwheels.LogExporter;
import com.agateau.utils.PlatformUtils;
import com.agateau.utils.log.LogFilePrinter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DesktopLogExporter implements LogExporter {
    private final LogFilePrinter mPrinter;

    public DesktopLogExporter(LogFilePrinter printer) {
        mPrinter = printer;
    }

    @Override
    public void exportLogs() {
        Path path = Paths.get(mPrinter.getPath());
        PlatformUtils.openURI(path.getParent().toString());
    }

    @Override
    public String getDescription() {
        return tr("Need to file a bug report? Use this button to find the log file to attach.");
    }

    @Override
    public String getActionText() {
        return tr("OPEN LOG FILE FOLDER");
    }
}
