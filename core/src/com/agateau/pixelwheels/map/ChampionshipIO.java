/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.map;

import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

/**
 * Loads a championship and its tracks from an XML file
 *
 * <p>See docs/map-format.md for details
 */
public class ChampionshipIO {
    public Championship load(FileHandle handle) {
        XmlReader.Element root = FileUtils.parseXml(handle);
        if (root == null) {
            NLog.e("Error loading championship from %s", handle.path());
            throw new RuntimeException("Error loading championship from " + handle.path());
        }
        try {
            return load(root);
        } catch (Exception e) {
            NLog.e("Error loading championship from %s: %s", handle.path(), e);
            e.printStackTrace();
            throw new RuntimeException("Error loading championship from " + handle.path());
        }
    }

    public Championship load(XmlReader.Element root) {
        String id = root.getAttribute("id");
        String name = root.getAttribute("name");
        Championship championship = new Championship(id, name);

        Array<XmlReader.Element> trackElements = root.getChildrenByName("track");
        Assert.check(trackElements.notEmpty(), "No tracks found in championship " + id);
        for (XmlReader.Element element : trackElements) {
            loadTrack(championship, element);
        }
        return championship;
    }

    private void loadTrack(Championship championship, XmlReader.Element root) {
        String id = root.getAttribute("id");
        String name = root.getAttribute("name");
        championship.addTrack(id, name);
    }
}
