package com.greenyetilab.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.greenyetilab.utils.log.NLog;

public abstract class RefreshHelper {
    public RefreshHelper(Stage stage) {
        installEventListener(stage);
    }

    public RefreshHelper(Group group) {
        Actor helperActor = new Actor() {
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
        stage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.F5) {
                    NLog.i("Refreshing");
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                refresh();
                            } catch (Exception exc) {
                                NLog.e("Refresh failed: %s", exc);
                            }
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Implementation of this method must do the refresh
     */
    protected abstract void refresh();
}
