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
    static final float VIEWPORT_WIDTH = 60;
    public static final float SCORE_PER_METER = 1;
    public static final int SCORE_CAR_HIT = 500;
}
