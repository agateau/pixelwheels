/*
 * Copyright 2023 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Map;

/** Helper class to log extra information and export logs */
public class LogExporterUtils {
    private static final Gson sGson = new GsonBuilder().create();

    public static void exportLogs(LogExporter exporter) {
        Preferences prefs = Gdx.app.getPreferences(Constants.CONFIG_FILENAME);
        NLog.i("preferences: %s", dumpPreferences(prefs));

        exporter.exportLogs();
    }

    static String dumpPreferences(Preferences prefs) {
        Map<String, ?> map = prefs.get();

        JsonObject root = new JsonObject();
        for (Map.Entry<String, ?> kv : map.entrySet()) {
            String value = kv.getValue().toString();
            root.add(kv.getKey(), new JsonPrimitive(value));
        }
        return sGson.toJson(root);
    }
}
