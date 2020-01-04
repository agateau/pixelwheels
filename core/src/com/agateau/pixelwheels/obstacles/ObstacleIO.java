/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.obstacles;

import com.agateau.pixelwheels.TextureRegionProvider;
import com.agateau.utils.FileUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

/** Reads obstacles.xml and returns a list of ObstacleDef */
public class ObstacleIO {
    public static Array<ObstacleDef> getAll(TextureRegionProvider provider) {
        String fileName = "obstacles.xml";
        FileHandle handle = FileUtils.assets(fileName);
        if (!handle.exists()) {
            throw new RuntimeException("No such file " + fileName);
        }
        XmlReader.Element root = FileUtils.parseXml(handle);
        if (root == null) {
            throw new RuntimeException("Failed to parse " + fileName);
        }
        try {
            return getAll(provider, root);
        } catch (Exception e) {
            NLog.e("Error loading obstacles from %s: %s", fileName, e);
            e.printStackTrace();
            throw new RuntimeException("Error loading vehicle from " + fileName);
        }
    }

    private static Array<ObstacleDef> getAll(
            TextureRegionProvider provider, XmlReader.Element root) {
        Array<ObstacleDef> array = new Array<>();
        for (XmlReader.Element child : root.getChildrenByName("obstacle")) {
            array.add(get(provider, child));
        }
        return array;
    }

    private static ObstacleDef get(TextureRegionProvider provider, XmlReader.Element child) {
        String id = child.getAttribute("id");
        ObstacleDef def = new ObstacleDef(id);
        def.density = child.getFloatAttribute("density");
        def.dynamic = child.getBooleanAttribute("dynamic", true);
        String shapeName = child.getAttribute("shape");
        if ("circle".equals(shapeName)) {
            def.createCircleShape(provider);
        } else if ("rectangle".equals(shapeName)) {
            def.createRectangleShape(provider);
        } else {
            throw new RuntimeException("Unknown shape " + shapeName);
        }
        return def;
    }
}
