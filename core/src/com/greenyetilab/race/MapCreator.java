package com.greenyetilab.race;

import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.Array;

/**
 * Create a large tiled map from several maps
 */
public class MapCreator {
    Array<TiledMap> mMaps = new Array<TiledMap>();

    public void addSourceMap(TiledMap map) {
        mMaps.add(map);
    }

    /**
     * Combines several maps into a single long map
     *
     * Current limits:
     * - Maps must be the same width
     * - Maps must use the same tilesets
     * - Maps must contain the same number of layers, with the same names
     * - Maps must contain only tile layers
     *
     * @return a new TiledMap
     */
    public TiledMap run() {
        TiledMap referenceMap = mMaps.get(0);

        // Compute sizes
        Array<TiledMap> mapSequence = new Array<TiledMap>();
        int mapWidth = 0;
        int mapHeight = 0;
        int layerCount = 1;
        float tileWidth, tileHeight;
        {
            TiledMapTileLayer layer = (TiledMapTileLayer) referenceMap.getLayers().get(0);
            tileWidth = layer.getTileWidth();
            tileHeight = layer.getTileHeight();
        }

        for (int i = 0; i < 3; ++i) {
            TiledMap map = mMaps.get(0);
            layerCount = Math.max(map.getLayers().getCount(), layerCount);
            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
            mapWidth = Math.max(layer.getWidth(), mapWidth);
            mapHeight += layer.getHeight();
            mapSequence.add(mMaps.get(0));
        }

        TiledMap dstMap = new TiledMap();

        // Copy tilesets
        for (TiledMapTileSet set : referenceMap.getTileSets()) {
            dstMap.getTileSets().addTileSet(set);
        }

        // Create layers
        MapLayers layers = dstMap.getLayers();
        for (int layerIdx = 0; layerIdx < layerCount; ++layerIdx) {
            TiledMapTileLayer dstLayer = new TiledMapTileLayer(mapWidth, mapHeight, (int)tileWidth, (int)tileHeight);
            dstLayer.setName(referenceMap.getLayers().get(layerIdx).getName());
            layers.add(dstLayer);
            int startY = 0;
            for (TiledMap srcMap : mapSequence) {
                TiledMapTileLayer srcLayer = (TiledMapTileLayer)srcMap.getLayers().get(layerIdx);
                copyMapLayer(dstMap, dstLayer, srcLayer, startY);
                startY += srcLayer.getHeight();
            }
        }
        return dstMap;
    }

    private static void copyMapLayer(TiledMap dstMap, TiledMapTileLayer dstLayer, TiledMapTileLayer srcLayer, int startY) {
        TiledMapTileSets dstTileSets = dstMap.getTileSets();
        for (int ty = 0; ty < srcLayer.getHeight(); ++ty) {
            for (int tx = 0; tx < srcLayer.getWidth(); ++tx) {
                TiledMapTileLayer.Cell srcCell = srcLayer.getCell(tx, ty);
                if (srcCell == null) {
                    continue;
                }
                int tileId = srcCell.getTile().getId();
                TiledMapTileLayer.Cell dstCell = new TiledMapTileLayer.Cell();
                dstCell.setTile(dstTileSets.getTile(tileId));
                dstCell.setFlipHorizontally(srcCell.getFlipHorizontally());
                dstCell.setFlipVertically(srcCell.getFlipVertically());
                dstCell.setRotation(srcCell.getRotation());
                dstLayer.setCell(tx, startY + ty, dstCell);
            }
        }
    }
}
