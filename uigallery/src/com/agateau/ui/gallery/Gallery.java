package com.agateau.ui.gallery;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Gallery for custom ui classes
 */

public class Gallery extends Game {
    @Override
    public void create() {
        setScreen(new GalleryScreen());
    }

    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 480;
        config.title = "UI Gallery";
        new LwjglApplication(new Gallery(), config);
    }
}
