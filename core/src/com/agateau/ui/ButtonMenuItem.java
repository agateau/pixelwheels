package com.agateau.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pools;

/**
 * A clickable menu item
 */
public class ButtonMenuItem extends Label implements MenuItem {
    private final Menu mMenu;
    private final Rectangle mRect = new Rectangle();

    public ButtonMenuItem(Menu menu, String text, Skin skin) {
        super(text, skin);
        mMenu = menu;

        addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                mMenu.setCurrentItem(ButtonMenuItem.this);
                onTriggered();
            }
        });
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void onTriggered() {
        ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
        fire(changeEvent);
        Pools.free(changeEvent);
    }

    @Override
    public void onLeftPressed() {

    }

    @Override
    public void onRightPressed() {

    }

    @Override
    public Rectangle getFocusRectangle() {
        mRect.x = getX();
        mRect.y = getY();
        mRect.width = getWidth();
        mRect.height = getHeight();
        return mRect;
    }
}
