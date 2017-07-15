package com.greenyetilab.tinywheels;

import com.badlogic.gdx.files.FileHandle;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.Introspector;

/**
 * Customization of the gameplay
 */
public class GamePlay {
    public int racerCount = 6;
    public int maxDrivingForce = 60;
    public int lowSpeed = 20;
    public int maxSpeed = 180;
    public int maxLateralImpulse = 3;
    public int maxSkidmarks = 60;

    public int lowSpeedMaxSteer = 20;
    public int highSpeedMaxSteer = 5;
    public int vehicleDensity = 14;
    public int vehicleRestitution = 1;
    public int groundDragFactor = 8;
    public int borderRestitution = 1;

    public int viewportWidth = 70;

    public int turboStrength = 500;
    public float turboDuration = 0.6f;

    public boolean alwaysShowTouchInput = false;

    public boolean showTestTrack = false;
    public boolean createSpeedReport = false;

    public static final GamePlay instance = new GamePlay();
}
