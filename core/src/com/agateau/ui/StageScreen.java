/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

/** A screen with a stage covering it */
public abstract class StageScreen extends ScreenAdapter {
    private final Stage mStage;
    private final Viewport mViewport;

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
        if (isBackKeyPressed()) {
            onBackPressed();
        }
        mStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mViewport.update(width, height, true);
    }

    /**
     * This method is called when the player wants to go back to the previous screen.
     *
     * <p>It is called automatically for global "back" shortcuts, but class users can call it
     * themselves for example from the ClickListener of a Back button
     */
    public abstract void onBackPressed();

    /** Must return true if the user pressed a key or activated a control to go back */
    public abstract boolean isBackKeyPressed();
}
