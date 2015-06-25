package com.greenyetilab.tinywheels.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.greenyetilab.utils.log.NLog;

/**
 * Loads a .tmx file and creates a screenshot of it as a PNG file
 */
public class MapScreenshotGenerator {
    private static final int SHOT_SIZE = 220;

    public static void main(String[] args) {
        new CommandLineApplication("MapScreenshotGenerator", args) {
            @Override
            int run(String[] arguments) {
                if (arguments.length > 0) {
                    for (String name : arguments) {
                        processFile(name);
                    }
                } else {
                    FileHandle mapDir = Gdx.files.absolute(".");
                    for (FileHandle file : mapDir.list(".tmx")) {
                        processFile(file.path());
                    }
                }
                return 0;
            }
        };
    }

    private static void processFile(String tmxFileName) {
        String shotFileName = tmxFileName.replace(".tmx", "-screenshot.png");
        FileHandle tmxFile = Gdx.files.absolute(tmxFileName);
        FileHandle shotFile = Gdx.files.absolute(shotFileName);
        if (isOutdated(shotFile, tmxFile)) {
            NLog.i("%s: updating", shotFile.path());
            generateScreenshot(shotFile, tmxFile);
        } else {
            NLog.i("%s: up to date", shotFile.path());
        }
    }

    private static boolean isOutdated(FileHandle dst, FileHandle src) {
        return dst.lastModified() < src.lastModified();
    }

    public static void generateScreenshot(FileHandle shotFile, FileHandle tmxFile) {
        TiledMap map = new TmxMapLoader().load(tmxFile.path());
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
        int mapWidth = (int)(layer.getWidth() * layer.getTileWidth());
        int mapHeight = (int)(layer.getHeight() * layer.getTileHeight());
        float ratio = (float)SHOT_SIZE / Math.max(mapWidth, mapHeight);

        int shotWidth = (int)(mapWidth * ratio);
        int shotHeight = (int)(mapHeight * ratio);

        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, shotWidth, shotHeight, false /* hasDepth */);
        OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(map);

        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(true /* yDown */, mapWidth, mapHeight);
        renderer.setView(camera);

        fbo.begin();
        renderer.render();

        Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, shotWidth, shotHeight);
        PixmapIO.writePNG(shotFile, pixmap);
    }
}
