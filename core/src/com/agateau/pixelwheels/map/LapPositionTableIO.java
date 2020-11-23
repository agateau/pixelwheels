/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.pixelwheels.map;

import com.agateau.utils.AgcMathUtils;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads a LapPositionTable from a TiledMap. Parses the section segments defined in
 * docs/map-format.md
 */
public class LapPositionTableIO {
    private static class Line implements Comparable {
        final Vector2 p1 = new Vector2();
        final Vector2 p2 = new Vector2();
        float order;

        @Override
        public int compareTo(Object o) {
            return Float.compare(order, ((Line) o).order);
        }

        public void swapPoints() {
            float x = p1.x;
            float y = p1.y;
            p1.set(p2);
            p2.set(x, y);
        }
    }

    public static LapPositionTable load(TiledMap map) {
        MapLayer layer = map.getLayers().get("Sections");
        Assert.check(layer != null, "No 'Sections' layer found");
        MapObjects objects = layer.getObjects();

        Array<Line> lines = new Array<>();
        lines.ensureCapacity(objects.getCount());
        Set<String> names = new HashSet<>();
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
            Assert.check(
                    obj instanceof PolylineMapObject,
                    "Object " + name + " in 'Sections' layer must be a PolylineMapObject");
            Polyline polyline = ((PolylineMapObject) obj).getPolyline();
            float[] vertices = polyline.getTransformedVertices();
            Assert.check(
                    vertices.length == 4,
                    "Polyline "
                            + name
                            + " in 'Sections' layer should have 2 points, not "
                            + (vertices.length / 2));
            Line line = new Line();
            line.p1.set(vertices[0], vertices[1]);
            line.p2.set(vertices[2], vertices[3]);
            line.order = order;
            lines.add(line);
        }
        lines.sort();

        LapPositionTable table = new LapPositionTable();
        for (int idx = 0; idx < lines.size; ++idx) {
            Line line1 = lines.get(idx);
            Line line2 = lines.get((idx + 1) % lines.size);
            if (!AgcMathUtils.isQuadrilateralConvex(line1.p1, line2.p1, line2.p2, line1.p2)) {
                NLog.d(
                        "Quadrilateral formed by line %f and %f is concave, swapping points of line %f",
                        line1.order, line2.order, line2.order);
                line2.swapPoints();
                if (!AgcMathUtils.isQuadrilateralConvex(line1.p1, line2.p1, line2.p2, line1.p2)) {
                    throw new RuntimeException(
                            "Quadrilateral formed by line "
                                    + line1.order
                                    + " and "
                                    + line2.order
                                    + " is concave");
                }
            }
            float[] vertices = {
                line1.p1.x, line1.p1.y,
                line2.p1.x, line2.p1.y,
                line2.p2.x, line2.p2.y,
                line1.p2.x, line1.p2.y
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
                    int r = (int) ((1 - Math.abs(pos.getCenterDistance())) * 255);
                    int g = pos.getSectionId() * 255 / table.getSectionCount();
                    int b = (int) (pos.getSectionDistance() * 255);
                    color = (r << 24) | (g << 16) | (b << 8) | 0xff;
                }
                pixmap.drawPixel(x, height - 1 - y, color);
            }
        }
        return pixmap;
    }
}
