/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.pixelwheels.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;

public class MouseCursorManager {
    private static final long AUTOHIDE_DELAY = 4 * 1000;
    private final Cursor mEmptyCursor;
    private Cursor mCursor;

    private int mOldX, mOldY;
    private long mTimestamp;
    private boolean mIsVisible = false;

    public MouseCursorManager() {
        refreshAssets();

        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        mEmptyCursor = Gdx.graphics.newCursor(pixmap, 0, 0);

        updateCoordinates();
        if (supportsCursor()) {
            hideMouseCursor();
        }
    }

    private void updateCoordinates() {
        mTimestamp = System.currentTimeMillis();
        mOldX = Gdx.input.getX();
        mOldY = Gdx.input.getY();
    }

    public void refreshAssets() {
        if (mCursor != null) {
            mCursor.dispose();
        }

        Pixmap pixmap = new Pixmap(Gdx.files.internal("ui/cursor.png"));
        mCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
    }

    public void act() {
        if (!supportsCursor()) {
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

    private void showMouseCursor() {
        mIsVisible = true;
        Gdx.graphics.setCursor(mCursor);
    }

    private void hideMouseCursor() {
        mIsVisible = false;
        Gdx.graphics.setCursor(mEmptyCursor);
    }

    private boolean supportsCursor() {
        return mEmptyCursor != null;
    }
}
