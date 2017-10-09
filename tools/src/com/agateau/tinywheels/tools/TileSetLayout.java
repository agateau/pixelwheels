/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
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
package com.agateau.tinywheels.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/** Contains extra information that can only be calculated after a Tiled Map's tile set images are loaded.
 * @author David Fraska */
public class TileSetLayout {

    public final BufferedImage image;
    private final IntMap<Vector2> imageTilePositions;
    private int numRows;
    private int numCols;
    public final int numTiles;
    public final int firstgid;

    /** Constructs a Tile Set layout. The tile set image contained in the baseDir should be the original tile set images before
     * being processed by TiledMapPacker (the ones actually read by Tiled).
     * @param props the properties of the tileset to process
     * @param baseDir the directory in which the tile set image is stored */
    protected TileSetLayout (MapProperties props, FileHandle baseDir) throws IOException {
        int tileWidth = props.get("tilewidth", Integer.class);
        int tileHeight = props.get("tileheight", Integer.class);
        int margin = props.get("margin", Integer.class);
        int spacing = props.get("spacing", Integer.class);

        this.firstgid = props.get("firstgid", Integer.class);

        image = ImageIO.read(baseDir.child(props.get("imagesource", String.class)).read());

        imageTilePositions = new IntMap<Vector2>();

        // fill the tile regions
        int x, y, tile = 0;
        numRows = 0;
        numCols = 0;

        int stopWidth = image.getWidth() - tileWidth;
        int stopHeight = image.getHeight() - tileHeight;

        for (y = margin; y <= stopHeight; y += tileHeight + spacing) {
            for (x = margin; x <= stopWidth; x += tileWidth + spacing) {
                if (y == margin) numCols++;
                imageTilePositions.put(tile, new Vector2(x, y));
                tile++;
            }
            numRows++;
        }

        numTiles = numRows * numCols;
    }

    public int getNumRows () {
        return numRows;
    }

    public int getNumCols () {
        return numCols;
    }

    /** Returns the location of the tile in {@link TileSetLayout#image} */
    public Vector2 getLocation (int tile) {
        return imageTilePositions.get(tile - firstgid);
    }
}
