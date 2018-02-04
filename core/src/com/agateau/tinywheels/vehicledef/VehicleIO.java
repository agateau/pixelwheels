/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.tinywheels.vehicledef;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.agateau.utils.FileUtils;

/**
 * Read vehicle XML files and returns POJO for them
 */
public class VehicleIO {
    public static VehicleDef get(String id) {
        String fileName = "vehicles/" + id + ".xml";
        FileHandle handle = FileUtils.assets(fileName);
        if (!handle.exists()) {
            throw new RuntimeException("No such file " + fileName);
        }
        XmlReader.Element root = FileUtils.parseXml(handle);
        return get(root, id);
    }

    public static VehicleDef get(XmlReader.Element root, String id) {
        VehicleDef data = new VehicleDef();
        data.id = id;
        data.name = root.getAttribute("name");
        data.speed = root.getFloatAttribute("speed");

        XmlReader.Element mainElement = root.getChildByName("main");
        data.mainImage = mainElement.getAttribute("image");

        for (XmlReader.Element element : root.getChildrenByName("axle")) {
            AxleDef axle = new AxleDef();
            axle.width = element.getFloatAttribute("width");
            axle.y = element.getFloatAttribute("y");
            axle.steer = element.getFloatAttribute("steer", 0);
            axle.drive = element.getFloatAttribute("drive", 1);
            axle.drift = element.getBooleanAttribute("drift", true);
            data.axles.add(axle);
        }
        return data;
    }
}
