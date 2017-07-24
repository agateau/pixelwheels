package com.agateau.ui.gallery;

import com.agateau.ui.ButtonMenuItem;
import com.agateau.ui.Menu;
import com.agateau.utils.StageScreen;
import com.agateau.utils.anchor.Anchor;
import com.agateau.utils.anchor.AnchorGroup;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

/**
 * The main screen of the gallery
 */

class GalleryScreen extends StageScreen {
    private TextureAtlas mAtlas;
    private Skin mSkin;

    GalleryScreen() {
        super(new ScalingViewport(Scaling.fit, 800, 480));
        loadSkin();
        setupUi();
    }

    private void loadSkin() {
        mAtlas = new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
        mSkin = new Skin(Gdx.files.internal("ui/uiskin.json"), mAtlas);
    }

    private void setupUi() {
        AnchorGroup root = new AnchorGroup();
        getStage().addActor(root);
        root.setFillParent(true);

        TextButton button = new TextButton("x", mSkin);
        root.addPositionRule(button, Anchor.TOP_RIGHT, root, Anchor.TOP_RIGHT);

        Menu menu = new Menu(mSkin);
        menu.addButton("Button A").addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                NLog.e("Button A clicked");
            }
        });
        menu.addButton("Button B").addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                NLog.e("Button B clicked");
            }
        });
        root.addPositionRule(menu, Anchor.CENTER, root, Anchor.CENTER);
    }
}
