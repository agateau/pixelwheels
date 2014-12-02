package com.greenyetilab.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the polygons of a tile
 */
public class TilePolygons {
    private final Array<float[]> mPolygons = new Array<float[]>();

    public static Map<Integer, TilePolygons> readTiledMap(FileHandle handle) {
        HashMap<Integer, TilePolygons> map = new HashMap<Integer, TilePolygons>();
        XmlReader.Element root = FileUtils.parseXml(handle);
        for (XmlReader.Element tileSetElement : root.getChildrenByName("tileset")) {
            int firstGid = tileSetElement.getIntAttribute("firstgid");
            int tileWidth = tileSetElement.getIntAttribute("tilewidth");
            int tileHeight = tileSetElement.getIntAttribute("tileheight");
            for (XmlReader.Element tileElement : tileSetElement.getChildrenByName("tile")) {
                TilePolygons polygons = fromXml(tileElement, tileWidth, tileHeight);
                if (polygons != null) {
                    Integer gid = firstGid + tileElement.getIntAttribute("id");
                    map.put(gid, polygons);
                }
            }
        }
        return map;
    }

    public static TilePolygons fromXml(XmlReader.Element element, int tileWidth, int tileHeight) {
        /*
        <objectgroup draworder="index">
          <object x="0" y="63.6364">
            <polygon points="0,0 64,-63.6364 -0.181818,-63.4545"/>
          </object>
        </objectgroup>
        */
        TilePolygons polygons = null;
        for (XmlReader.Element objectGroupElement : element.getChildrenByName("objectgroup")) {
            XmlReader.Element objectElement = objectGroupElement.getChildByName("object");
            if (polygons == null) {
                polygons = new TilePolygons();
            }
            float origX = objectElement.getFloat("x");
            float origY = objectElement.getFloat("y");
            String polygonString = objectElement.getChildByName("polygon").getAttribute("points");
            String[] coords = polygonString.split(" ");
            float[] vertices = new float[coords.length * 2];
            for (int idx = 0; idx < coords.length; ++idx) {
                String coord = coords[idx];
                int commaPos = coord.indexOf(',');
                float x = Float.valueOf(coord.substring(0, commaPos));
                float y = Float.valueOf(coord.substring(commaPos + 1));
                vertices[idx * 2] = (origX + x) / tileWidth;
                vertices[idx * 2 + 1] = 1 - (origY + y) / tileHeight;
            }
            polygons.mPolygons.add(vertices);
        }
        return polygons;
    }

    public void createBodyShapes(Body body, float width, float height) {
        for (float[] vertices : mPolygons) {
            PolygonShape shape = createPolygonShape(vertices, width, height);
            body.createFixture(shape, 1);
        }
    }

    public static PolygonShape createPolygonShape(float[] vertices, float width, float height) {
        PolygonShape shape = new PolygonShape();
        float[] scaledVertices = vertices.clone();
        scaleVertices(scaledVertices, width, height);
        shape.set(scaledVertices);
        return shape;
    }

    private static void scaleVertices(float[] vertices, float scaleX, float scaleY) {
        for (int i = 0; i < vertices.length; i += 2) {
            vertices[i] *= scaleX;
            vertices[i + 1] *= scaleY;
        }
    }
}
