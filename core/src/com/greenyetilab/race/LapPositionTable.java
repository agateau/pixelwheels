package com.greenyetilab.race;

/**
 * Can provide the position within a lap based on x, y (in tile pixels)
 */
public class LapPositionTable {
    private final int mWidth;
    private final int mHeight;
    private final int[] mPositions;

    public LapPositionTable(int width, int height) {
        mWidth = width;
        mHeight = height;
        mPositions = new int[width * height];
    }

    public void set(int x, int y, int position) {
        mPositions[y * mWidth + x] = position;
    }

    public int get(int x, int y) {
        return  mPositions[y * mWidth + x];
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public static float distanceFromPosition(int value) {
        return (float)(value & 0xff00) / 255;
    }

    public static int sectionFromPosition(int value) {
        return (value & 0xff0000) >> 16;
    }

    public static int createPosition(int section, float distance) {
        return (section << 16) | ((int)(distance * 255) << 8) | 0xff;
    }
}
