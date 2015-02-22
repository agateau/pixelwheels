package com.greenyetilab.race.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.greenyetilab.race.LapPositionTable;
import com.greenyetilab.utils.Assert;
import com.greenyetilab.utils.log.NLog;

/**
 * Create a lap position table from a .tmx file
 *
 * The .tmx file must contains a "Lap" object layer with a single polyline element
 */
public class LapPositionTableGenerator {
    private static class LapSection {
        public final Vector2 v1;
        public final Vector2 v2;
        public final Vector2 intersection = null;
        public final float length;

        public LapSection(Vector2 v1, Vector2 v2) {
            this.v1 = v1;
            this.v2 = v2;
            this.length = (float)Math.sqrt(Math.pow(v2.x - v1.x, 2) + Math.pow(v2.y - v1.y, 2));
        }

        public String toString() {
            return String.format("<v1=%s v2=%s i=%s l=%f>", v1, v2, intersection, length);
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
        Vector2[] vertices = loadLapVertices(map);

        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
        int width = layer.getWidth() * ((int)layer.getTileWidth());
        int height = layer.getHeight() * ((int)layer.getTileHeight());
        LapPositionTable table = new LapPositionTable(width, height);
        fillTable(table, vertices);

        saveTable(table, tableFile);
    }

    private static void fillTable(LapPositionTable table, Vector2[] vertices) {
        LapSection[] sections = new LapSection[vertices.length];
        for (int idx = 0; idx < sections.length; idx++) {
            sections[idx] = new LapSection(vertices[idx], vertices[(idx + 1) % sections.length]);
        }
    }

    private static void saveTable(LapPositionTable table, FileHandle tableFile) {
    }

    private static Vector2[] loadLapVertices(TiledMap map) {
        MapLayer layer = map.getLayers().get("Lap");
        Assert.check(layer != null, "No 'Lap' layer found");

        MapObject obj = layer.getObjects().get(0);
        Assert.check(obj instanceof PolylineMapObject, "'Lap' layer does not contain a PolylineMapObject");

        Polyline polyline = ((PolylineMapObject)obj).getPolyline();
        float[] coords = polyline.getTransformedVertices();
        Vector2[] vertices = new Vector2[coords.length / 2];
        for (int idx = 0; idx < vertices.length; ++idx) {
            vertices[idx] = new Vector2(coords[2 * idx], coords[2 * idx + 1]);
        }
        Assert.check(vertices.length > 1, "Need more than one vertice in 'Lap' polyline");
        return vertices;
    }
}
