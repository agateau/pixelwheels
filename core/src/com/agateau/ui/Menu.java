package com.agateau.ui;

import com.agateau.utils.Assert;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;

/**
 * A keyboard and game controller friendly menu system
 */

public class Menu extends ScrollPane {
    private final NinePatch mSelectionNinePatch;
    private final Container mContainer;
    private final Skin mSkin;
    private int mCurrentIndex = -1;
    private final int PADDING = 8;

    private class Container extends VerticalGroup {
        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            Actor actor = getCurrentActor();
            if (actor != null) {
                drawSelection(batch, parentAlpha, actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
            }
        }
    }

    private final Array<MenuItem> mItems = new Array<MenuItem>();

    public Menu(Skin skin) {
        super(null);
        mSkin = skin;
        mSelectionNinePatch = skin.getAtlas().createPatch("selection");
        mContainer = new Container();
        mContainer.pad(PADDING);
        mContainer.space(PADDING * 2);
        setWidget(mContainer);
    }

    public Actor addButton(String text) {
        return addItem(new ButtonMenuItem(this, text, mSkin));
    }

    public Actor addItem(ButtonMenuItem item) {
        mItems.add(item);

        mContainer.addActor(item.getActor());
        mContainer.setSize(mContainer.getPrefWidth(), mContainer.getPrefHeight());
        setSize(mContainer.getWidth(), mContainer.getHeight());
        if (mCurrentIndex == -1) {
            mCurrentIndex = mItems.size - 1;
        }
        return item.getActor();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            adjustIndex(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            adjustIndex(-1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            triggerCurrentItem();
        }
    }

    void setCurrentItem(MenuItem item) {
        if (item == null) {
            mCurrentIndex = -1;
            return;
        }
        mCurrentIndex = mItems.indexOf(item, /* identity= */ true);
        Assert.check(mCurrentIndex != -1, "Invalid item");
    }

    private void adjustIndex(int delta) {
        mCurrentIndex = MathUtils.clamp(mCurrentIndex + delta, 0, mItems.size - 1);
    }

    private void triggerCurrentItem() {
        if (mCurrentIndex < 0) {
            return;
        }
        mItems.get(mCurrentIndex).trigger();
    }

    private Actor getCurrentActor() {
        if (mCurrentIndex < 0) {
            return null;
        }
        return mItems.get(mCurrentIndex).getActor();
    }

    private void drawSelection(Batch batch, float parentAlpha, float x, float y, float width, float height) {
        mSelectionNinePatch.draw(batch, x - PADDING, y - PADDING, width + 2 * PADDING, height + 2 * PADDING);
    }
}
