package com.agateau.tinywheels;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.agateau.utils.Assert;

/**
 * Load a LapPositionTable from a TiledMap. The map must contain a "Zones" layer which must contain
 * convex quadrilaterals named "0".."n"
 *
 */
public class LapPositionTableIO {
    private static class Line {
        float x1, y1;
        float x2, y2;

        void reverse() {
            float tmp = x1;
            x1 = x2;
            x2 = tmp;
            tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
    }

    public static LapPositionTable load(TiledMap map) {
        MapLayer layer = map.getLayers().get("Sections");
        Assert.check(layer != null, "No 'Sections' layer found");
        MapObjects objects = layer.getObjects();

        Line[] lines = new Line[objects.getCount()];
        for (MapObject obj : objects) {
            int section = Integer.parseInt(obj.getName());
            Assert.check(section >= 0 && section < lines.length, "Invalid ID " + section);
            Assert.check(obj instanceof PolylineMapObject, "'Sections' layer should only contain PolylineMapObjects");
            Polyline polyline = ((PolylineMapObject)obj).getPolyline();
            float[] vertices = polyline.getTransformedVertices();
            Assert.check(vertices.length == 4, "Polyline with ID " + section + "should have 2 points, not " + (vertices.length / 2));
            Line line = new Line();
            line.x1 = vertices[0];
            line.y1 = vertices[1];
            line.x2 = vertices[2];
            line.y2 = vertices[3];
            Assert.check(lines[section] == null, "Duplicate ID " + section);
            lines[section] = line;
        }

        LapPositionTable table = new LapPositionTable();
        for (int idx = 0; idx < lines.length; ++idx) {
            Line line1 = lines[idx];
            Line line2 = lines[(idx + 1) % lines.length];
            Assert.check(line2 != null, "'Sections' layer is missing a line with ID " + idx);
            float[] vertices = {
                line1.x1, line1.y1,
                line2.x1, line2.y1,
                line2.x2, line2.y2,
                line1.x2, line1.y2
            };
            Polygon polygon = new Polygon(vertices);
            table.addSection(idx, polygon);
        }
        return table;
    }

    public static Pixmap createPixmap(LapPositionTable table, int width, int height) {
        NLog.i("Saving");
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        for (int y = 0; y < height; ++y) {
            NLog.i("Saving %d/%d", y, height);
            for (int x = 0; x < width; ++x) {
                LapPosition pos = table.get(x, y);
                int color;
                if (pos == null) {
                    color = 0;
                } else {
                    int r = (int)((1 - Math.abs(pos.getCenterDistance())) * 255);
                    int g = pos.getSectionId() * 255 / table.getSectionCount();
                    int b = (int)(pos.getSectionDistance() * 255);
                    color = (r << 24) | (g << 16) | (b << 8) | 0xff;
                }
                pixmap.drawPixel(x, height - 1 - y, color);
            }
        }
        return pixmap;
    }
}
