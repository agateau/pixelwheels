package com.greenyetilab.tinywheels.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.greenyetilab.tinywheels.LapPositionTable;
import com.greenyetilab.tinywheels.LapPositionTableIO;

/**
 * Load a .tmx file and save its corresponding lap position table as a PNG file
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
        LapPositionTable table = LapPositionTableIO.load(map);

        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
        int width = layer.getWidth() * ((int)layer.getTileWidth());
        int height = layer.getHeight() * ((int)layer.getTileHeight());

        Pixmap pixmap = LapPositionTableIO.createPixmap(table, width, height);
        PixmapIO.writePNG(tableFile, pixmap);
    }
}
