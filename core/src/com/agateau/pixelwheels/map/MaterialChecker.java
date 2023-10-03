/*
 * Copyright 2023 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.map;

import com.badlogic.gdx.math.Vector2;

/** Helper class to compute the slowest material between two positions */
public class MaterialChecker {
    private final Vector2 mTmpVector = new Vector2();
    private final Track mTrack;

    public MaterialChecker(Track track) {
        mTrack = track;
    }

    // Coordinates are in pixels
    public Material getSlowestMaterialAhead(Vector2 position, Vector2 target) {
        Material slowest = Material.ROAD;

        float len = mTmpVector.set(target).sub(position).len();
        int steps = (int) Math.ceil(len / mTrack.getTileWidth());

        // Start from 1 because we don't want to look *after* the current position
        // Test with <= because we want to look at the final position
        for (int progress = 1; progress <= steps; ++progress) {
            mTmpVector.set(position).lerp(target, ((float) progress) / steps);
            Material material = mTrack.getMaterialAt(mTmpVector);
            if (material.getSpeed() < slowest.getSpeed()) {
                slowest = material;
            }
        }
        return slowest;
    }
}
