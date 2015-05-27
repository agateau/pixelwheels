package com.greenyetilab.tinywheels;

/**
 * Created by aurelien on 25/11/14.
 */
public class Constants {
    public static final float UNIT_FOR_PIXEL = 1f / 20f;
    public static final int Z_GROUND = 0;
    public static final int Z_VEHICLES = 1;
    public static final int Z_SHADOWS = 2;
    public static final int Z_OBSTACLES = 3;
    public static final int Z_FLYING = 4;

    public static final int Z_COUNT = Z_FLYING + 1;

    public static final float VIEWPORT_WIDTH = 45;
    public static final float CAMERA_ADVANCE_PERCENT = 0.25f;

    public static final int PLAYER_HEALTH = 3;

    // In degrees per second
    public static final float MAX_CAMERA_ROTATION_SPEED = 180;

    // When the camera must rotate by less than this angle, its maximum rotation speed is linearly
    // interpolated between 1 and MAX_CAMERA_ROTATION_SPEED. When it must rotate by more than this
    // angle its maximum rotation speed is MAX_CAMERA_ROTATION_SPEED.
    public static final float MIN_ANGLE_FOR_MAX_CAMERA_ROTATION_SPEED = 15;
}
