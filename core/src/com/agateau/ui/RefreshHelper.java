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

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class RefreshHelper {
    public RefreshHelper(Stage stage) {
        installEventListener(stage);
    }

    public RefreshHelper(Group group) {
        Actor helperActor =
                new Actor() {
                    @Override
                    public void setStage(Stage stage) {
                        super.setStage(stage);
                        installEventListener(stage);
                    }
                };
        group.addActor(helperActor);
    }

    private void installEventListener(Stage stage) {
        if (stage == null) {
            return;
        }
        stage.addListener(
                new InputListener() {
                    @Override
                    public boolean keyUp(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.F5) {
                            NLog.i("Refreshing");
                            Gdx.app.postRunnable(
                                    () -> {
                                        try {
                                            refreshAssets();
                                            refresh();
                                        } catch (Exception exc) {
                                            NLog.e("Refresh failed: %s", exc);
                                        }
                                    });
                            return true;
                        }
                        return false;
                    }
                });
    }

    protected void refreshAssets() {}

    /** Implementation of this method must do the refresh */
    protected abstract void refresh();
}
