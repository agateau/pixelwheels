package com.greenyetilab.tinywheels.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.greenyetilab.tinywheels.TwGame;
import com.greenyetilab.utils.FileUtils;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 480;
        config.title = "Tiny Wheels";
        FileUtils.appName = "tinywheels";
        new LwjglApplication(new TwGame(), config);
    }
}
