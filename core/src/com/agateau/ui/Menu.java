package com.agateau.ui;

import com.agateau.utils.Assert;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

/**
 * A keyboard and game controller friendly menu system
 */

public class Menu extends ScrollPane {
    private static final float SELECTION_ANIMATION_DURATION = 0.1f;
    private static final int PADDING = 8;
    private final Image mSelectionImage;
    private final Group mPaneWidget;
    private final VerticalGroup mContainer;
    private final Skin mSkin;
    private int mCurrentIndex = -1;

    private final Array<MenuItem> mItems = new Array<MenuItem>();

    public Menu(Skin skin) {
        super(null);
        mSkin = skin;

        NinePatch patch = skin.getAtlas().createPatch("selection");
        mSelectionImage = new Image(patch);

        mPaneWidget = new Group();

        mContainer = new VerticalGroup();
        mContainer.pad(PADDING);
        mContainer.space(PADDING * 2);

        mPaneWidget.addActor(mContainer);
        mPaneWidget.addActor(mSelectionImage);

        setWidget(mPaneWidget);
    }

    public Actor addButton(String text) {
        return addItem(new ButtonMenuItem(this, text, mSkin));
    }

    public Actor addItem(ButtonMenuItem item) {
        mItems.add(item);
        if (mCurrentIndex == -1) {
            mCurrentIndex = mItems.size - 1;
        }

        mContainer.addActor(item.getActor());

        float width = mContainer.getPrefWidth();
        float height = mContainer.getPrefHeight();

        mContainer.setSize(width, height);
        mPaneWidget.setSize(width, height);
        setSize(width, height);

        return item.getActor();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateSelection();
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            adjustIndex(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            adjustIndex(-1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            triggerCurrentItem();
        }
    }

    public void setCurrentItem(MenuItem item) {
        if (item == null) {
            setCurrentIndex(-1);
            return;
        }
        int index = mItems.indexOf(item, /* identity= */ true);
        Assert.check(index != -1, "Invalid item");
        setCurrentIndex(index);
    }

    public MenuItem getCurrentItem() {
        return mCurrentIndex >= 0 ? mItems.get(mCurrentIndex) : null;
    }

    private void adjustIndex(int delta) {
        setCurrentIndex(MathUtils.clamp(mCurrentIndex + delta, 0, mItems.size - 1));
    }

    private void triggerCurrentItem() {
        if (mCurrentIndex < 0) {
            return;
        }
        mItems.get(mCurrentIndex).trigger();
    }

    private void setCurrentIndex(int index) {
        if (index == mCurrentIndex) {
            return;
        }
        int old = mCurrentIndex;
        mCurrentIndex = index;
        if (old >= 0 && mCurrentIndex == -1) {
            mSelectionImage.addAction(Actions.fadeOut(SELECTION_ANIMATION_DURATION));
            return;
        }
        if (old == -1) {
            Rectangle rect = getCurrentItem().getFocusRectangle();
            mSelectionImage.setPosition(rect.x - PADDING, rect.y - PADDING);
            mSelectionImage.setSize(rect.width + 2 * PADDING, rect.height + 2 * PADDING);
            mSelectionImage.addAction(Actions.fadeIn(SELECTION_ANIMATION_DURATION));
            ensureItemVisible();
        } else {
            updateSelection();
        }
    }

    void updateSelection() {
        MenuItem item = getCurrentItem();
        if (item == null) {
            return;
        }
        Rectangle rect = item.getFocusRectangle();
        mSelectionImage.addAction(Actions.moveTo(rect.x - PADDING, rect.y - PADDING, SELECTION_ANIMATION_DURATION));
        mSelectionImage.addAction(Actions.sizeTo(rect.width + 2 * PADDING, rect.height + 2 * PADDING, SELECTION_ANIMATION_DURATION));
        ensureItemVisible();
    }

    private void ensureItemVisible() {
        MenuItem item = getCurrentItem();
        Rectangle rect = item.getFocusRectangle();
        scrollTo(rect.x - PADDING, rect.y - PADDING, rect.width + 2 * PADDING, rect.height + 2 * PADDING);
    }
}
