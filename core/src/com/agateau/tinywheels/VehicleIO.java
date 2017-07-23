package com.agateau.tinywheels;

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
