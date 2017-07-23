package com.agateau.tinywheels;

import com.agateau.utils.log.NLog;

/**
 * A component to track the racer time
 */
class LapPositionComponent {
    private final MapInfo mMapInfo;
    private final Vehicle mVehicle;

    private float mBestLapTime = -1;
    private float mTotalTime = 0;
    private float mLapTime = 0;
    private int mLapCount = 0;
    private final LapPosition mLapPosition = new LapPosition();
    private boolean mHasFinishedRace = false;

    public LapPositionComponent(MapInfo mapInfo, Vehicle vehicle) {
        mMapInfo = mapInfo;
        mVehicle = vehicle;
    }

    public void act(float delta) {
        if (mHasFinishedRace) {
            return;
        }
        mTotalTime += delta;
        mLapTime += delta;
        updatePosition();
    }

    public float getBestLapTime() {
        return mBestLapTime;
    }

    public float getTotalTime() {
        return mTotalTime;
    }

    public int getLapCount() {
        return mLapCount;
    }

    public float getLapDistance() {
        return mLapPosition.getLapDistance();
    }

    public boolean hasFinishedRace() {
        return mHasFinishedRace;
    }

    private void updatePosition() {
        final int oldSectionId = mLapPosition.getSectionId();
        final float PFU = 1 / Constants.UNIT_FOR_PIXEL;
        final int pixelX = (int)(PFU * mVehicle.getX());
        final int pixelY = (int)(PFU * mVehicle.getY());
        final LapPosition pos = mMapInfo.getLapPositionTable().get(pixelX, pixelY);
        if (pos == null) {
            NLog.e("No LapPosition at pixel " + pixelX + " x " + pixelY);
            return;
        }
        mLapPosition.copy(pos);
        if (mLapPosition.getSectionId() == 0 && oldSectionId > 1) {
            if (mLapCount >= 1) {
                // Check lap count before calling onLapCompleted() because we get there when we
                // first cross the line at start time and we don't want to count this as a
                // completed lap.
                onLapCompleted();
            }
            ++mLapCount;
            if (mLapCount > mMapInfo.getTotalLapCount()) {
                --mLapCount;
                mHasFinishedRace = true;
            }
        } else if (mLapPosition.getSectionId() > 1 && oldSectionId == 0) {
            --mLapCount;
        }
    }

    private void onLapCompleted() {
        if (mBestLapTime < 0 || mLapTime < mBestLapTime) {
            mBestLapTime = mLapTime;
        }
        mLapTime = 0;
    }

    public void markRaceFinished() {
        if (mHasFinishedRace) {
            return;
        }
        // Completing one lap represents that percentage of the race
        float lapPercent = 1f / mMapInfo.getTotalLapCount();

        float lastLapPercent = mLapPosition.getLapDistance() / mMapInfo.getLapPositionTable().getSectionCount() * lapPercent;
        float percentageDone = (mLapCount - 1) * lapPercent + lastLapPercent;

        mTotalTime = mTotalTime / percentageDone;
        if (mBestLapTime < 0) {
            mBestLapTime = mTotalTime / mMapInfo.getTotalLapCount();
        }
        mHasFinishedRace = true;
    }
}
