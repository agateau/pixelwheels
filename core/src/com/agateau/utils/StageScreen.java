package com.agateau.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A screen with a stage covering it
 */
public class StageScreen extends ScreenAdapter {
    private Stage mStage;
    private Viewport mViewport;

    public StageScreen(Viewport viewport) {
        mViewport = viewport;
        mStage = new Stage(mViewport);
    }

    public Stage getStage() {
        return mStage;
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(mStage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mStage.act(delta);
        mStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mViewport.update(width, height, true);
    }
}
