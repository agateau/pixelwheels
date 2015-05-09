package com.greenyetilab.tinywheels;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.log.NLog;

/**
 * Combines several maps into a single long map
 *
 * Current limits:
 * - Maps must use the same tilesets
 * - Maps must contain the same number of layers, with the same names
 * - Object layers must only contain RectangleMapObjects
 * - There must be one tile layer named "Centers" with a tile at the top center and a tile at
 *   the bottom center
 *
 */
public class MapCreator {
    private static class MapChunk {
        public final TiledMap map;
        public final int width;
        public final int height;
        public final int bottomX;
        public final int topX;

        public MapChunk(TiledMap map) {
            this.map = map;
            TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
            this.width = layer.getWidth();
            this.height = layer.getHeight();

            TiledMapTileLayer centersLayer = (TiledMapTileLayer)map.getLayers().get("Centers");
            assert centersLayer != null;
            this.bottomX = findX(centersLayer, 0);
            this.topX = findX(centersLayer, this.height - 1);
        }

        private int findX(TiledMapTileLayer layer, int ty) {
            for (int x = 0; x < this.width; ++x) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, ty);
                if (cell != null) {
                    return x;
                }
            }
            return this.width / 2;
        }
    }
    Array<MapChunk> mMapChunks = new Array<MapChunk>();

    public void addSourceMap(TiledMap map) {
        mMapChunks.add(new MapChunk(map));
    }

    /**
     * Generate a map made of mapLength map chunks
     *
     * @return a new TiledMap
     */
    public TiledMap run(int mapLength) {
        TiledMap referenceMap = mMapChunks.get(0).map;
        int layerCount = referenceMap.getLayers().getCount();

        Array<MapChunk> mapSequence = new Array<MapChunk>();
        float tileWidth, tileHeight;
        {
            TiledMapTileLayer layer = (TiledMapTileLayer) referenceMap.getLayers().get(0);
            tileWidth = layer.getTileWidth();
            tileHeight = layer.getTileHeight();
        }

        // Compute map size
        int mapWidth;
        int mapHeight = 0;
        int centerTX = 0;
        {
            int left = 0;
            int right = 0;
            for (int i = 0; i < mapLength; ++i) {
                int mapIndex;
                if (i == 0) {
                    // Always start with a straight road
                    mapIndex = 0;
                } else {
                    mapIndex = MathUtils.random(mMapChunks.size - 1);
                }
                MapChunk chunk = mMapChunks.get(mapIndex);
                int chunkLeft = centerTX - chunk.bottomX;
                int chunkRight = chunkLeft + chunk.width;
                left = Math.min(chunkLeft, left);
                right = Math.max(chunkRight, right);
                centerTX += chunk.topX - chunk.bottomX;
                mapHeight += chunk.height;
                mapSequence.add(chunk);
            }
            mapWidth = right - left;

            // position centerTX so that the map fits in the boundaries
            centerTX = -left;
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
        for (MapChunk chunk : mapSequence) {
            int startTX = centerTX - chunk.bottomX;
            for (int layerIdx = 0; layerIdx < layerCount; ++layerIdx) {
                MapLayer srcLayer = chunk.map.getLayers().get(layerIdx);
                MapLayer dstLayer = dstMap.getLayers().get(layerIdx);
                if (srcLayer instanceof TiledMapTileLayer) {
                    TiledMapTileLayer tsrcLayer = (TiledMapTileLayer) srcLayer;
                    TiledMapTileLayer tdstLayer = (TiledMapTileLayer) dstLayer;
                    copyTileLayer(dstMap, tdstLayer, tsrcLayer, startTX, startTY);
                    if (layerIdx == 0) {
                        copyColumns(dstMap, tdstLayer, 0, startTX, startTY, tsrcLayer, 0);
                        copyColumns(dstMap, tdstLayer, startTX + chunk.width, mapWidth, startTY, tsrcLayer, chunk.width - 1);
                    }
                } else {
                    copyLayer(dstLayer, srcLayer, startTX * tileWidth, startTY * tileHeight);
                }
            }
            centerTX += chunk.topX - chunk.bottomX;
            startTY += chunk.height;
        }
        return dstMap;
    }

    private static void copyTileLayer(TiledMap dstMap, TiledMapTileLayer dstLayer, TiledMapTileLayer srcLayer, int startTX, int startTY) {
        TiledMapTileSets dstTileSets = dstMap.getTileSets();
        for (int ty = 0; ty < srcLayer.getHeight(); ++ty) {
            for (int tx = 0; tx < srcLayer.getWidth(); ++tx) {
                TiledMapTileLayer.Cell srcCell = srcLayer.getCell(tx, ty);
                if (srcCell == null) {
                    continue;
                }
                TiledMapTileLayer.Cell dstCell = copyCell(dstTileSets, srcCell);
                dstLayer.setCell(startTX + tx, startTY + ty, dstCell);
            }
        }
    }

    private static TiledMapTileLayer.Cell copyCell(TiledMapTileSets tileSets, TiledMapTileLayer.Cell srcCell) {
        int tileId = srcCell.getTile().getId();
        TiledMapTileLayer.Cell dstCell = new TiledMapTileLayer.Cell();
        dstCell.setTile(tileSets.getTile(tileId));
        dstCell.setFlipHorizontally(srcCell.getFlipHorizontally());
        dstCell.setFlipVertically(srcCell.getFlipVertically());
        dstCell.setRotation(srcCell.getRotation());
        return dstCell;
    }

    private static void copyColumns(TiledMap dstMap, TiledMapTileLayer dstLayer, int startTX, int endTX, int startTY, TiledMapTileLayer srcLayer, int srcTX) {
        TiledMapTileSets dstTileSets = dstMap.getTileSets();
        if (startTX >= endTX) {
            return;
        }
        for (int ty = 0; ty < srcLayer.getHeight(); ++ty) {
            TiledMapTileLayer.Cell srcCell = srcLayer.getCell(srcTX, ty);
            if (srcCell == null) {
                continue;
            }
            int tileId = srcCell.getTile().getId();
            TiledMapTileLayer.Cell dstCell = copyCell(dstTileSets, srcCell);
            for (int dstTX = startTX; dstTX < endTX; ++dstTX) {
                dstLayer.setCell(dstTX, startTY + ty, dstCell);
            }
        }
    }

    private static void copyLayer(MapLayer dstLayer, MapLayer srcLayer, float startX, float startY) {
        for (MapObject srcObject : srcLayer.getObjects()) {
            MapObject dstObject = null;
            if (srcObject instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) srcObject).getRectangle();
                dstObject = new RectangleMapObject(startX + rect.x, startY + rect.y, rect.width, rect.height);
            } else if (srcObject instanceof PolygonMapObject) {
                Polygon srcPolygon = ((PolygonMapObject) srcObject).getPolygon();
                Polygon dstPolygon = new Polygon(srcPolygon.getTransformedVertices());
                dstPolygon.translate(startX, startY);
                dstObject = new PolygonMapObject(dstPolygon.getTransformedVertices());
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
