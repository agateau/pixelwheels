package com.greenyetilab.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.greenyetilab.utils.log.NLog;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * This class can read and write public fields of a class and serialize the changes to an xml file
 */
public class Introspector {
    private final Class mClass;
    private final Object mObject;

    public Introspector(Class cls, Object object) {
        mClass = cls;
        mObject = object;
    }

    public void load(FileHandle handle) {
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
                field = mClass.getField(name);
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
                setInt(name, Integer.valueOf(value));
            }
        }
    }

    public void save(FileHandle handle, Object reference) {
        XmlWriter writer = new XmlWriter(handle.writer(false));
        try {
            XmlWriter root = writer.element("gameplay");
            for (Field field : mClass.getDeclaredFields()) {
                if (!Modifier.isPublic(field.getModifiers())) {
                    continue;
                }
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Object value = field.get(mObject);
                if (value.equals(field.get(reference))) {
                    continue;
                }
                root.element("key")
                        .attribute("name", field.getName())
                        .attribute("type", field.getType().toString())
                        .text(value.toString())
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

    public Object get(String key) {
        try {
            Field field = mClass.getField(key);
            return field.get(mObject);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException("get(" + key + ") failed. " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("get(" + key + ") failed. " + e);
        }
    }

    public int getInt(String key) {
        try {
            Field field = mClass.getField(key);
            return field.getInt(mObject);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException("getInt(" + key + ") failed. " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("getInt(" + key + ") failed. " + e);
        }
    }

    public void setInt(String key, int value) {
        try {
            Field field = mClass.getField(key);
            field.setInt(mObject, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException("setInt(" + key + ") failed. " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("setInt(" + key + ") failed. " + e);
        }
    }
}
