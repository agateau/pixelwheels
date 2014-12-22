package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * A generic overlay display
 */
public abstract class Overlay extends WidgetGroup {
    protected static final float IN_DURATION = 0.5f;
    protected final Actor mContent;

    public Overlay(TextureRegion dot) {
        setFillParent(true);
        Image bg = new Image(dot);
        bg.setColor(0, 0, 0, 0);
        bg.setFillParent(true);
        addActor(bg);
        bg.addAction(Actions.alpha(0.6f, IN_DURATION));

        mContent = createContent();
    }

    protected abstract Actor createContent();

    @Override
    public void layout() {
        super.layout();
        if (mContent.getParent() == null) {
            mContent.setSize(this.getWidth(), this.getHeight());
            mContent.setPosition(0, this.getHeight());
            mContent.addAction(Actions.moveTo(0, 0, IN_DURATION, Interpolation.swingOut));
            addActor(mContent);
        }
    }
}
