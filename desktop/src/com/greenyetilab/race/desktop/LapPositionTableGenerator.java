package com.greenyetilab.race.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.race.LapPosition;
import com.greenyetilab.race.LapPositionTable;
import com.greenyetilab.utils.Assert;
import com.greenyetilab.utils.log.NLog;

/**
 * Create a lap position table from a .tmx file
 *
 * The .tmx file must contains a "Zones" object layer with concave quadrilaterals
 */
public class LapPositionTableGenerator {
    private static class LapZone {
        private int mSection;
        private Polygon mPolygon;
        private final Warper mWarper = new Warper();

        public LapZone(int section, Polygon polygon) {
            mSection = section;
            mPolygon = polygon;
            float[] vertices = mPolygon.getTransformedVertices();
            int verticeCount = vertices.length / 2;
            Assert.check(verticeCount == 4, "Polygon " + section + " must have 4 vertices, not " + verticeCount);
            mWarper.setSource(
                    vertices[0], vertices[1],
                    vertices[2], vertices[3],
                    vertices[4], vertices[5],
                    vertices[6], vertices[7]
            );
            mWarper.setDestination(
                    0, 0,
                    1, 0,
                    1, 1,
                    0, 1
            );
        }

        public LapPosition computePosition(float x, float y) {
            Vector2 out = mWarper.warp(x, y);
            return new LapPosition(mSection, out.x);
        }
    }

    public static void main(String[] args) {
        new CommandLineApplication("LapPositionTableGenerator", args) {
            @Override
            int run(String[] arguments) {
                FileHandle tmxFile = Gdx.files.absolute(arguments[0]);
                FileHandle tableFile = Gdx.files.absolute(arguments[1]);
                LapPositionTableGenerator.generateTable(tmxFile, tableFile);
                return 0;
            }
        };
    }

    public static void generateTable(FileHandle tmxFile, FileHandle tableFile) {
        TiledMap map = new TmxMapLoader().load(tmxFile.path());
        Array<LapZone> zones = loadLapZones(map);

        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
        int width = layer.getWidth() * ((int)layer.getTileWidth());
        int height = layer.getHeight() * ((int)layer.getTileHeight());
        LapPositionTable table = new LapPositionTable(width, height);
        for (int y = 0; y < height; ++y) {
            NLog.i("Analyzing %d/%d", y, height);
            for (int x = 0; x < width; ++x) {
                LapZone zone = findLapZone(zones, x, y);
                if (zone != null) {
                    LapPosition position = zone.computePosition(x, y);
                    table.set(x, y, position);
                }
            }
        }

        saveTable(table, tableFile);
    }

    private static void saveTable(LapPositionTable table, FileHandle tableFile) {
        NLog.i("Saving");
        int width = table.getWidth();
        int height = table.getHeight();
        Pixmap pixmap = new Pixmap(table.getWidth(), table.getHeight(), Pixmap.Format.RGBA8888);
        for (int y = 0; y < height; ++y) {
            NLog.i("Saving %d/%d", y, height);
            for (int x = 0; x < width; ++x) {
                LapPosition pos = table.get(x, y);
                int color;
                if (pos == null) {
                    color = 0;
                } else {
                    color = ((int)(pos.distance * 255) << 8) + 255;
                }
                pixmap.drawPixel(x, y, color);
            }
        }
        PixmapIO.writePNG(tableFile, pixmap);
    }

    private static Array<LapZone> loadLapZones(TiledMap map) {
        MapLayer layer = map.getLayers().get("Zones");
        Assert.check(layer != null, "No 'Zones' layer found");

        Array<LapZone> zones = new Array<LapZone>();
        for (MapObject obj : layer.getObjects()) {
            int section = Integer.parseInt(obj.getName());
            NLog.i("Section %d", section);
            Assert.check(obj instanceof PolygonMapObject, "'Zones' layer should only contain PolygonMapObjects");
            Polygon polygon = ((PolygonMapObject)obj).getPolygon();
            zones.add(new LapZone(section, polygon));

        }
        return zones;
    }

    private static LapZone findLapZone(Array<LapZone> zones, int x, int y) {
        for (LapZone zone : zones) {
            if (zone.mPolygon.contains(x, y)) {
                return zone;
            }
        }
        return null;
    }
}
