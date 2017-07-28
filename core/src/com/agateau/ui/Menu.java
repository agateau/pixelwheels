package com.agateau.ui;

import com.agateau.utils.Assert;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

/**
 * A keyboard and game controller friendly menu system
 */

public class Menu extends ScrollPane {
    private static final float SELECTION_ANIMATION_DURATION = 0.2f;
    private static final int PADDING = 8;
    private final MenuInputHandler mMenuInputHandler = new MenuInputHandler();
    private final Image mFocusIndicator;
    private final Group mPaneWidget;
    private final VerticalGroup mContainer;
    private final Skin mSkin;
    private MenuStyle mStyle;
    private int mCurrentIndex = -1;

    private final Array<MenuItem> mItems = new Array<MenuItem>();

    public static class MenuStyle {
        public Drawable focus;

        public MenuStyle() {
        }
    }

    public Menu(Skin skin) {
        super(null);
        mSkin = skin;
        mStyle = skin.get(MenuStyle.class);

        mFocusIndicator = new Image(mStyle.focus);

        mPaneWidget = new Group();

        mContainer = new VerticalGroup() {
            @Override
            public void layout() {
                super.layout();
                if (mFocusIndicator == null) {
                    return;
                }
                updateFocusIndicatorBounds();
            }
        };
        mContainer.pad(PADDING);
        mContainer.space(PADDING * 2);

        mPaneWidget.addActor(mContainer);
        mPaneWidget.addActor(mFocusIndicator);

        setWidget(mPaneWidget);
    }

    public Skin getSkin() {
        return mSkin;
    }

    public Actor addButton(String text) {
        return addItem(new ButtonMenuItem(this, text, mSkin));
    }

    public Actor addItem(MenuItem item) {
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
        mMenuInputHandler.act(delta);
        if (mMenuInputHandler.isPressed(VirtualKey.DOWN)) {
            if (!getCurrentItem().goDown()) {
                adjustIndex(1);
            }
        } else if (mMenuInputHandler.isPressed(VirtualKey.UP)) {
            if (!getCurrentItem().goUp()) {
                adjustIndex(-1);
            }
        } else if (mMenuInputHandler.isPressed(VirtualKey.LEFT)) {
            getCurrentItem().goLeft();
        } else if (mMenuInputHandler.isPressed(VirtualKey.RIGHT)) {
            getCurrentItem().goRight();
        } else if (mMenuInputHandler.isPressed(VirtualKey.TRIGGER)) {
            triggerCurrentItem();
        }
    }

    public void setKeyMapper(KeyMapper keyMapper) {
        mMenuInputHandler.setKeyMapper(keyMapper);
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
        int old = mCurrentIndex;
        mCurrentIndex = index;
        if (old >= 0 && mCurrentIndex == -1) {
            mFocusIndicator.addAction(Actions.fadeOut(SELECTION_ANIMATION_DURATION));
        } else if (old == -1) {
            Rectangle rect = getCurrentItem().getFocusRectangle();
            updateFocusIndicatorBounds();
            mFocusIndicator.addAction(Actions.fadeIn(SELECTION_ANIMATION_DURATION));
        } else {
            animateFocusIndicator();
        }
    }

    void animateFocusIndicator() {
        MenuItem item = getCurrentItem();
        if (item == null) {
            return;
        }
        Rectangle rect = item.getFocusRectangle();
        mFocusIndicator.addAction(Actions.moveTo(rect.x - PADDING, rect.y - PADDING, SELECTION_ANIMATION_DURATION, Interpolation.pow2Out));
        mFocusIndicator.addAction(Actions.sizeTo(rect.width + 2 * PADDING, rect.height + 2 * PADDING, SELECTION_ANIMATION_DURATION, Interpolation.pow2Out));
        ensureItemVisible();
    }

    private void updateFocusIndicatorBounds() {
        Rectangle rect = getCurrentItem().getFocusRectangle();
        mFocusIndicator.setBounds(rect.x - PADDING, rect.y - PADDING, rect.width + 2 * PADDING, rect.height + 2 * PADDING);
        ensureItemVisible();
    }

    private void ensureItemVisible() {
        MenuItem item = getCurrentItem();
        Rectangle rect = item.getFocusRectangle();
        scrollTo(rect.x - PADDING, rect.y - PADDING, rect.width + 2 * PADDING, rect.height + 2 * PADDING);
    }
}
