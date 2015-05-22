package com.greenyetilab.tinywheels;

import com.greenyetilab.utils.log.NLog;

/**
 * A component to track the racer time
 */
class StopWatchComponent {
    private float mBestLapTime = -1;
    private float mTotalTime = 0;
    private float mLapTime = 0;

    public void act(float delta) {
        mTotalTime += delta;
        mLapTime += delta;
    }

    public void onLapCompleted() {
        if (mBestLapTime < 0 || mLapTime < mBestLapTime) {
            mBestLapTime = mLapTime;
        }
        mLapTime = 0;
    }

    public float getBestLapTime() {
        return mBestLapTime;
    }

    public float getTotalTime() {
        return mTotalTime;
    }
}
