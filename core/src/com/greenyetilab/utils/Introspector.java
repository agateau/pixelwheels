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
    private final Object mReference;
    private final Object mObject;
    private final FileHandle mFileHandle;

    public Introspector(Object object, Object reference, FileHandle fileHandle) {
        mClass = object.getClass();
        mObject = object;
        mReference = reference;
        mFileHandle = fileHandle;
    }

    public void load() {
        if (!mFileHandle.exists()) {
            return;
        }
        XmlReader.Element root = FileUtils.parseXml(mFileHandle);
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
            } else if (type.equals("boolean")) {
                set(name, Boolean.valueOf(value));
            }
        }
    }

    public void save() {
        XmlWriter writer = new XmlWriter(mFileHandle.writer(false));
        try {
            XmlWriter root = writer.element("object");
            for (Field field : mClass.getDeclaredFields()) {
                if (!Modifier.isPublic(field.getModifiers())) {
                    continue;
                }
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Object value = field.get(mObject);
                if (value.equals(field.get(mReference))) {
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

    public <T> T get(String key) {
        return getFrom(mObject, key);
    }

    public <T> T getReference(String key) {
        return getFrom(mReference, key);
    }

    private <T> T getFrom(Object object, String key) {
        try {
            Field field = mClass.getField(key);
            return (T)field.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException("get(" + key + ") failed. " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("get(" + key + ") failed. " + e);
        }
    }

    public <T> void set(String key, T value) {
        try {
            Field field = mClass.getField(key);
            field.set(mObject, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException("set(" + key + ") failed. " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("set(" + key + ") failed. " + e);
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

    public float getFloat(String key) {
        try {
            Field field = mClass.getField(key);
            return field.getFloat(mObject);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException("getInt(" + key + ") failed. " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("getInt(" + key + ") failed. " + e);
        }
    }

    public void setFloat(String key, float value) {
        try {
            Field field = mClass.getField(key);
            field.setFloat(mObject, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException("setInt(" + key + ") failed. " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("setInt(" + key + ") failed. " + e);
        }
    }
}
