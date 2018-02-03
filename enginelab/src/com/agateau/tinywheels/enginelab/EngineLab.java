package com.agateau.tinywheels.enginelab;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class EngineLab extends Game {
    @Override
    public void create() {
        setScreen(new EngineLabScreen());
    }

    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 600;
        config.title = "Engine Lab";
        new LwjglApplication(new EngineLab(), config);
    }
}
