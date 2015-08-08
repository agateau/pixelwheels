package com.greenyetilab.tinywheels;

import com.badlogic.gdx.files.FileHandle;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.Introspector;

/**
 * Customization of the gameplay
 */
public class GamePlay {
    public int racerCount = 6;
    public int maxDrivingForce = 70;
    public int lowSpeed = 20;
    public int midSpeed = 140;
    public int maxSpeed = 200;
    public int maxLateralImpulse = 8;
    public int maxSkidmarks = 200;

    public int lowSpeedMaxSteer = 30;
    public int highSpeedMaxSteer = 6;
    public int vehicleDensity = 14;
    public int vehicleRestitution = 1;
    public int groundDragFactor = 8;
    public int borderRestitution = 1;

    public boolean rotateCamera = true;
    public int viewportWidth = 70;

    public int turboStrength = 600;
    public float turboDuration = 0.35f;

    public int spinImpulse = 80;
    public int spinDuration = 2;

    public static final GamePlay instance = new GamePlay();

    private final Introspector mIntrospector = new Introspector(GamePlay.class, this);

    public Introspector getIntrospector() {
        return mIntrospector;
    }

    public void load() {
        mIntrospector.load(getFileHandle());
    }

    private static final GamePlay sReference = new GamePlay();
    public void save() {
        mIntrospector.save(getFileHandle(), sReference);
    }

    private static FileHandle getFileHandle() {
        return FileUtils.getUserWritableFile("gameplay.xml");
    }
}
