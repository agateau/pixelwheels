package com.agateau.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

/**
 * Utility methods to help work with Scene2d
 */
public class Scene2dUtils {
    private static final float FAKE_TOUCH_DELAY = 0.1f;

    private static class Clicker implements Pool.Poolable, Runnable {
        private Actor mTarget;
        private final Vector2 mClickCoords = new Vector2();

        @Override
        public void reset() {
            mTarget = null;
        }

        public void click(Actor target) {
            mTarget = target;
            mClickCoords.set(target.getWidth() / 2, target.getHeight() / 2);
            target.localToStageCoordinates(mClickCoords);

            fireTouchEvent(mTarget, mClickCoords.x, mClickCoords.y, InputEvent.Type.touchDown);
            target.addAction(Actions.delay(FAKE_TOUCH_DELAY, Actions.run(this)));
        }

        @Override
        public void run() {
            fireTouchEvent(mTarget, mClickCoords.x, mClickCoords.y, InputEvent.Type.touchUp);
            Pools.free(this);
        }
    }

    public static void simulateClick(Actor target) {
        Clicker clicker = Pools.obtain(Clicker.class);
        clicker.click(target);
    }

    public static void fireTouchEvent(Actor target, float stageX, float stageY, InputEvent.Type type) {
        InputEvent event = Pools.obtain(InputEvent.class);
        event.setType(type);
        event.setStage(target.getStage());
        event.setStageX(stageX);
        event.setStageY(stageY);
        event.setPointer(0);
        event.setButton(0);
        target.fire(event);
        Pools.free(event);
    }
}
