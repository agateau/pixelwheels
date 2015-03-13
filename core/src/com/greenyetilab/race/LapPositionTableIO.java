package com.greenyetilab.race;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.greenyetilab.utils.Assert;
import com.greenyetilab.utils.log.NLog;

/**
 * Load a LapPositionTable from a TiledMap. The map must contain a "Zones" layer which must contain
 * convex quadrilaterals named "0".."n"
 *
 */
public class LapPositionTableIO {
    public static LapPositionTable load(TiledMap map) {
        MapLayer zonesLayer = map.getLayers().get("Zones");
        Assert.check(zonesLayer != null, "No 'Zones' layer found");

        LapPositionTable table = new LapPositionTable();

        for (MapObject obj : zonesLayer.getObjects()) {
            int section = Integer.parseInt(obj.getName());
            Assert.check(obj instanceof PolygonMapObject, "'Zones' layer should only contain PolygonMapObjects");
            Polygon polygon = ((PolygonMapObject)obj).getPolygon();
            table.addSection(section, polygon);
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
                    int r = (int)((1 - Math.abs(pos.computeCenterDistance(x, y))) * 255);
                    int g = pos.sectionId * 255 / table.getSectionCount();
                    int b = (int)(pos.sectionDistance * 255);
                    color = (r << 24) | (g << 16) | (b << 8) | 0xff;
                }
                pixmap.drawPixel(x, height - 1 - y, color);
            }
        }
        return pixmap;
    }
}
