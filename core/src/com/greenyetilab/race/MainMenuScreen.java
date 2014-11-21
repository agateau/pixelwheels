package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by aurelien on 21/11/14.
 */
public class MainMenuScreen extends ScreenAdapter {
    private final RaceGame mGame;
    private Stage mStage;
    private Viewport mViewport;

    public MainMenuScreen(RaceGame game) {
        mGame = game;
        mViewport = new ScreenViewport();
        mStage = new Stage(mViewport);

        Skin skin = game.getAssets().skin;

        final TextButton button = new TextButton("Start", skin, "default");

        button.setWidth(200f);
        button.setHeight(20f);
        button.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 10f);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.start("race.tmx");
            }
        });

        mStage.addActor(button);
        Gdx.input.setInputProcessor(mStage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mStage.act(delta);
        mStage.draw();
    }
}
