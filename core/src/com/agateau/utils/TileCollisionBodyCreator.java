package com.agateau.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;
import java.util.Map;

/**
 * Create static bodies from the collision objects of a tile map
 */
public class TileCollisionBodyCreator {
    private Map<Integer, TilePolygons> mMap = new HashMap<Integer, TilePolygons>();

    public static TileCollisionBodyCreator fromFileHandle(FileHandle handle) {
        XmlReader.Element root = FileUtils.parseXml(handle);
        return fromXml(root);
    }

    public static TileCollisionBodyCreator fromXml(XmlReader.Element root) {
        TileCollisionBodyCreator creator = new TileCollisionBodyCreator();
        for (XmlReader.Element tileSetElement : root.getChildrenByName("tileset")) {
            int firstGid = tileSetElement.getIntAttribute("firstgid");
            int tileWidth = tileSetElement.getIntAttribute("tilewidth");
            int tileHeight = tileSetElement.getIntAttribute("tileheight");
            for (XmlReader.Element tileElement : tileSetElement.getChildrenByName("tile")) {
                TilePolygons polygons = TilePolygons.fromXml(tileElement, tileWidth, tileHeight);
                if (polygons != null) {
                    Integer gid = firstGid + tileElement.getIntAttribute("id");
                    creator.mMap.put(gid, polygons);
                }
            }
        }
        return creator;
    }

    public void createCollisionBodies(World world, float scale, TiledMapTileLayer layer) {
        final float tileWidth = scale * layer.getTileWidth();
        final float tileHeight = scale * layer.getTileHeight();
        for (int ty = 0; ty < layer.getHeight(); ++ty) {
            for (int tx = 0; tx < layer.getWidth(); ++tx) {
                TiledMapTileLayer.Cell cell = layer.getCell(tx, ty);
                if (cell == null) {
                    continue;
                }
                createBodyForCell(world, cell, tx * tileWidth, ty * tileHeight, tileWidth, tileHeight);
            }
        }
    }

    private void createBodyForCell(World world, TiledMapTileLayer.Cell cell, float x, float y, float width, float height) {
        int id = cell.getTile().getId();
        TilePolygons polygons = mMap.get(id);
        if (polygons == null) {
            return;
        }
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);

        polygons.createBodyShapes(body, width, height, cell.getRotation() * 90);
    }
}
