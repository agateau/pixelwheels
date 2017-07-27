package com.agateau.ui.gallery;

import com.agateau.ui.GridMenuItem;
import com.agateau.ui.Menu;
import com.agateau.ui.StageScreen;
import com.agateau.ui.TextureRegionItemRenderer;
import com.agateau.ui.TextureRegionItemRendererAdapter;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.anchor.SizeRule;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
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

        final GridMenuItem<TextureRegion> gridMenuItem = createGridMenuItem(menu);
        menu.addItem(gridMenuItem);
        menu.addButton("Add column").addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gridMenuItem.setColumnCount(gridMenuItem.getColumnCount() + 1);
            }
        });

        menu.addButton("Quit").addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        for (int i = 0; i < 100; ++i) {
            menu.addButton(String.format("Dummy %d", i));
        }
        root.addSizeRule(menu, root, SizeRule.IGNORE, 1);
        root.addPositionRule(menu, Anchor.TOP_CENTER, root, Anchor.TOP_CENTER);
    }

    private GridMenuItem<TextureRegion> createGridMenuItem(Menu menu) {
        Array<TextureRegion> items = new Array<TextureRegion>();
        items.add(mAtlas.findRegion("icon-back"));
        items.add(mAtlas.findRegion("icon-restart"));
        items.add(mAtlas.findRegion("rectbutton"));
        items.add(mAtlas.findRegion("icon-left"));
        items.add(mAtlas.findRegion("icon-right"));
        items.add(mAtlas.findRegion("checkbox-off"));
        items.add(mAtlas.findRegion("icon-config"));
        items.add(mAtlas.findRegion("icon-debug"));

        GridMenuItem<TextureRegion> gridMenuItem = new GridMenuItem<TextureRegion>(menu);
        gridMenuItem.setItemSize(70, 80);
        gridMenuItem.setItemRenderer(new TextureRegionItemRenderer());
        gridMenuItem.setItems(items);
        return gridMenuItem;
    }
}
