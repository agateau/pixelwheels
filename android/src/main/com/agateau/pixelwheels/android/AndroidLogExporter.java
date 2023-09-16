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
package com.agateau.pixelwheels.android;

import static com.agateau.translations.Translator.tr;

import android.content.Context;
import android.net.Uri;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import com.agateau.pixelwheels.LogExporter;
import com.agateau.utils.log.LogFilePrinter;
import java.io.File;

/** Implements LogExporter for Android by sending an email */
public class AndroidLogExporter implements LogExporter {
    private static final String EMAIL_RECIPIENT = "pixelwheels@agateau.com";

    private final Context mContext;
    private final LogFilePrinter mLogFilePrinter;

    public AndroidLogExporter(Context context, LogFilePrinter logFilePrinter) {
        mContext = context;
        mLogFilePrinter = logFilePrinter;
    }

    @Override
    public void exportLogs() {
        mLogFilePrinter.flush();
        File file = mContext.getFileStreamPath(mLogFilePrinter.getPath());
        Uri contentUri =
                FileProvider.getUriForFile(
                        mContext, "com.agateau.tinywheels.android.fileprovider", file);

        new ShareCompat.IntentBuilder(mContext)
                .setType("message/rfc822")
                .addEmailTo(EMAIL_RECIPIENT)
                .setSubject("Pixel Wheels bug report")
                .addStream(contentUri)
                .setChooserTitle(tr("Share via"))
                .startChooser();
    }

    @Override
    public String getDescription() {
        return tr("Hit a bug? Use this button to report it.");
    }

    @Override
    public String getActionText() {
        return tr("REPORT BUG");
    }
}
