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
package com.agateau.pixelwheels.obstacles.tiled;

import com.agateau.pixelwheels.GameWorld;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

/** Handles coalescing all "full" obstacle cells to create fewer, larger obstacle bodies. */
class FullObstacleCreator {
    final boolean[][] mCells;

    /**
     * A TiledObstacleDef which does not itself create the obstacle bodies: it just fills mCells.
     * FullObstacleCreator.create() then pass over all mCells to create bodies.
     */
    private final TiledObstacleDef mFullObstacleDef =
            new TiledObstacleDef() {
                @Override
                public void create(
                        GameWorld world,
                        int col,
                        int row,
                        int tileSize,
                        TiledMapTileLayer.Cell cell) {
                    mCells[col][row] = true;
                }
            };

    public FullObstacleCreator(int width, int height) {
        mCells = new boolean[width][height];
    }

    public TiledObstacleDef getObstacleDef() {
        return mFullObstacleDef;
    }

    /** Create the rectangles: note that mCells is scanned *bottom-to-top*, not *top-to-bottom* */
    public void create(GameWorld world, int tileSize) {
        int width = mCells.length;
        int height = mCells[0].length;

        Rectangle rect = new Rectangle();
        rect.x = -0.5f;
        rect.y = -0.5f;

        int startTx = -1;
        for (int ty = 0; ty < height; ++ty) {
            // Trick: we let `tx` go to `width` inclusive and simulate an empty column at
            // `mCells[width]` to handle the case where the last cell of the row is filled.
            for (int tx = 0; tx <= width; ++tx) {
                boolean filled = tx < width && mCells[tx][ty];
                if (startTx >= 0) {
                    // We are inside a rectangle
                    if (!filled) {
                        // We found the end of a filled row span, check if cells below the span are
                        // filled too, so that we can include them in the same rectangle. If they
                        // are, clear them so that we do not create another rectangle for them when
                        // we process the next rows
                        int endTy;
                        for (endTy = ty + 1; endTy < height; ++endTy) {
                            if (isRowSpanFilled(startTx, tx, endTy)) {
                                clearRowSpan(startTx, tx, endTy);
                            } else {
                                break;
                            }
                        }

                        rect.width = tx - startTx;
                        rect.height = endTy - ty;
                        RectangleDef.createRectangle(world, startTx, ty, tileSize, rect);
                        startTx = -1;
                    }
                } else {
                    if (filled) {
                        startTx = tx;
                    }
                }
            }
        }
    }

    private boolean isRowSpanFilled(int startTx, int endTx, int ty) {
        for (int tx = startTx; tx < endTx; ++tx) {
            if (!mCells[tx][ty]) {
                return false;
            }
        }
        return true;
    }

    private void clearRowSpan(int startTx, int endTx, int ty) {
        for (int tx = startTx; tx < endTx; ++tx) {
            mCells[tx][ty] = false;
        }
    }
}
