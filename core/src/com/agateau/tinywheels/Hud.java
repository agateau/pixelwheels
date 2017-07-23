package com.agateau.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.agateau.utils.anchor.AnchorGroup;

/**
 * Hud showing player info during race
 */
class Hud {
    private final static float BUTTON_SIZE_CM = 1.5f;

    private final float BUTTON_SIZE_PX;

    private AnchorGroup mRoot;
    private float mZoom;

    public Hud(Assets assets, Stage stage) {
        mRoot = new AnchorGroup();

        BUTTON_SIZE_PX = assets.findRegion("hud-action").getRegionWidth();
        stage.addActor(mRoot);
    }

    public AnchorGroup getRoot() {
        return mRoot;
    }

    public void act(float delta) {
        updateZoom();
    }

    public void setScreenRect(int x, int y, int width, int height) {
        mRoot.setBounds(x, y, width, height);
    }

    public float getZoom() {
        return mZoom;
    }

    private void updateZoom() {
        float ppc = (Gdx.graphics.getPpcX() + Gdx.graphics.getPpcY()) / 2;
        float pxSize = BUTTON_SIZE_CM * ppc;
        float stageSize = pxSize * mRoot.getStage().getWidth() / Gdx.graphics.getWidth();

        float regionSize = BUTTON_SIZE_PX;
        if (stageSize < regionSize) {
            stageSize = regionSize;
        }

        mZoom = MathUtils.floor(stageSize / regionSize);
    }
}
