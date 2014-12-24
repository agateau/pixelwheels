package com.greenyetilab.race;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * An actor which shows up when player does something good or bad
 */
public class ScoreIndicator extends Group implements Pool.Poolable {
    private static final float ANIMATION_DURATION = 0.5f;
    private static ReflectionPool<ScoreIndicator> sPool = new ReflectionPool<ScoreIndicator>(ScoreIndicator.class);

    private final StringBuilder mSB = new StringBuilder();
    private Label mLabel;

    public static Actor create(Assets assets, int delta, float posX, float posY) {
        ScoreIndicator obj = sPool.obtain();
        if (obj.mLabel == null) {
            obj.mLabel = new Label("", assets.skin, "default");
            obj.mLabel.setAlignment(Align.center);
            obj.addActor(obj.mLabel);
        }
        obj.mSB.setLength(0);
        if (delta > 0) {
            obj.mLabel.setColor(Color.GREEN);
            obj.mSB.append('+').append(delta);
        } else {
            obj.mLabel.setColor(Color.RED);
            obj.mSB.append(delta);
        }
        obj.mLabel.setPosition(posX, posY);
        obj.mLabel.setText(obj.mSB);
        obj.mLabel.addAction(Actions.sequence(
                Actions.parallel(
                        Actions.moveBy(0, 20),
                        Actions.fadeOut(ANIMATION_DURATION)
                ),
                Actions.removeActor()
        ));
        return obj;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setParent(Group parent) {
        super.setParent(parent);
        if (parent == null) {
            sPool.free(this);
        }
    }
}
