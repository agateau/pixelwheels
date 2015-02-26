package com.greenyetilab.race;

/**
 * Can provide the position within a lap based on x, y (in tile pixels)
 */
public class LapPositionTable {
    private final int mWidth;
    private final int mHeight;
    private final LapPosition[] mPositions;

    public LapPositionTable(int width, int height) {
        mWidth = width;
        mHeight = height;
        mPositions = new LapPosition[width * height];
    }

    public void set(int x, int y, LapPosition position) {
        mPositions[y * mWidth + x] = position;
    }

    public LapPosition get(int x, int y) {
        return  mPositions[y * mWidth + x];
    }
}
