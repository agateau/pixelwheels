package com.greenyetilab.utils;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

/**
 * Represents the polygons of a tile
 */
public class TilePolygons {
    private final Array<Polygon> mPolygons = new Array<Polygon>();

    public static TilePolygons fromXml(XmlReader.Element element, int tileWidth, int tileHeight) {
        /*
        For a polygon
        <objectgroup draworder="index">
          <object x="0" y="63.6364">
            <polygon points="0,0 64,-63.6364 -0.181818,-63.4545"/>
          </object>
        </objectgroup>
        For a rectangle
        <objectgroup draworder="index">
          <object x="0" y="0" width="64" height="64"/>
        </objectgroup>
        */
        TilePolygons polygons = null;
        for (XmlReader.Element objectGroupElement : element.getChildrenByName("objectgroup")) {
            XmlReader.Element objectElement = objectGroupElement.getChildByName("object");
            if (objectElement == null) {
                // Can happen if collision objects have been created, then removed
                continue;
            }
            if (polygons == null) {
                polygons = new TilePolygons();
            }
            float[] vertices;
            XmlReader.Element polygonElement = objectElement.getChildByName("polygon");
            if (polygonElement != null) {
                vertices = readPolygonVertices(objectElement, polygonElement);
            } else {
                vertices = readRectangleVertices(objectElement);
            }
            for (int idx = 0, n = vertices.length; idx < n; idx += 2) {
                vertices[idx] = vertices[idx] / tileWidth;
                vertices[idx + 1] = 1 - vertices[idx + 1] / tileHeight;
            }
            Polygon polygon = new Polygon(vertices);
            polygon.setOrigin(0.5f, 0.5f);
            polygons.mPolygons.add(polygon);
        }
        return polygons;
    }

    private static float[] readPolygonVertices(XmlReader.Element objectElement, XmlReader.Element polygonElement) {
        float origX = objectElement.getFloat("x");
        float origY = objectElement.getFloat("y");
        String polygonString = polygonElement.getAttribute("points");
        String[] coords = polygonString.split(" ");
        float[] vertices = new float[coords.length * 2];
        for (int idx = 0; idx < coords.length; ++idx) {
            String coord = coords[idx];
            int commaPos = coord.indexOf(',');
            float x = Float.valueOf(coord.substring(0, commaPos));
            float y = Float.valueOf(coord.substring(commaPos + 1));
            vertices[idx * 2] = origX + x;
            vertices[idx * 2 + 1] = origY + y;
        }
        return vertices;
    }

    private static float[] readRectangleVertices(XmlReader.Element objectElement) {
        float x = objectElement.getFloat("x");
        float y = objectElement.getFloat("y");
        float w = objectElement.getFloat("width");
        float h = objectElement.getFloat("height");
        return new float[]{
            x, y,
            x + w, y,
            x + w, y + h,
            x, y + h
        };
    }

    public void createBodyShapes(Body body, float width, float height, float rotation) {
        for (Polygon polygon : mPolygons) {
            polygon.setRotation(rotation);
            float[] vertices = polygon.getTransformedVertices().clone();
            for (int idx = 0; idx < vertices.length; idx += 2) {
                vertices[idx] *= width;
                vertices[idx + 1] *= height;
            }
            PolygonShape shape = new PolygonShape();
            shape.set(vertices);
            body.createFixture(shape, 1);
        }
    }
}
