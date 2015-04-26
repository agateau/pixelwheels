package com.greenyetilab.utils;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.greenyetilab.utils.log.NLog;

public class FileUtils {
    public static String appName = "unnamed";
    public static FileHandle getUserWritableFile(String name) {
        FileHandle handle;
        if (Gdx.app.getType() == ApplicationType.Desktop) {
            handle = Gdx.files.external(".local/share/" + appName + "/" + name);
        } else {
            handle = Gdx.files.local(name);
        }
        return handle;
    }

    public static FileHandle getCacheDir() {
        FileHandle handle;
        if (Gdx.app.getType() == ApplicationType.Desktop) {
            handle = Gdx.files.external(".cache/" + appName);
        } else {
            if (!Gdx.files.isExternalStorageAvailable()) {
                return null;
            }
            handle = Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "/" + appName);
        }
        handle.mkdirs();
        return handle;
    }

    public static FileHandle assets(String path) {
        FileHandle handle = Gdx.files.internal(path);
        /* // Disabled for now: does not work for desktop releases
        if (Gdx.app.getType() == ApplicationType.Desktop) {
            handle = new FileHandle(new File(handle.path()));
        }
        */
        return handle;
    }

    public static XmlReader.Element parseXml(FileHandle handle) {
        XmlReader reader = new XmlReader();
        XmlReader.Element root;
        try {
            root = reader.parse(handle);
        } catch (IOException e) {
            NLog.e("Failed to parse xml file from %s. Exception: %s.", handle.path(), e.toString());
            return null;
        }
        if (root == null) {
            NLog.e("Failed to parse xml file from %s. No root element.", handle.path());
            return null;
        }
        return root;
    }
}
