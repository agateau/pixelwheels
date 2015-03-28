package com.greenyetilab.race;

import com.badlogic.gdx.files.FileHandle;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.Introspector;

/**
 * Customization of the gameplay
 */
public class GamePlay {
    public static int racerCount = 4;
    public static int maxDrivingForce = 100;
    public static int maxLateralImpulse = 8;
    public static int maxSkidmarks = 20;

    public static void load() {
        Introspector.load(GamePlay.class, getFileHandle());
    }

    public static void save() {
        Introspector.save(GamePlay.class, getFileHandle());
    }

    private static FileHandle getFileHandle() {
        return FileUtils.getUserWritableFile("gameplay.xml");
    }
}
