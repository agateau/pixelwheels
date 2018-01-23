package com.agateau.tinywheels.motorlab;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class MotorLab extends Game {
    @Override
    public void create() {
        setScreen(new MotorLabScreen());
    }

    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 600;
        config.title = "UI Gallery";
        new LwjglApplication(new MotorLab(), config);
    }
}
