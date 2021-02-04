/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;

public class MouseCursorManager {
    private static final long AUTOHIDE_DELAY = 4 * 1000;
    private final Cursor mEmptyCursor;
    private Cursor mCursor;

    private int mOldX, mOldY;
    private long mTimestamp;
    private boolean mIsVisible = false;
    private boolean mReady = false;

    private static MouseCursorManager sInstance;

    public static MouseCursorManager getInstance() {
        if (sInstance == null) {
            sInstance = new MouseCursorManager();
        }
        return sInstance;
    }

    private MouseCursorManager() {
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        mEmptyCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
    }

    public void setCursorPixmap(FileHandle fileHandle) {
        if (mCursor != null) {
            mCursor.dispose();
        }

        Pixmap pixmap = new Pixmap(fileHandle);
        mCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
    }

    public boolean isVisible() {
        return mIsVisible;
    }

    public void act() {
        if (mCursor == null) {
            return;
        }
        if (!mReady) {
            actNotReady();
            return;
        }
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        boolean hasMoved = x != mOldX || y != mOldY;
        long now = System.currentTimeMillis();
        if (hasMoved) {
            mOldX = x;
            mOldY = y;
            mTimestamp = now;
            if (!mIsVisible) {
                showMouseCursor();
            }
        } else {
            if (mIsVisible && (now - mTimestamp) > AUTOHIDE_DELAY) {
                hideMouseCursor();
            }
        }
    }

    /**
     * Consider the game is still starting up until at least one of the input coordinates is not 0.
     * If we don't do that, then the code sees a fake mouse move from (0, 0) to the actual cursor
     * coordinates.
     */
    private void actNotReady() {
        hideMouseCursor();
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        if (x == 0 && y == 0) {
            return;
        }

        mReady = true;
        mOldX = x;
        mOldY = y;
        mTimestamp = System.currentTimeMillis();
    }

    private void showMouseCursor() {
        mIsVisible = true;
        Gdx.graphics.setCursor(mCursor);
    }

    private void hideMouseCursor() {
        mIsVisible = false;
        Gdx.graphics.setCursor(mEmptyCursor);
    }
}
