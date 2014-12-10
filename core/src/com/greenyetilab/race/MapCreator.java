package com.greenyetilab.race;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.log.NLog;

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
     * - Object layers must only contain RectangleMapObjects
     *
     * @return a new TiledMap
     */
    public TiledMap run(int mapLength) {
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

        for (int i = 0; i < mapLength; ++i) {
            int mapIndex = MathUtils.random(mMaps.size - 1);
            TiledMap map = mMaps.get(mapIndex);
            layerCount = Math.max(map.getLayers().getCount(), layerCount);
            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
            mapWidth = Math.max(layer.getWidth(), mapWidth);
            mapHeight += layer.getHeight();
            mapSequence.add(map);
        }

        TiledMap dstMap = new TiledMap();

        // Copy tilesets
        for (TiledMapTileSet set : referenceMap.getTileSets()) {
            dstMap.getTileSets().addTileSet(set);
        }

        // Create layers
        MapLayers layers = dstMap.getLayers();
        for (int layerIdx = 0; layerIdx < layerCount; ++layerIdx) {
            MapLayer referenceLayer = referenceMap.getLayers().get(layerIdx);
            boolean isTileLayer = referenceLayer instanceof TiledMapTileLayer;
            MapLayer dstLayer;
            if (isTileLayer) {
                dstLayer = new TiledMapTileLayer(mapWidth, mapHeight, (int) tileWidth, (int) tileHeight);
            } else {
                dstLayer = new MapLayer();
            }
            dstLayer.setName(referenceLayer.getName());
            layers.add(dstLayer);
        }

        // Fill layers
        int startTY = 0;
        for (TiledMap srcMap : mapSequence) {
            for (int layerIdx = 0; layerIdx < layerCount; ++layerIdx) {
                MapLayer srcLayer = srcMap.getLayers().get(layerIdx);
                MapLayer dstLayer = dstMap.getLayers().get(layerIdx);
                if (srcLayer instanceof TiledMapTileLayer) {
                    copyTileLayer(dstMap, (TiledMapTileLayer) dstLayer, (TiledMapTileLayer) srcLayer, startTY);
                } else {
                    copyLayer(dstLayer, srcLayer, startTY * tileHeight);
                }
            }
            startTY += ((TiledMapTileLayer)srcMap.getLayers().get(0)).getHeight();
        }
        return dstMap;
    }

    private static void copyTileLayer(TiledMap dstMap, TiledMapTileLayer dstLayer, TiledMapTileLayer srcLayer, int startTY) {
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
                dstLayer.setCell(tx, startTY + ty, dstCell);
            }
        }
    }

    private static void copyLayer(MapLayer dstLayer, MapLayer srcLayer, float startY) {
        for (MapObject srcObject : srcLayer.getObjects()) {
            MapObject dstObject = null;
            if (srcObject instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) srcObject).getRectangle();
                dstObject = new RectangleMapObject(rect.x, startY + rect.y, rect.width, rect.height);
            } else {
                NLog.e("Map objects of type %s are not supported yet", srcObject.getClass());
            }
            if (dstObject != null) {
                dstObject.setName(srcObject.getName());
                dstObject.setColor(srcObject.getColor());
                dstObject.setVisible(srcObject.isVisible());
                dstObject.setOpacity(srcObject.getOpacity());
                dstObject.getProperties().putAll(srcObject.getProperties());
                dstLayer.getObjects().add(dstObject);
            }
        }
    }
}
