package com.greenyetilab.tinywheels;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.greenyetilab.utils.FileUtils;

/**
 * Read vehicle XML files and returns POJO for them
 */
public class VehicleIO {
    public static class AxleData {
        float width;
        float y;
        float steer;
        float drive;
    }

    public static class Data {
        String name;
        float speed;
        String mainImage;
        Array<AxleData> axles = new Array<AxleData>();
    }

    public static Data get(String name) {
        String fileName = "vehicles/" + name + ".xml";
        FileHandle handle = FileUtils.assets(fileName);
        if (!handle.exists()) {
            throw new RuntimeException("No such file " + fileName);
        }
        XmlReader.Element root = FileUtils.parseXml(handle);
        return  get(root);
    }

    public static Data get(XmlReader.Element root) {
        Data data = new Data();
        data.name = root.getAttribute("name");
        data.speed = root.getFloatAttribute("speed");

        XmlReader.Element mainElement = root.getChildByName("main");
        data.mainImage = mainElement.getAttribute("image");

        for (XmlReader.Element element : root.getChildrenByName("axle")) {
            AxleData axle = new AxleData();
            axle.width = element.getFloatAttribute("width");
            axle.y = element.getFloatAttribute("y");
            axle.steer = element.getFloatAttribute("steer", 0);
            axle.drive = element.getFloatAttribute("drive", 1);
            data.axles.add(axle);
        }
        return data;
    }
}
