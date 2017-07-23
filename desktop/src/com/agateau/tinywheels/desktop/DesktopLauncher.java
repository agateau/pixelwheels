package com.agateau.tinywheels.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.agateau.tinywheels.TwGame;
import com.agateau.utils.FileUtils;

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
