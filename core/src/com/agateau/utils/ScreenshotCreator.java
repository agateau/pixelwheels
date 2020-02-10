/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ScreenshotCreator {
    // %tF  == year-month-day
    // %<tT == hours:minutes:seconds
    // %<tL == milliseconds
    private static final String FILENAME_FORMAT = "screenshot-%tF-%<tT.%<tL.png";
    private static Pixmap sPixmap;
    private static PixmapIO.PNG sPNG;

    public static String saveScreenshot() {
        Pixmap pixmap = takeScreenshot();
        FileHandle handle = generateFileHandle();
        try {
            sPNG.write(handle, pixmap);
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error writing PNG: " + handle, ex);
        }
        return handle.path();
    }

    private static Pixmap takeScreenshot() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        if (sPixmap == null) {
            init();
        }
        Gdx.gl.glReadPixels(
                0, 0, width, height, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, sPixmap.getPixels());
        return sPixmap;
    }

    private static void init() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        sPixmap = new Pixmap(width, height, Pixmap.Format.RGB888);
        // Guess at deflated size
        sPNG = new PixmapIO.PNG((int) (width * height * 1.5f));
    }

    private static FileHandle generateFileHandle() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String filename = String.format(Locale.US, FILENAME_FORMAT, calendar);
        return FileUtils.getUserWritableFile(filename);
    }
}
