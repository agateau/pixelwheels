package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Indicate an input zone on the hud
 */
public class InputHudIndicator extends Actor {
    private final static float INDICATOR_OPACITY = 0.6f;
    private final static float INDICATOR_SIZE_CM = 2;

    private final TextureRegion[] mIcons = new TextureRegion[2];

    private boolean mIsPressed = false;

    /**
     * name is a string like "left" or "right"
     */
    public InputHudIndicator(Assets assets, String name) {
        mIcons[0] = assets.findRegion("hud-" + name);
        mIcons[1] = assets.findRegion("hud-" + name + "-down");

        float ppc = (Gdx.graphics.getPpcX() + Gdx.graphics.getPpcY()) / 2;
        float pxSize = INDICATOR_SIZE_CM * ppc;

        setWidth(pxSize);
        setHeight(pxSize);
    }

    void setPressed(boolean isPressed) {
        mIsPressed = isPressed;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        TextureRegion icon = mIcons[mIsPressed ? 1 : 0];
        Color color = batch.getColor();

        float w = icon.getRegionWidth();
        float h = icon.getRegionHeight();
        float zoom = MathUtils.floor(getWidth() / w);
        float oldA = color.a;
        color.a = alpha * INDICATOR_OPACITY;
        batch.setColor(color);
        batch.draw(
                icon,
                MathUtils.round(getX() + (getWidth() - w) / 2 / zoom),
                MathUtils.round(getY() + (getHeight() - h) / 2 / zoom),
                0, 0,
                w, h,
                zoom, zoom,
                0 // rotation
        );
        color.a = oldA;
        batch.setColor(color);
    }
}
