package com.greenyetilab.race;

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

    public static final float VIEWPORT_WIDTH = 40;
    public static final float CAMERA_ADVANCE_PERCENT = 0.7f;

    public static final float SCORE_PER_METER = 1;
    public static final float VIEWPORT_POOL_RECYCLE_HEIGHT = 10;

    public static final int SCORE_CIVIL_HIT = -50;
    public static final int SCORE_ENEMY_HIT = 100;
    public static final int SCORE_GIFT_PICK = 400;
}
