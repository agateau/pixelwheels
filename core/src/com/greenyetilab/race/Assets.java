package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by aurelien on 21/11/14.
 */
public class Assets {
    public final Skin skin;
    public final Texture car;
    public final Texture wheel;

    Assets() {
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.car = new Texture("car.png");
        this.wheel = new Texture("wheel.png");
    }
}
