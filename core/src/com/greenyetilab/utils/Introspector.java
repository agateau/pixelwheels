package com.greenyetilab.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.greenyetilab.race.GamePlay;
import com.greenyetilab.utils.log.NLog;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * A set of static methods to read/write the fields of an all-static class
 */
public class Introspector {
    public static void load(Class cls, FileHandle handle) {
        if (!handle.exists()) {
            return;
        }
        XmlReader.Element root = FileUtils.parseXml(handle);
        if (root == null) {
            return;
        }
        for (XmlReader.Element keyElement : root.getChildrenByName("key")) {
            String name = keyElement.getAttribute("name");
            String type = keyElement.getAttribute("type");
            String value = keyElement.getText();
            Field field;
            try {
                field = cls.getField(name);
            } catch (NoSuchFieldException e) {
                NLog.e("No field named '%s', skipping", name);
                continue;
            }
            String fieldType = field.getType().toString();
            if (!fieldType.equals(type)) {
                NLog.e("Field '%s' is of type '%s', but XML expected '%s', skipping", name, fieldType, type);
                continue;
            }
            if (type.equals("int")) {
                setInt(cls, name, Integer.valueOf(value));
            }
        }
    }

    public static void save(Class cls, FileHandle handle) {
        XmlWriter writer = new XmlWriter(handle.writer(false));
        try {
            XmlWriter root = writer.element("gameplay");
            for (Field field : cls.getDeclaredFields()) {
                root.element("key")
                        .attribute("name", field.getName())
                        .attribute("type", field.getType().toString())
                        .text(field.get(null).toString())
                        .pop();
            }
            root.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static int getInt(Class cls, String key) {
        try {
            Field field = GamePlay.class.getField(key);
            return field.getInt(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException("getInt(" + key + ") failed. " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("getInt(" + key + ") failed. " + e);
        }
    }

    public static void setInt(Class cls, String key, int value) {
        try {
            Field field = cls.getField(key);
            field.setInt(null, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException("setInt(" + key + ") failed. " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("setInt(" + key + ") failed. " + e);
        }
    }
}
