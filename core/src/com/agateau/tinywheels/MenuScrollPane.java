package com.agateau.tinywheels;

import com.agateau.ui.Menu;
import com.agateau.ui.MenuItem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * A ScrollPane which can track the current item of a menu
 */
public class MenuScrollPane extends ScrollPane {
    private Menu mMenu;
    private final Vector2 mTmp = new Vector2();

    private ChangeListener mListener = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            ensureItemVisible();
        }
    };

    public MenuScrollPane(Menu menu) {
        super(null);
        setMenu(menu);
    }

    public void setMenu(Menu menu) {
        setWidget(menu);
        mMenu = menu;
        mMenu.addListener(mListener);
    }

    public float getPrefWidth() {
        return mMenu.getWidth();
    }

    private void ensureItemVisible() {
        Menu.MenuStyle style = mMenu.getMenuStyle();

        MenuItem item = mMenu.getCurrentItem();
        Rectangle rect = item.getFocusRectangle();
        mapDescendantRectangle(item.getActor(), rect);
        scrollTo(rect.x - style.focusPadding, rect.y - style.focusPadding, rect.width + 2 * style.focusPadding, rect.height + 2 * style.focusPadding);
    }

    // TODO: Duplicated in Menu: factorize
    private void mapDescendantRectangle(Actor actor, Rectangle rect) {
        mTmp.set(rect.x, rect.y);
        actor.localToAscendantCoordinates(mMenu, mTmp);
        rect.x = mTmp.x;
        rect.y = mTmp.y;
    }
}
