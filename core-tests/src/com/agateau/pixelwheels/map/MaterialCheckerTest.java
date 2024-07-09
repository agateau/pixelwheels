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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MaterialCheckerTest {
    private static final int TILE_SIZE = 10;

    private static class TestTrack extends Track {
        private final String[] mLines;

        public TestTrack(String[] lines) {
            super(null, "id", "name");
            mLines = lines;
        }

        @Override
        public float getTileWidth() {
            return TILE_SIZE;
        }

        @Override
        public Material getMaterialAt(float x, float y) {
            int tx = MathUtils.floor(x / TILE_SIZE);
            int ty = MathUtils.floor(y / TILE_SIZE);
            Character ch = mLines[ty].charAt(tx);
            return materialFromCharacter(ch);
        }

        private static Material materialFromCharacter(Character character) {
            switch (character) {
                case '.':
                    return Material.ROAD;
                case 's':
                    return Material.SAND;
                default:
                    throw new RuntimeException("Unknown material character '" + character + "'");
            }
        }
    }

    private static final Track SAND_TRACK =
            new TestTrack(
                    new String[] {
                        //         111111
                        // 23456789012345
                        "ss....ssss....ss", // 0
                        "ss....ssss....ss", // 1
                        "ss....ssss....ss", // 2
                        "ss............ss", // 3
                        "ss............ss", // 4
                    });

    @Test
    public void testSand() {
        // GIVEN a checker on the SAND_TRACK
        MaterialChecker checker = new MaterialChecker(SAND_TRACK);

        // WHEN getMaterialAhead() is called from road to sand
        // THEN it returns sand
        assertThat(
                checker.getSlowestMaterialAhead(createVector2(3, 0), createVector2(8, 2)),
                is(Material.SAND));

        assertThat(
                checker.getSlowestMaterialAhead(createVector2(3, 0), createVector2(4, 4)),
                is(Material.ROAD));

        assertThat(
                checker.getSlowestMaterialAhead(createVector2(3, 0), createVector2(11, 1)),
                is(Material.SAND));
    }

    // Create a vector in pixel coordinate, centered on tile coordinates tx, ty
    private static Vector2 createVector2(int tx, int ty) {
        return new Vector2((float) (tx + 0.5) * TILE_SIZE, (float) (ty + 0.5) * TILE_SIZE);
    }
}
