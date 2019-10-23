/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.racer;

import com.agateau.utils.FileUtils;
import com.agateau.utils.KeyValueWriter;
import java.util.HashMap;

/** A generic probe */
public class Probe implements Racer.Component {
    protected final KeyValueWriter mWriter;
    private float mLogTime = 0;
    private final HashMap<String, Object> mValues = new HashMap<>();

    Probe(String fileName) {
        mWriter = new KeyValueWriter(FileUtils.getUserWritableFile(fileName));
        mWriter.setFieldSeparator(' ');
    }

    @Override
    public void act(float delta) {
        mValues.put("t", mLogTime);
        for (String key : mValues.keySet()) {
            mWriter.put(key, mValues.get(key));
        }
        mWriter.endRow();
        mLogTime += delta;
    }

    public void addValue(String name, Object value) {
        mValues.put(name, value);
    }
}
