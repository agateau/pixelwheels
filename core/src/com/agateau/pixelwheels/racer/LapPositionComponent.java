/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.racer;

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.map.LapPosition;
import com.agateau.pixelwheels.map.Track;
import com.agateau.utils.log.NLog;

/**
 * A component to track the racer time
 */
public class LapPositionComponent implements Racer.Component {
    private final Track mTrack;
    private final Vehicle mVehicle;

    private float mBestLapTime = -1;
    private float mTotalTime = 0;
    private float mLapTime = 0;
    private int mLapCount = 0;
    private final LapPosition mLapPosition = new LapPosition();
    private boolean mHasFinishedRace = false;

    public LapPositionComponent(Track track, Vehicle vehicle) {
        mTrack = track;
        mVehicle = vehicle;
    }

    @Override
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
        final LapPosition pos = mTrack.getLapPositionTable().get(pixelX, pixelY);
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
            if (mLapCount > mTrack.getTotalLapCount()) {
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
        float lapPercent = 1f / mTrack.getTotalLapCount();

        float lastLapPercent = mLapPosition.getLapDistance() / mTrack.getLapPositionTable().getSectionCount() * lapPercent;
        float percentageDone = (mLapCount - 1) * lapPercent + lastLapPercent;

        mTotalTime = mTotalTime / percentageDone;
        if (mBestLapTime < 0) {
            mBestLapTime = mTotalTime / mTrack.getTotalLapCount();
        }
        mHasFinishedRace = true;
    }
}
