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
package com.agateau.pixelwheels.vehicledef;

import com.agateau.utils.FileUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.utils.XmlReader;

/**
 * Read vehicle XML files and returns POJO for them.
 *
 * <p>Width and height are swapped, because vehicles are drawn vertically but body is horizontal
 */
public class VehicleIO {
    public static VehicleDef get(String id) {
        String fileName = "vehicles/" + id + ".xml";
        FileHandle handle = FileUtils.assets(fileName);
        if (!handle.exists()) {
            throw new RuntimeException("No such file " + fileName);
        }
        XmlReader.Element root = FileUtils.parseXml(handle);
        if (root == null) {
            throw new RuntimeException("Error loading vehicle from " + fileName);
        }
        try {
            return get(root, id);
        } catch (Exception e) {
            NLog.e("Error loading vehicle from %s: %s", fileName, e);
            e.printStackTrace();
            throw new RuntimeException("Error loading vehicle from " + fileName);
        }
    }

    public static VehicleDef get(XmlReader.Element root, String id) {
        VehicleDef data = new VehicleDef(id, root.getAttribute("name"));
        data.speed = root.getFloatAttribute("speed");

        float width = root.getFloatAttribute("height");
        float height = root.getFloatAttribute("width");

        XmlReader.Element mainElement = root.getChildByName("main");
        data.mainImage = mainElement.getAttribute("image");

        XmlReader.Element shapesElement = root.getChildByName("shapes");
        for (int i = 0, n = shapesElement.getChildCount(); i < n; ++i) {
            XmlReader.Element element = shapesElement.getChild(i);
            data.shapes.add(loadShape(element, width, height));
        }
        if (data.shapes.size == 0) {
            throw new RuntimeException("No shapes defined in vehicle " + id);
        }

        for (XmlReader.Element element : root.getChildrenByName("axle")) {
            AxleDef axle = new AxleDef();
            axle.width = element.getFloatAttribute("width");
            axle.y = element.getFloatAttribute("y");
            axle.steer = element.getFloatAttribute("steer", 0);
            axle.drive = element.getFloatAttribute("drive", 1);
            axle.drift = element.getBooleanAttribute("drift", true);
            axle.tyreSize = AxleDef.TyreSize.valueOf(element.getAttribute("tyreSize", "NORMAL"));
            data.axles.add(axle);
        }
        return data;
    }

    private static Shape2D loadShape(
            XmlReader.Element element, float vehicleWidth, float vehicleHeight) {
        String type = element.getName();
        if (type.equals("octogon")) {
            float width = element.getFloatAttribute("height");
            float height = element.getFloatAttribute("width");
            float x = element.getFloatAttribute("y", (vehicleWidth - width) / 2);
            float y = element.getFloatAttribute("x", (vehicleHeight - height) / 2);
            float corner = element.getFloatAttribute("corner", 0);
            Polygon polygon = new Polygon();
            polygon.setVertices(
                    new float[] {
                        width / 2 - corner,
                        -height / 2,
                        width / 2,
                        -height / 2 + corner,
                        width / 2,
                        height / 2 - corner,
                        width / 2 - corner,
                        height / 2,
                        -width / 2 + corner,
                        height / 2,
                        -width / 2,
                        height / 2 - corner,
                        -width / 2,
                        -height / 2 + corner,
                        -width / 2 + corner,
                        -height / 2
                    });
            polygon.translate(x - (vehicleWidth - width) / 2, y - (vehicleHeight - height) / 2);
            return polygon;
        } else if (type.equals("trapezoid")) {
            float bottomHeight = element.getFloatAttribute("bottomWidth");
            float topHeight = element.getFloatAttribute("topWidth");
            float height = Math.max(bottomHeight, topHeight);
            float width = element.getFloatAttribute("height");
            float x = element.getFloatAttribute("y", (vehicleWidth - width) / 2);
            float y = element.getFloatAttribute("x", (vehicleHeight - height) / 2);
            Polygon polygon = new Polygon();
            polygon.setVertices(
                    new float[] {
                        width / 2, topHeight / 2,
                        -width / 2, bottomHeight / 2,
                        -width / 2, -bottomHeight / 2,
                        width / 2, -topHeight / 2,
                    });
            polygon.translate(x - (vehicleWidth - width) / 2, y - (vehicleHeight - height) / 2);
            return polygon;
        } else {
            throw new RuntimeException("Unknown shape type: " + element);
        }
    }
}
