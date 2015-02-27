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
import com.greenyetilab.race.LapPositionTable;
import com.greenyetilab.utils.Assert;
import com.greenyetilab.utils.log.NLog;

/**
 * Create a lap position table from a .tmx file
 *
 * The .tmx file must contains a "Zones" object layer with concave quadrilaterals
 */
public class LapPositionTableGenerator {
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

        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
        int width = layer.getWidth() * ((int)layer.getTileWidth());
        int height = layer.getHeight() * ((int)layer.getTileHeight());
        LapPositionTable table = new LapPositionTable(width, height);

        MapLayer zonesLayer = map.getLayers().get("Zones");
        Assert.check(zonesLayer != null, "No 'Zones' layer found");

        for (MapObject obj : zonesLayer.getObjects()) {
            int section = Integer.parseInt(obj.getName());
            NLog.i("Section %d", section);
            Assert.check(obj instanceof PolygonMapObject, "'Zones' layer should only contain PolygonMapObjects");
            Polygon polygon = ((PolygonMapObject)obj).getPolygon();
            table.addZone(section, polygon);
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
                int pos = table.get(x, y);
                pixmap.drawPixel(x, height - 1 - y, pos);
            }
        }
        PixmapIO.writePNG(tableFile, pixmap);
    }
}
