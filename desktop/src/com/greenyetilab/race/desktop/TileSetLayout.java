package com.greenyetilab.race.desktop;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
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
     * being processed by {@link TiledMapPacker} (the ones actually read by Tiled).
     * @param tileset the tile set to process
     * @param baseDir the directory in which the tile set image is stored */
    protected TileSetLayout (int firstgid, TiledMapTileSet tileset, FileHandle baseDir) throws IOException {
        int tileWidth = tileset.getProperties().get("tilewidth", Integer.class);
        int tileHeight = tileset.getProperties().get("tileheight", Integer.class);
        int margin = tileset.getProperties().get("margin", Integer.class);
        int spacing = tileset.getProperties().get("spacing", Integer.class);

        this.firstgid = firstgid;

        image = ImageIO.read(baseDir.child(tileset.getProperties().get("imagesource", String.class)).read());

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
