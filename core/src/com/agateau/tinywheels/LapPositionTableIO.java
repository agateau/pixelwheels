/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.tinywheels;

import com.agateau.utils.Assert;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.utils.Array;

import java.util.HashSet;
import java.util.Set;

/**
 * Loads a LapPositionTable from a TiledMap. Parses the section segments defined in
 * doc/map-format.md
 */
public class LapPositionTableIO {
    private static class Line implements Comparable {
        float x1, y1;
        float x2, y2;
        float order;

        @Override
        public int compareTo(Object o) {
            return Float.compare(order, ((Line)o).order);
        }
    }

    public static LapPositionTable load(TiledMap map) {
        MapLayer layer = map.getLayers().get("Sections");
        Assert.check(layer != null, "No 'Sections' layer found");
        MapObjects objects = layer.getObjects();

        Array<Line> lines = new Array<Line>();
        lines.ensureCapacity(objects.getCount());
        Set<String> names = new HashSet<String>();
        for (MapObject obj : objects) {
            String name = obj.getName();
            Assert.check(!name.isEmpty(), "Section line is missing a name");
            Assert.check(!names.contains(name), "Duplicate section line " + name);
            names.add(name);

            float order;
            try {
                order = Float.parseFloat(name);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid section name " + name);
            }
            Assert.check(obj instanceof PolylineMapObject, "'Sections' layer should only contain PolylineMapObjects");
            Polyline polyline = ((PolylineMapObject)obj).getPolyline();
            float[] vertices = polyline.getTransformedVertices();
            Assert.check(vertices.length == 4, "Polyline with name " + order + "should have 2 points, not " + (vertices.length / 2));
            Line line = new Line();
            line.x1 = vertices[0];
            line.y1 = vertices[1];
            line.x2 = vertices[2];
            line.y2 = vertices[3];
            line.order = order;
            lines.add(line);
        }
        lines.sort();

        LapPositionTable table = new LapPositionTable();
        for (int idx = 0; idx < lines.size; ++idx) {
            Line line1 = lines.get(idx);
            Line line2 = lines.get((idx + 1) % lines.size);
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
