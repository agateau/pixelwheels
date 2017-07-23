package com.agateau.tinywheels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * A generic overlay display
 */
public class Overlay extends WidgetGroup {
    protected static final float IN_DURATION = 0.5f;
    private Actor mContent;

    public Overlay(TextureRegion dot) {
        setFillParent(true);
        Image bg = new Image(dot);
        bg.setColor(0, 0, 0, 0);
        bg.setFillParent(true);
        addActor(bg);
        bg.addAction(Actions.alpha(0.6f, IN_DURATION));
    }

    public void setContent(Actor actor) {
        if (mContent != null && mContent.getParent() != null) {
            mContent.getParent().removeActor(mContent);
        }
        mContent = actor;
    }

    @Override
    public void layout() {
        super.layout();
        if (mContent != null && mContent.getParent() == null) {
            mContent.setSize(this.getWidth(), this.getHeight());
            mContent.setPosition(0, this.getHeight());
            mContent.addAction(Actions.moveTo(0, 0, IN_DURATION, Interpolation.swingOut));
            addActor(mContent);
        }
    }
}
