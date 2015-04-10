package com.greenyetilab.race;

import com.badlogic.gdx.files.FileHandle;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.Introspector;

/**
 * Customization of the gameplay
 */
public class GamePlay {
    public static int racerCount = 8;
    public static int maxDrivingForce = 120;
    public static int maxLateralImpulse = 8;
    public static int maxSkidmarks = 20;
    public static int lowSpeedMaxSteer = 40;
    public static int highSpeedMaxSteer = 10;
    public static int vehicleDensity = 14;
    public static int vehicleRestitution = 1;
    public static int borderRestitution = 1;
    public static int viewportWidth = 60;

    public static int spinImpulse = 80;
    public static int spinDuration = 2;

    public static int hudButtonSize = 120;

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
